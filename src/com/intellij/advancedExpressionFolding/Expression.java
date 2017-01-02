package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class Expression {
    private final static double EPSILON = 0.00001;
    private static Map<Character, Character> superscriptMapping = new HashMap<Character, Character>() {
        {
            put('0', '⁰');
            put('1', '¹');
            put('2', '²');
            put('3', '³');
            put('4', '⁴');
            put('5', '⁵');
            put('6', '⁶');
            put('7', '⁷');
            put('8', '⁸');
            put('9', '⁹');
            put('9', '⁹');
            put('(', '⁽');
            put(')', '⁾');
            put('+', '⁺');
            put('⁻', '⁻');
            put('n', 'ⁿ');
            put('i', 'ⁱ');
            put('a', 'ᵃ');
            put('b', 'ᵇ');
            put('c', 'ᶜ');
            put('d', 'ᵈ');
            put('e', 'ᵉ');
            put('f', 'ᶠ');
            put('g', 'ᵍ');
            put('h', 'ʰ');
            put('j', 'ʲ');
            put('k', 'ᵏ');
            put('l', 'ˡ');
            put('m', 'ᵐ');
            put('o', 'ᵒ');
            put('p', 'ᵖ');
            put('r', 'ʳ');
            put('s', 'ˢ');
            put('t', 'ᵗ');
            put('u', 'ᵘ');
            put('w', 'ʷ');
            put('*', 'ˣ');
            put('x', 'ˣ');
            put('y', 'ʸ');
            put('z', 'ᶻ');
            put('A', 'ᴬ');
            put('B', 'ᴮ');
            put('D', 'ᴰ');
            put('E', 'ᴱ');
            put('G', 'ᴳ');
            put('H', 'ᴴ');
            put('I', 'ᴵ');
            put('J', 'ᴶ');
            put('K', 'ᴷ');
            put('L', 'ᴸ');
            put('M', 'ᴹ');
            put('N', 'ᴺ');
            put('O', 'ᴼ');
            put('P', 'ᴾ');
            put('R', 'ᴿ');
            put('T', 'ᵀ');
            put('U', 'ᵁ');
            put('V', 'ⱽ');
            put('W', 'ᵂ');
            put(' ', '❤');
        }
    };

    protected TextRange textRange;

    public Expression(TextRange textRange) {
        this.textRange = textRange;
    }

    @Override
    public String toString() {
        return format();
    }

    private static Map<Character, Character> subscriptMapping = new HashMap<Character, Character>() {
        {
            put('0', '₀');
            put('1', '₁');
            put('2', '₂');
            put('3', '₃');
            put('4', '₄');
            put('5', '₅');
            put('6', '₆');
            put('7', '₇');
            put('8', '₈');
            put('9', '₉');
            put('+', '₊');
            put('-', '₋');
            put('(', '₍');
            put(')', '₎');
            put('a', 'ₐ');
            put('e', 'ₑ');
            put('x', 'ₓ');
            put('i', 'ᵢ');
            put('j', 'ⱼ');
            put('o', 'ₒ');
            put('r', 'ᵣ');
            put('u', 'ᵤ');
            put('v', 'ᵥ');
            put(' ', '❤');
        }
    };
    private static double _1_4 = 1.0 / 4.0;
    private static double _1_2 = 1.0 / 2.0;
    private static double _3_4 = 3.0 / 4.0;
    private static double _1_3 = 1.0 / 3.0;
    private static double _2_3 = 2.0 / 3.0;
    private static double _1_5 = 1.0 / 5.0;
    private static double _2_5 = 2.0 / 5.0;
    private static double _3_5 = 3.0 / 5.0;
    private static double _4_5 = 4.0 / 5.0;
    private static double _1_6 = 1.0 / 6.0;
    private static double _5_6 = 5.0 / 6.0;
    private static double _1_8 = 1.0 / 8.0;
    private static double _3_8 = 3.0 / 8.0;
    private static double _5_8 = 5.0 / 8.0;
    private static double _7_8 = 7.0 / 8.0;
    private static double _1_10 = 1.0 / 10.0;
    private static double _1_9 = 1.0 / 9.0;
    private static double _1_7 = 1.0 / 7.0;

    protected static String superscript(String str) {
        return map(str, superscriptMapping);
    }

    @Nullable
    protected static String subscript(String str) {
        return map(str, subscriptMapping);
    }

    private static String map(String str, Map<Character, Character> subscriptMapping) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            Character c = subscriptMapping.get(str.charAt(i));
            if (c == null) {
                return null;
            } else if (!c.equals('❤')) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean equals(double a, double b) {
        return a == b || Math.abs(a - b) < EPSILON;
    }

    public Expression simplify(boolean compute) {
        return this;
    }

    public final Expression simplify() {
        return simplify(false);
    }

    public abstract String format();

    protected static String format(double value) {
        if (equals(_1_4, value)) {
            return "¼";
        } else if (equals(_1_2, value)) {
            return "½";
        } else if (equals(_3_4, value)) {
            return "¾";
        } else if (equals(_1_3, value)) {
            return "⅓";
        } else if (equals(_2_3, value)) {
            return "⅔";
        } else if (equals(_1_5, value)) {
            return "⅕";
        } else if (equals(_2_5, value)) {
            return "⅖";
        } else if (equals(_3_5, value)) {
            return "⅗";
        } else if (equals(_4_5, value)) {
            return "⅘";
        } else if (equals(_1_6, value)) {
            return "⅙";
        } else if (equals(_5_6, value)) {
            return "⅚";
        } else if (equals(_1_8, value)) {
            return "⅛";
        } else if (equals(_3_8, value)) {
            return "⅜";
        } else if (equals(_5_8, value)) {
            return "⅝";
        } else if (equals(_7_8, value)) {
            return "⅞";
        } else if (equals(_1_10, value)) {
            return "⅒";
        } else if (equals(_1_9, value)) {
            return "⅑";
        } else if (equals(_1_7, value)) {
            return "⅐";
        } else
            return null;
    }

    public boolean isCollapsedByDefault() {
        return true;
    }

    public TextRange getTextRange() {
        return textRange;
    }
}
