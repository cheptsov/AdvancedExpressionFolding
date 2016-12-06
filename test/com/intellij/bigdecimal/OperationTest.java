package com.intellij.bigdecimal;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

public class OperationTest {
    @Test
    public void simplify() throws Exception {
        Operation e1 = new Subtract(Arrays.asList(new Subtract(Arrays.asList(new Variable("a"), new Variable("b"))), new Variable("c")));
        Operation s1 = e1.simplify();
        Assert.assertNotEquals(s1, e1);
        Assert.assertEquals(3, s1.getOperands().size());
        Assert.assertEquals("-", s1.getCharacter());
        Operation e2 = new Multiply(Arrays.asList(new Add(Arrays.asList(new Add(Arrays.asList(new Variable("a"), new Variable("b"))), new Variable("c"))), new Literal(new BigDecimal(10))));
        Operation s2 = e2.simplify();
        Assert.assertEquals("(a + b + c) * 10", s2.format());
    }

    @Test
    public void format() throws Exception {
        Expression e1 = new Subtract(Arrays.asList(new Subtract(Arrays.asList(new Variable("a"), new Variable("b"))), new Variable("c")));
        Assert.assertEquals("a - b - c", e1.format());
        Expression e1_2 = new Subtract(Arrays.asList(new Variable("c"), new Subtract(Arrays.asList(new Variable("a"), new Variable("b")))));
        Assert.assertEquals("c - (a - b)", e1_2.format());
        Operation e2 = new Add(Arrays.asList(new Multiply(Arrays.asList(new Variable("a"), new Variable("b"))), new Variable("c")));
        Assert.assertEquals("a * b + c", e2.format());
        Operation e3 = new Multiply(Arrays.asList(new Add(Arrays.asList(new Variable("a"), new Variable("b"))), new Variable("c")));
        Assert.assertEquals("(a + b) * c", e3.format());
        Operation e4 = new Multiply(Arrays.asList(new Add(Arrays.asList(new Variable("a"), new Variable("b"))), e3));
        Assert.assertEquals("(a + b) * (a + b) * c", e4.format());
        Operation e5 = new Multiply(Arrays.asList(new Max(Arrays.asList(new Variable("a"), new Variable("b"))), new Variable("c")));
        Assert.assertEquals("max(a, b) * c", e5.format());
        Operation e6 = new Multiply(Arrays.asList(new Pow(Arrays.asList(new Variable("a"), new Variable("b"))), e3));
        Assert.assertEquals("a ^ b * (a + b) * c", e6.format());
        Expression e7 = new Subtract(Arrays.asList(new Literal(BigDecimal.ZERO), new Variable("a")));
        Assert.assertEquals("- a", e7.format());
        Expression e8 = new Subtract(Arrays.asList(new Literal(BigDecimal.ZERO), new Add(Arrays.asList(new Variable("a"), new Variable("b")))));
        Assert.assertEquals("- (a + b)", e8.format());
        Expression e9 = new Add(Arrays.asList(new Reminder(Arrays.asList(new Variable("a"), new Variable("b"))), new Variable("c")));
        Assert.assertEquals("a % b + c", e9.format());
    }
}