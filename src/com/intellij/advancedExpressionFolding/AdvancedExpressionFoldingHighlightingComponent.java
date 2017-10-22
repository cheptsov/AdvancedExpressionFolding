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
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// TODO: Support multi-line range highlighters
public class AdvancedExpressionFoldingHighlightingComponent extends AbstractProjectComponent implements EditorMouseListener, EditorMouseMotionListener, FileEditorManagerListener {

    private static final TooltipGroup FOLDING_TOOLTIP_GROUP = new TooltipGroup("FOLDING_TOOLTIP_GROUP", 10);
    private Map<Editor, Map<Expression, RangeHighlighterEx>> highlighters = new HashMap<>();
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

    private void processEditors(FileEditor[] editors, PsiDocumentManager documentManager) {
        try {
            for (FileEditor editor : editors) {
                EditorEx editorEx = getEditorEx(editor);
                if (editorEx != null) {
                    highlighters.putIfAbsent(editorEx, new HashMap<>());
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

    private void processRegion(@NotNull FoldRegion region, @NotNull PsiDocumentManager documentManager, @NotNull EditorEx editorEx) throws IndexNotReadyException {
        if (isHighlightingRegion(region)) {
            @Nullable PsiFile psiFile = documentManager.getPsiFile(editorEx.getDocument());
            if (psiFile != null) {
                @Nullable PsiElement element = psiFile.findElementAt(region.getStartOffset());
                if (element != null) {
                    @Nullable Expression expression = findHighlightingExpression(psiFile, region.getDocument(), region.getStartOffset());
                    if (expression != null) {
                        Map<Expression, RangeHighlighterEx> m = highlighters.get(editorEx);
                        if (!region.isExpanded()) {
                            TextAttributes highlightedTextAttributes = getHighlightedTextAttributes(editorEx);
                            RangeHighlighterEx highlighter = m.get(expression);
                            if (highlighter == null) {
                                TextRange htr = expression.getHighlightedTextRange();
                                highlighter = (RangeHighlighterEx) editorEx.getMarkupModel().addRangeHighlighter(htr.getStartOffset(),
                                        htr.getEndOffset(), HighlighterLayer.WARNING - 1, highlightedTextAttributes, HighlighterTargetArea.EXACT_RANGE);
                                highlighter.setAfterEndOfLine(false);
                                m.put(expression, highlighter);
                            }
                        } else {
                            RangeHighlighterEx __highlighter = m.remove(expression);
                            if (__highlighter != null) {
                                editorEx.getMarkupModel().removeHighlighter(__highlighter);
                            }
                        }
                    }
                }

            }
        }
    }

    @NotNull
    private TextAttributes getHighlightedTextAttributes(@NotNull EditorEx editorEx) {
        TextAttributes foldedTextAttributes = editorEx.getColorsScheme().getAttributes(EditorColors.FOLDED_TEXT_ATTRIBUTES);
        if (foldedTextAttributes.getBackgroundColor() != null) {
            foldedTextAttributes.setForegroundColor(null);
        }
        foldedTextAttributes.setFontType(Font.PLAIN);
        return foldedTextAttributes;
    }

    private boolean isHighlightingRegion(@NotNull FoldRegion region) {
        return region.getGroup() != null && region.getGroup().toString().endsWith(Expression.HIGHLIGHTED_GROUP_POSTFIX) && region.isValid();
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

    private Expression findHighlightingExpression(@NotNull PsiFile psiFile, @NotNull Document document, int offset) throws IndexNotReadyException {
        @Nullable PsiElement element = psiFile.findElementAt(offset);
        if (element != null) {
            @Nullable Expression expression;
            int count = 0;
            while (count++ < 10 && element != null) {
                expression = AdvancedExpressionFoldingBuilder.getNonSyntheticExpression(element, document);
                if (expression != null && expression.isHighlighted()) {
                    return expression;
                }
                element = element.getParent();
            }
        }
        return null;
    }

    @Override
    public void projectClosed() {
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
            @Nullable EditorEx editorEx = e.getEditor() instanceof EditorEx ? ((EditorEx) e.getEditor()) : null;
            if (editorEx != null) {
                @NotNull VisualPosition visualPosition = editorEx.xyToVisualPosition(e.getMouseEvent().getPoint());
                int offset = editorEx.logicalPositionToOffset(editorEx.visualToLogicalPosition(visualPosition));
                try {
                    @Nullable PsiFile psiFile = PsiDocumentManager.getInstance(myProject).getPsiFile(editorEx.getDocument());
                    if (psiFile != null) {
                        @Nullable Expression expression = findHighlightingExpression(psiFile, editorEx.getDocument(), offset);
                        if (expression != null) {
                            TextRange htr = expression.getHighlightedTextRange();
                            if (htr.contains(offset)) {
                                for (FoldRegion region : editorEx.getFoldingModel().getAllFoldRegions()) {
                                    if (htr.getStartOffset() <= region.getStartOffset()
                                            && region.getEndOffset() <= htr.getEndOffset()
                                            && isHighlightingRegion(region)
                                            && !region.isExpanded()) {
                                        editorEx.getFoldingModel()
                                                .runBatchFoldingOperation(() -> region.setExpanded(true));
                                    }
                                }
                            }
                        }
                    }
                } catch (IndexNotReadyException ignored) {
                }
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
            @Nullable EditorEx editorEx = e.getEditor() instanceof EditorEx ? ((EditorEx) e.getEditor()) : null;
            if (editorEx != null) {
                @NotNull VisualPosition visualPosition = editorEx.xyToVisualPosition(e.getMouseEvent().getPoint());
                int offset = editorEx.logicalPositionToOffset(editorEx.visualToLogicalPosition(visualPosition));
                try {
                    @Nullable PsiFile psiFile = PsiDocumentManager.getInstance(myProject).getPsiFile(editorEx.getDocument());
                    if (psiFile != null) {
                        @Nullable Expression expression = findHighlightingExpression(psiFile, editorEx.getDocument(), offset);
                        if (expression != null) {
                            TextRange htr = expression.getHighlightedTextRange();
                            if (htr.contains(offset)) {
                                for (FoldRegion region : editorEx.getFoldingModel().getAllFoldRegions()) {
                                    if (htr.getStartOffset() <= region.getStartOffset()
                                            && region.getEndOffset() <= htr.getEndOffset()
                                            && isHighlightingRegion(region)
                                            && !region.isExpanded()) {
                                        // TODO: Sometimes the popup doesn't show, see TypeCastTestData.java
                                        DocumentFragment range = createDocumentFragment(editorEx, region);
                                        final Point p = SwingUtilities
                                                .convertPoint((Component) e.getMouseEvent().getSource(),
                                                        e.getMouseEvent().getPoint(),
                                                        editorEx.getComponent().getRootPane().getLayeredPane());
                                        controller.showTooltip(editorEx, p, new DocumentFragmentTooltipRenderer(range),
                                                false, FOLDING_TOOLTIP_GROUP);
                                        return;
                                    }
                                }
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
    public void fileOpened(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile file) {
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(myProject);
        FileEditor[] editors = fileEditorManager.getEditors(file);
        processEditors(editors, documentManager);
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
        FileEditor[] editors = fileEditorManager.getEditors(virtualFile);
        for (FileEditor editor : editors) {
            EditorEx editorEx = getEditorEx(editor);
            if (editorEx != null) {
                Map<Expression, RangeHighlighterEx> m = highlighters.remove(editorEx);
                if (m != null) {
                    m.forEach((expression, highlighter) ->
                            editorEx.getMarkupModel().removeHighlighter(highlighter)
                    );
                }
            }
        }
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent fileEditorManagerEvent) {

    }

    public Map<Expression, RangeHighlighterEx> getHighlighters(Editor editor) {
        return highlighters.get(editor);
    }
}
