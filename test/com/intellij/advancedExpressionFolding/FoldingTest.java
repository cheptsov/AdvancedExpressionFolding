package com.intellij.advancedExpressionFolding;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class FoldingTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.addClass("package java.math;\n" +
                "public class BigDecimal extends Number implements Comparable<BigDecimal> {\n" +
                "   public BigDecimal(int val) {}\n" +
                "   public BigDecimal abs() { return this; }\n" +
                "   public int intValue() { return 0; }\n" +
                "}");
        myFixture.addClass("package java.util;\n" +
                "public class ArrayList<T> {\n" +
                "   public T get(int p) { return null; }\n" +
                "   public int size() { return 0; }\n" +
                "}");
        myFixture.addClass("package java.lang;\n" +
                "\n" +
                "public class String {\n" +
                "    public java.lang.String toString() {\n" +
                "        return null;\n" +
                "    }\n" +
                "}");
        myFixture.addClass("package java.lang;\n" +
                "\n" +
                "public class StringBuilder {\n" +
                "    public StringBuilder() {\n" +
                "    }\n" +
                "\n" +
                "    public StringBuilder(Object object) {\n" +
                "    }\n" +
                "\n" +
                "    public StringBuilder append(Object object) {\n" +
                "        return this;\n" +
                "    }\n" +
                "    \n" +
                "    public java.lang.String toString() {\n" +
                "        return null;\n" +
                "    }\n" +
                "}");
        disableAllFoldings();
    }

    public void doFoldingTest() {
        myFixture.testFoldingWithCollapseStatus(getTestDataPath() + "/" + getTestName(false) + ".java");
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
        AdvancedExpressionFoldingSettings.getInstance().getState().setGetSetExpressionsCollapse(true);
        doFoldingTest();
    }

    private void disableAllFoldings() {
        AdvancedExpressionFoldingSettings.getInstance().getState().disableAll();
    }
}
