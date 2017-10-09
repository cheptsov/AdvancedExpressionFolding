package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

public class FoldingTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        disableAllFoldings();
    }
    
    public void doFoldingTest() {
        myFixture.testFoldingWithCollapseStatus(getTestDataPath() + "/" + getTestName(false) + ".java");
    }

    public void doHighlightingTest() {
        myFixture.testHighlighting(getTestDataPath() + "/" + getTestName(false) + ".java");
    }

    public void testAbs() {
        doFoldingTest();
    }

    public void testElvis() {
        doFoldingTest();
    }

    public void testGet() {
        doFoldingTest();
    }

    public void testFor() {
        doFoldingTest();
    }

    public void testStringBuilderTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setConcatenationExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testInterpolatedStringTestData() {
        AdvancedExpressionFoldingSettings.getInstance().getState().setConcatenationExpressionsCollapse(true);
        doFoldingTest();
    }

    public void testCollectionTestData() {
        // TODO Make settings more granular
        AdvancedExpressionFoldingSettings.getInstance().getState().setConcatenationExpressionsCollapse(true);
        doFoldingTest();
    }

    private void disableAllFoldings() {
        AdvancedExpressionFoldingSettings.getInstance().getState().disableAll();
    }
}
