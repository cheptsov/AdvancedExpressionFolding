package com.intellij.advancedExpressionFolding;

import org.jetbrains.annotations.NotNull;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

public class PropertyUtil {

    @NotNull
    public static String guessPropertyName(@NotNull String text) {
        StringBuilder sb = new StringBuilder(text.length());
        int startPos;
        if (text.startsWith("get") || text.startsWith("set")) {
            startPos = 3;
        } else if (text.startsWith("is")) {
            startPos = 2;
        } else {
            startPos = 0;
        }
        sb.append(text, startPos, text.length());
        for (int i = 0; i < sb.length(); i++) {
            if (isUpperCase(sb.charAt(i)) &&
                    (i == sb.length() - 1 || isUpperCase(sb.charAt(i + 1)) || i == 0)) {
                sb.setCharAt(i, toLowerCase(sb.charAt(i)));
            } else if (isLowerCase(sb.charAt(i))) {
                break;
            }
        }
        return sb.toString();
    }
}
