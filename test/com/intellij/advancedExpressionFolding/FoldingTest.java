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
    }

    public void doTest() {
        myFixture.testFolding(getTestDataPath() + "/" + getTestName(false) + ".java");
    }

    public void testAbs() throws Exception {
        doTest();
    }

    public void testElvis() throws Exception {
        doTest();
    }

    public void testGet() throws Exception {
        doTest();
    }
}
