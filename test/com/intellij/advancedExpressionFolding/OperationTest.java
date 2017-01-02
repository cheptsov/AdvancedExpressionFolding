package com.intellij.advancedExpressionFolding;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

public class OperationTest {
    @Test
    public void simplify() throws Exception {
        Operation e1 = new Subtract(null, Arrays.asList(new Subtract(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b"))), new Variable(null, "c")));
        Operation s1 = (Operation) e1.simplify(false);
        Assert.assertNotEquals(s1, e1);
        Assert.assertEquals(3, s1.getOperands().size());
        Assert.assertEquals("-", s1.getCharacter());
        Operation e2 = new Multiply(null, Arrays.asList(new Add(null, Arrays.asList(new Add(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b"))), new Variable(null, "c"))), new NumberLiteral(null, new BigDecimal(10))));
        Operation s2 = (Operation) e2.simplify(false);
        Assert.assertEquals("(a + b + c) * 10", s2.format());
        Operation e3 = new Subtract(null,
                Arrays.asList(new Subtract(null, Arrays.asList(new NumberLiteral(null, 10), new NumberLiteral(null, 4))), new NumberLiteral(null, 1)));
        Expression s3 = e3.simplify(true);
        Assert.assertEquals("5", s3.format());
    }

    @Test
    public void format() throws Exception {
        Expression e1 = new Subtract(null, Arrays.asList(new Subtract(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b"))), new Variable(null, "c")));
        Assert.assertEquals("a - b - c", e1.format());
        Expression e1_2 = new Subtract(null, Arrays.asList(new Variable(null, "c"), new Subtract(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b")))));
        Assert.assertEquals("c - (a - b)", e1_2.format());
        Operation e2 = new Add(null, Arrays.asList(new Multiply(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b"))), new Variable(null, "c")));
        Assert.assertEquals("a * b + c", e2.format());
        Operation e3 = new Multiply(null, Arrays.asList(new Add(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b"))), new Variable(null, "c")));
        Assert.assertEquals("(a + b) * c", e3.format());
        Operation e4 = new Multiply(null, Arrays.asList(new Add(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b"))), e3));
        Assert.assertEquals("(a + b) * (a + b) * c", e4.format());
        Operation e5 = new Multiply(null, Arrays.asList(new Max(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b"))), new Variable(null, "c")));
        Assert.assertEquals("max(a, b) * c", e5.format());
        Operation e6 = new Multiply(null, Arrays.asList(new Pow(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b"))), e3));
        Assert.assertEquals("aᵇ * (a + b) * c", e6.format());
        Expression e7 = new Negate(null, Collections.singletonList(new Variable(null, "a")));
        Assert.assertEquals("-a", e7.format());
        Expression e8 = new Subtract(null, Arrays.asList(new NumberLiteral(null, BigDecimal.ZERO), new Add(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b")))));
        Assert.assertEquals("0 - (a + b)", e8.format());
        Expression e9 = new Add(null, Arrays.asList(new Remainder(null, Arrays.asList(new Variable(null, "a"), new Variable(null, "b"))), new Variable(null, "c")));
        Assert.assertEquals("a % b + c", e9.format());
        Expression e9_1 = new Add(null, Arrays.asList(new Variable(null, "a"), new Remainder(null, Arrays.asList(new Variable(null, "b"), new Variable(null, "c")))));
        Assert.assertEquals("a + b % c", e9_1.format());
        Expression e10 = new Abs(null, Collections.singletonList(new Variable(null, "a")));
        Assert.assertEquals("|a|", e10.format());
        Expression e11 = new Remainder(null,
                Arrays.asList(new Variable(null, "a"), new Multiply(null, Arrays.asList(new Variable(null, "b"), new Variable(null, "c")))));
        Assert.assertEquals("a % (b * c)", e11.format());
    }
}