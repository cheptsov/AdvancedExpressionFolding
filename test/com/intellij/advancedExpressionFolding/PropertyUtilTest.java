package com.intellij.advancedExpressionFolding;

import org.junit.Test;

import static com.intellij.advancedExpressionFolding.PropertyUtil.guessPropertyName;
import static org.junit.Assert.*;

public class PropertyUtilTest {
    @Test
    public void testGuessPropertyName() {
        assertEquals("", guessPropertyName(""));
        assertEquals("length", guessPropertyName("length"));
        assertEquals("name", guessPropertyName("getName"));
        assertEquals("name", guessPropertyName("setName"));
        assertEquals("enabled", guessPropertyName("isEnabled"));
        assertEquals("enabledByDefault", guessPropertyName("isEnabledByDefault"));
        assertEquals("html", guessPropertyName("getHTML"));
        assertEquals("htmlText", guessPropertyName("isHTMLText"));
    }
}