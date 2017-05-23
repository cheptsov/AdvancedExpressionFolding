package com.intellij.advancedExpressionFolding;

import com.intellij.codeInsight.hint.DocumentFragmentTooltipRenderer;
import com.intellij.codeInsight.hint.TooltipController;
import com.intellij.codeInsight.hint.TooltipGroup;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.FoldingListener;
import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.editor.impl.FoldingModelImpl;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AdvancedExpressionFoldingHighlightingComponent extends AbstractProjectComponent {

    private static final TooltipGroup FOLDING_TOOLTIP_GROUP = new TooltipGroup("FOLDING_TOOLTIP_GROUP", 10);
    private FileEditorManagerListener editorManagerListener;
    private Map<FoldRegion, RangeHighlighter> highlighters = new HashMap<>();
    private Map<FoldRegion, EditorMouseMotionListener> motionListeners = new HashMap<>();
    private Map<FoldRegion, EditorMouseListener> mouseListeners = new HashMap<>();
    private TooltipController controller;

    protected AdvancedExpressionFoldingHighlightingComponent(Project project) {
        super(project);
        this.controller = TooltipController.getInstance();
    }

    @Override
    public void projectOpened() {
        FileEditor[] editors = FileEditorManager.getInstance(myProject).getAllEditors();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(myProject);
        processEditors(editors, documentManager);

        editorManagerListener = new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                FileEditor[] editors = source.getEditors(file);
                processEditors(editors, documentManager);
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                /*FileEditor[] editors = source.getEditors(file);*/
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {

            }
        };
        FileEditorManager.getInstance(myProject).addFileEditorManagerListener(editorManagerListener, myProject);
    }

    protected void processEditors(FileEditor[] editors, PsiDocumentManager documentManager) {
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
    }

    private static EditorEx getEditorEx(FileEditor fileEditor) {
        Editor editor = fileEditor instanceof TextEditor ? ((TextEditor)fileEditor).getEditor() : null;
        return editor instanceof EditorEx ? (EditorEx)editor : null;
    }

    private void processRegion(@NotNull FoldRegion region, PsiDocumentManager documentManager, EditorEx editorEx) {
        FoldingGroup group = region.getGroup();
        if (group != null && group.toString().endsWith(HighlightingExpression.GROUP_POSTFIX) && region.isValid()) {
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
                        EditorMouseMotionListener m = motionListeners.get(region);
                        if (m != null) {
                            editorEx.removeEditorMouseMotionListener(m);
                        }
                        EditorMouseMotionListener motionListener = new EditorMouseMotionAdapter() {
                            @Override
                            public void mouseMoved(EditorMouseEvent e) {
                                if (e.getArea() == EditorMouseEventArea.EDITING_AREA) {
                                    VisualPosition visualPosition = editorEx.xyToVisualPosition(e.getMouseEvent().getPoint());
                                    int mouseOffset = editorEx.logicalPositionToOffset(editorEx.visualToLogicalPosition(visualPosition));
                                    if (!DumbService.isDumb(myProject)) {
                                        try {
                                            Expression expr = findHighlightingExpression(psiFile, editorEx.getDocument(), mouseOffset);
                                            if (expr != null) {
                                                if (expression.getElement().getTextRange().contains(mouseOffset) && !region.isExpanded() && region.isValid()) {
                                                    DocumentFragment range = createDocumentFragment(editorEx, region);
                                                    final Point p = SwingUtilities.convertPoint((Component) e.getMouseEvent().getSource(), e.getMouseEvent().getPoint(),
                                                            editorEx.getComponent().getRootPane().getLayeredPane());
                                                    controller.showTooltip(editorEx, p, new DocumentFragmentTooltipRenderer(range), false, FOLDING_TOOLTIP_GROUP);
                                                }
                                            } else {
                                                controller.cancelTooltip(FOLDING_TOOLTIP_GROUP, e.getMouseEvent(), true);
                                            }
                                        } catch (IndexNotReadyException t) {
                                            controller.cancelTooltip(FOLDING_TOOLTIP_GROUP, e.getMouseEvent(), true);
                                        }
                                    } else {
                                        controller.cancelTooltip(FOLDING_TOOLTIP_GROUP, e.getMouseEvent(), true);
                                    }
                                } else {
                                    controller.cancelTooltip(FOLDING_TOOLTIP_GROUP, e.getMouseEvent(), true);
                                }
                            }
                        };
                        editorEx.addEditorMouseMotionListener(motionListener);
                        motionListeners.put(region, motionListener);
                        EditorMouseListener n = mouseListeners.get(region);
                        if (n != null) {
                            editorEx.removeEditorMouseListener(n);
                        }
                        EditorMouseListener mouseListener = new EditorMouseAdapter() {
                            @Override
                            public void mouseClicked(EditorMouseEvent e) {
                                if (e.getArea() == EditorMouseEventArea.EDITING_AREA) {
                                    VisualPosition visualPosition = editorEx.xyToVisualPosition(e.getMouseEvent().getPoint());
                                    int mouseOffset = editorEx.logicalPositionToOffset(editorEx.visualToLogicalPosition(visualPosition));
                                    if (expression.getElement().getTextRange().contains(mouseOffset) && !region.isExpanded()) {
                                        editorEx.getFoldingModel().runBatchFoldingOperation(() ->
                                                region.setExpanded(true));
                                    }
                                }
                            }
                        };
                        editorEx.addEditorMouseListener(mouseListener);
                        mouseListeners.put(region, n);
                    } else {
                        RangeHighlighter highlighter = highlighters.remove(region);
                        if (highlighter != null) {
                            editorEx.getMarkupModel().removeHighlighter(highlighter);
                        }
                        EditorMouseMotionListener m = motionListeners.remove(region);
                        if (m != null) {
                            editorEx.removeEditorMouseMotionListener(m);
                        }
                        EditorMouseListener n = mouseListeners.remove(region);
                        if (n != null) {
                            editorEx.removeEditorMouseListener(n);
                        }
                    }
                }
            }
        }
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

    private Expression findHighlightingExpression(PsiFile psiFile, Document document, int offset) {
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
}
