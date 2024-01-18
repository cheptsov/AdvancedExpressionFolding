package com.intellij.advancedExpressionFolding;

import com.intellij.codeInsight.folding.JavaCodeFoldingSettings;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.rt.execution.junit.FileComparisonFailure;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FoldingTest extends LightJavaCodeInsightFixtureTestCase {

    public static final DefaultLightProjectDescriptor TEST_JDK = new DefaultLightProjectDescriptor() {
        public Sdk getSdk() {
            return JavaSdk.getInstance()
                    .createJdk("Test JDK", System.getProperty("java.home"), true);
        }
    };

    private static final String FOLD = "fold";

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

    public void doReadOnlyFoldingTest() {
        testReadOnlyFoldingRegions(getTestDataPath() + "/" + getTestName(false) + ".java",
                null, true);
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

    // TODO: Refactor this mess
    private void testReadOnlyFoldingRegions(@NotNull String verificationFileName,
                                    @Nullable String destinationFileName,
                                    boolean doCheckCollapseStatus) {
        String expectedContent;
        final File verificationFile;
        try {
            verificationFile = new File(verificationFileName);
            expectedContent = FileUtil.loadFile(verificationFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(expectedContent);

        expectedContent = StringUtil.replace(expectedContent, "\r", "");
        final String cleanContent = expectedContent.replaceAll("<" + FOLD + "\\stext=\'[^\']*\'(\\sexpand=\'[^\']*\')*>", "")
                .replace("</" + FOLD + ">", "");
        if (destinationFileName == null) {
            myFixture.configureByText(FileTypeManager.getInstance().getFileTypeByFileName(verificationFileName), cleanContent);
        }
        else {
            try {
                FileUtil.writeToFile(new File(destinationFileName), cleanContent);
                VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(destinationFileName);
                assertNotNull(file);
                myFixture.configureFromExistingVirtualFile(file);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            WriteAction.run(() -> myFixture.getFile().getVirtualFile().setWritable(false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String actual = ((CodeInsightTestFixtureImpl)myFixture).getFoldingDescription(doCheckCollapseStatus);
        if (!expectedContent.equals(actual)) {
            throw new FileComparisonFailure(verificationFile.getName(), expectedContent, actual, verificationFile.getPath());
        }
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
        doReadOnlyFoldingTest();
    }

    public void testControlFlowMultiStatementTestData() {
        // TODO: Test with different indentation settings
        AdvancedExpressionFoldingSettings.getInstance().getState().setControlFlowMultiStatementCodeBlockCollapse(true);
        doReadOnlyFoldingTest();
    }

    public void testLocalDateTestData() {
        AdvancedExpressionFoldingSettings.State state = AdvancedExpressionFoldingSettings.getInstance().getState();
        state.setComparingLocalDatesCollapse(true);
        doReadOnlyFoldingTest();
    }

    public void testLocalDateLiteralTestData() {
        AdvancedExpressionFoldingSettings.State state = AdvancedExpressionFoldingSettings.getInstance().getState();
        state.setLocalDateLiteralCollapse(true);
        doReadOnlyFoldingTest();
    }

    public void testLocalDateLiteralPostfixTestData() {
        AdvancedExpressionFoldingSettings.State state = AdvancedExpressionFoldingSettings.getInstance().getState();
        state.setLocalDateLiteralCollapse(true);
        state.setLocalDateLiteralPostfix(true);
        doReadOnlyFoldingTest();
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
        doReadOnlyFoldingTest();
    }

    private void disableAllFoldings() {
        // TODO: Find a way to test all folding both together and separately
        AdvancedExpressionFoldingSettings.getInstance().getState().disableAll();
    }
}
