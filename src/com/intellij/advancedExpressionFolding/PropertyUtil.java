package com.intellij.advancedExpressionFolding;

import org.jetbrains.annotations.NotNull;

public class PropertyUtil {

    @NotNull
    public static String guessPropertyName(@NotNull String text) {
        StringBuilder sb = new StringBuilder();
        if (text.startsWith("get")) {
            sb.append(text.substring(3));
        } else if (text.startsWith("set")) {
            sb.append(text.substring(3));
        } else if (text.startsWith("is")) {
            sb.append(text.substring(2));
        } else {
            sb.append(text);
        }
        for (int i = 0; i < sb.length(); i++) {
            if (Character.isUpperCase(sb.charAt(i)) &&
                    (i == sb.length() - 1 || Character.isUpperCase(sb.charAt(i + 1)) || i == 0)) {
                sb.setCharAt(i, Character.toLowerCase(sb.charAt(i)));
            } else if (Character.isLowerCase(sb.charAt(i))) {
                break;
            }
        }
        return sb.toString();
    }
}
