package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaSdkImpl;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FoldingTest extends LightCodeInsightFixtureTestCase {

    public static final DefaultLightProjectDescriptor TEST_JDK = new DefaultLightProjectDescriptor() {
        public Sdk getSdk() {
            return ((JavaSdkImpl) JavaSdk.getInstance())
                    .createMockJdk("Test JDK", System.getProperty("java.home"), true);
        }
    };

    @Override
    public void setUp() throws Exception {
        super.setUp();
        disableAllFoldings();
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    @NotNull
    protected LightProjectDescriptor getProjectDescriptor() {
        return TEST_JDK;
    }

    public void doFoldingTest() {
        myFixture.testFoldingWithCollapseStatus(getTestDataPath() + "/" + getTestName(false) + ".java");
    }

    @NotNull
    protected List<Map.Entry<Expression, RangeHighlighterEx>> doAdvancedHighlighting() {
        AdvancedExpressionFoldingHighlightingComponent highlightingComponent = getProject()
                .getComponent(AdvancedExpressionFoldingHighlightingComponent.class);
        highlightingComponent.fileOpened(FileEditorManager.getInstance(getProject()), myFixture.getFile().getVirtualFile());
        ArrayList<Map.Entry<Expression, RangeHighlighterEx>> entries = new ArrayList<>(highlightingComponent
                .getHighlighters(myFixture.getEditor())
                .entrySet());
        entries.sort(Comparator.comparingInt(o -> o.getValue().getStartOffset()));
        return entries;
    }

    public void testElvisTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setCheckExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testForRangeTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setRangeExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testStringBuilderTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setConcatenationExpressionsCollapse(true);
        doFoldingTest();
        List<Map.Entry<Expression, RangeHighlighterEx>> entries = doAdvancedHighlighting();
        assertEquals(2, entries.size());
        assertEquals("new StringBuilder().append(\"[\")",
                myFixture.getDocument(getFile()).getText(entries.get(0).getKey().getHighlightedTextRange()));
        assertEquals("sb3.toString()",
                myFixture.getDocument(getFile()).getText(entries.get(1).getKey().getHighlightedTextRange()));
    }

    public void testInterpolatedStringTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setConcatenationExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testConcatenationTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setConcatenationExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testGetSetPutTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setGetExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testSliceTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setSlicingExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testAppendSetterInterpolatedStringTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setConcatenationExpressionsCollapse(true);
        AdvancedExpressionFoldingSettings.getInstance().getState().setGetSetExpressionsCollapse(true);
        doFoldingTest();
        List<Map.Entry<Expression, RangeHighlighterEx>> entries = doAdvancedHighlighting();
        assertEquals(3, entries.size());
        assertEquals("new StringBuilder().append(args[0])",
                myFixture.getDocument(getFile()).getText(entries.get(0).getKey().getHighlightedTextRange()));
        assertEquals("sb1.toString()",
                myFixture.getDocument(getFile()).getText(entries.get(1).getKey().getHighlightedTextRange()));
        assertEquals("sb2.toString()",
                myFixture.getDocument(getFile()).getText(entries.get(2).getKey().getHighlightedTextRange()));
    }

    public void testEqualsCompareTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setComparingExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testTypeCastTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setCastExpressionsCollapse(true);
        doFoldingTest();
        List<Map.Entry<Expression, RangeHighlighterEx>> entries = doAdvancedHighlighting();
        assertEquals(1, entries.size());
        assertEquals("((TypeCastTestData) ((TypeCastTestData) t.getObject()).getObject())", myFixture.getDocument(getFile()).getText(entries.get(0).getKey().getHighlightedTextRange()));
    }

    public void testVarTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setVarExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testGetterSetterTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setGetSetExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testControlFlowSingleStatementTestData() {
        // TODO: Test with different indentation settings
        AdvancedExpressionFoldingSettings.getInstance().getState().setControlFlowSingleStatementCodeBlockCollapse(true);
        doFoldingTest();
    }

    public void testControlFlowMultiStatementTestData() {
        // TODO: Test with different indentation settings
        AdvancedExpressionFoldingSettings.getInstance().getState().setControlFlowMultiStatementCodeBlockCollapse(true);
        doFoldingTest();
    }

    public void testCompactControlFlowTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setCompactControlFlowSyntaxCollapse(true);
        doFoldingTest();
        List<Map.Entry<Expression, RangeHighlighterEx>> entries = doAdvancedHighlighting();
        assertEquals(7, entries.size());
        assertEquals("(args.length > 0)", myFixture.getDocument(getFile()).getText(entries.get(0).getKey().getHighlightedTextRange()));
        assertEquals("(String arg : args)", myFixture.getDocument(getFile()).getText(entries.get(1).getKey().getHighlightedTextRange()));
        assertEquals("(int i = 0; i < args.length; i++)", myFixture.getDocument(getFile()).getText(entries.get(2).getKey().getHighlightedTextRange()));
        assertEquals("(true)", myFixture.getDocument(getFile()).getText(entries.get(3).getKey().getHighlightedTextRange()));
        assertEquals("(true)", myFixture.getDocument(getFile()).getText(entries.get(4).getKey().getHighlightedTextRange()));
        assertEquals("(args.length)", myFixture.getDocument(getFile()).getText(entries.get(5).getKey().getHighlightedTextRange()));
        assertEquals("(Exception e)", myFixture.getDocument(getFile()).getText(entries.get(6).getKey().getHighlightedTextRange()));
    }

    public void testSemicolonTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setSemicolonsCollapse(true);
        doFoldingTest();
    }

    private void disableAllFoldings() {
        // TODO: Find a way to test all folding both together and separately
        AdvancedExpressionFoldingSettings.getInstance().getState().disableAll();
    }
}
