package com.intellij.advancedExpressionFolding;

import com.intellij.codeInsight.hint.DocumentFragmentTooltipRenderer;
import com.intellij.codeInsight.hint.TooltipController;
import com.intellij.codeInsight.hint.TooltipGroup;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseEventArea;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.FoldingListener;
import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.editor.impl.FoldingModelImpl;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AdvancedExpressionFoldingHighlightingComponent extends AbstractProjectComponent implements EditorMouseListener, EditorMouseMotionListener, FileEditorManagerListener {

    private static final TooltipGroup FOLDING_TOOLTIP_GROUP = new TooltipGroup("FOLDING_TOOLTIP_GROUP", 10);
    private FileEditorManagerListener editorManagerListener;
    private Map<FoldRegion, RangeHighlighter> highlighters = new HashMap<>(); // TODO: Make sure there is no memory leak
    private TooltipController controller;

    protected AdvancedExpressionFoldingHighlightingComponent(Project project, EditorFactory editorFactory) {
        super(project);
        this.controller = TooltipController.getInstance();
        editorFactory.getEventMulticaster().addEditorMouseMotionListener(this, project);
        editorFactory.getEventMulticaster().addEditorMouseListener(this, project);
    }

    @Override
    public void projectOpened() {
        FileEditor[] editors = FileEditorManager.getInstance(myProject).getAllEditors();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(myProject);
        processEditors(editors, documentManager);

        final MessageBusConnection connection = myProject.getMessageBus().connect(myProject);
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);
    }

    protected void processEditors(FileEditor[] editors, PsiDocumentManager documentManager) {
        try {
            for (FileEditor editor : editors) {
                EditorEx editorEx = getEditorEx(editor);
                if (editorEx != null) {
                    for (FoldRegion region : editorEx.getFoldingModel().getAllFoldRegions()) {
                        processRegion(region, documentManager, editorEx);
                    }
                    FoldingListener foldingListener = new FoldingListener() {
                        @Override
                        public void onFoldRegionStateChange(@NotNull FoldRegion region) {
                            processRegion(region, documentManager, editorEx);
                        }

                        @Override
                        public void onFoldProcessingEnd() {

                        }
                    };
                    editorEx.getFoldingModel().addListener(foldingListener, editor);
                }
            }
        } catch (IndexNotReadyException e) {
            // ignore
        }
    }

    private static EditorEx getEditorEx(FileEditor fileEditor) {
        Editor editor = fileEditor instanceof TextEditor ? ((TextEditor)fileEditor).getEditor() : null;
        return editor instanceof EditorEx ? (EditorEx)editor : null;
    }

    private void processRegion(@NotNull FoldRegion region, PsiDocumentManager documentManager, EditorEx editorEx) throws IndexNotReadyException {
        if (isHighlightingRegion(region)) {
            PsiFile psiFile = documentManager.getPsiFile(editorEx.getDocument());
            PsiElement element = null;
            if (psiFile != null) {
                element = psiFile.findElementAt(region.getStartOffset());
            }
            if (element != null) {
                Expression expression = findHighlightingExpression(psiFile, region.getDocument(), region.getStartOffset());
                if (expression != null) {
                    TextAttributes foldedTextAttributes = editorEx.getColorsScheme().getAttributes(EditorColors.FOLDED_TEXT_ATTRIBUTES);
                    if (foldedTextAttributes.getBackgroundColor() != null) {
                        foldedTextAttributes.setForegroundColor(null);
                    }
                    foldedTextAttributes.setFontType(Font.PLAIN);
                    if (!region.isExpanded()) {
                        RangeHighlighter h = highlighters.remove(region);
                        if (h != null) {
                            editorEx.getMarkupModel().removeHighlighter(h);
                        }
                        RangeHighlighterEx highlighter = (RangeHighlighterEx) editorEx.getMarkupModel().addRangeHighlighter(expression.getElement().getTextRange().getStartOffset(),
                                expression.getElement().getTextRange().getEndOffset(), HighlighterLayer.WARNING - 1, foldedTextAttributes, HighlighterTargetArea.EXACT_RANGE);
                        highlighter.setAfterEndOfLine(false);
                        highlighters.put(region, highlighter);
                    } else {
                        RangeHighlighter highlighter = highlighters.remove(region);
                        if (highlighter != null) {
                            editorEx.getMarkupModel().removeHighlighter(highlighter);
                        }
                    }
                }
            }
        }
    }

    private boolean isHighlightingRegion(@NotNull FoldRegion region) {
        return region.getGroup() != null && region.getGroup().toString().endsWith(HighlightingExpression.GROUP_POSTFIX) && region.isValid();
    }

    private DocumentFragment createDocumentFragment(EditorEx editorEx, FoldRegion fold) {
        final FoldingGroup group = fold.getGroup();
        final int foldStart = fold.getStartOffset();
        if (group != null) {
            final int endOffset = ((FoldingModelImpl) editorEx.getFoldingModel()).getEndOffset(group);
            return new DocumentFragment(editorEx.getDocument(), foldStart, endOffset);
        }

        final int oldEnd = fold.getEndOffset();
        return new DocumentFragment(editorEx.getDocument(), foldStart, oldEnd);
    }

    private Expression findHighlightingExpression(PsiFile psiFile, Document document, int offset) throws IndexNotReadyException {
        PsiElement element = psiFile.findElementAt(offset);
        if (element != null) {
            Expression expression;
            int count = 0;
            while (count++ < 10 && element != null) {
                expression = AdvancedExpressionFoldingBuilder.getNonSyntheticExpression(element, document);
                if (expression instanceof HighlightingExpression) {
                    return expression;
                }
                element = element.getParent();
            }
        }
        return null;
    }

    @Override
    public void projectClosed() {
        FileEditorManager.getInstance(myProject).removeFileEditorManagerListener(editorManagerListener);
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getName();
    }

    @Override
    public void mousePressed(EditorMouseEvent e) {

    }

    @Override
    public void mouseClicked(EditorMouseEvent e) {
        if (e.getArea() == EditorMouseEventArea.EDITING_AREA) {
            EditorEx editorEx = e.getEditor() instanceof EditorEx ? ((EditorEx) e.getEditor()) : null;
            VisualPosition visualPosition = editorEx.xyToVisualPosition(e.getMouseEvent().getPoint());
            int offset = editorEx.logicalPositionToOffset(editorEx.visualToLogicalPosition(visualPosition));
            try {
                Expression expr = findHighlightingExpression(PsiDocumentManager.getInstance(myProject).getPsiFile(editorEx.getDocument()),
                        editorEx.getDocument(), offset);
                if (expr != null) {
                    for (FoldRegion region : editorEx.getFoldingModel().getAllFoldRegions()) {
                        if (expr.getTextRange().getStartOffset() <= region.getStartOffset()
                                && region.getEndOffset() <= expr.getTextRange().getEndOffset() && isHighlightingRegion(region) && !region.isExpanded()) {
                            editorEx.getFoldingModel().runBatchFoldingOperation(() -> region.setExpanded(true));
                        }
                    }
                }
            } catch (IndexNotReadyException ignored) {
            }
        }
    }

    @Override
    public void mouseReleased(EditorMouseEvent e) {

    }

    @Override
    public void mouseEntered(EditorMouseEvent e) {

    }

    @Override
    public void mouseExited(EditorMouseEvent e) {

    }

    @Override
    public void mouseMoved(EditorMouseEvent e) {
        if (!DumbService.isDumb(myProject) && e.getArea() == EditorMouseEventArea.EDITING_AREA) {
            EditorEx editorEx = e.getEditor() instanceof EditorEx ? ((EditorEx) e.getEditor()) : null;
            if (editorEx != null) {
                VisualPosition visualPosition = editorEx.xyToVisualPosition(e.getMouseEvent().getPoint());
                int offset = editorEx.logicalPositionToOffset(editorEx.visualToLogicalPosition(visualPosition));
                try {
                    Expression expr = findHighlightingExpression(PsiDocumentManager.getInstance(myProject).getPsiFile(editorEx.getDocument()),
                            editorEx.getDocument(), offset);
                    if (expr != null) {
                        for (FoldRegion region : editorEx.getFoldingModel().getAllFoldRegions()) {
                            if (expr.getTextRange().getStartOffset() <= region.getStartOffset()
                                    && region.getEndOffset() <= expr.getTextRange().getEndOffset() && isHighlightingRegion(region) && !region.isExpanded()) {
                                DocumentFragment range = createDocumentFragment(editorEx, region);
                                final Point p = SwingUtilities.convertPoint((Component) e.getMouseEvent().getSource(), e.getMouseEvent().getPoint(),
                                        editorEx.getComponent().getRootPane().getLayeredPane());
                                controller.showTooltip(editorEx, p, new DocumentFragmentTooltipRenderer(range), false, FOLDING_TOOLTIP_GROUP);
                                return;
                            }
                        }
                    }
                } catch (IndexNotReadyException ignored) {
                }
            }
        }
        controller.cancelTooltip(FOLDING_TOOLTIP_GROUP, e.getMouseEvent(), true);
    }

    @Override
    public void mouseDragged(EditorMouseEvent e) {

    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(myProject);
        FileEditor[] editors = source.getEditors(file);
        processEditors(editors, documentManager);
    }
}
