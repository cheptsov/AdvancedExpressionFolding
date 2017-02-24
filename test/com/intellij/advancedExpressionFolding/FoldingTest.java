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
                "public class BigDecimal extends Number implements Comparable<BigDecimal> { " +
                "   public BigDecimal(int val) {}\n" +
                "   public BigDecimal abs() { return this; }\n" +
                "   public int intValue() { return 0; }\n" +
                "}");
    }

    public void doTest() {
        myFixture.testFolding(getTestDataPath() + "/" + getTestName(false) + ".java");
    }

    public void testAbs() throws Exception {
        doTest();
    }
}
