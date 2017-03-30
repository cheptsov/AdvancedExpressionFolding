package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;

public class CharacterLiteral extends Expression {
    private Character character;

    public CharacterLiteral(PsiElement element, TextRange textRange, Character character) {
        super(element, textRange);
        this.character = character;
    }

    @Override
    public String format() {
        return "'" + StringUtil.escapeStringCharacters(character.toString()) + "'";
    }

    public Character getCharacter() {
        return character;
    }
}
