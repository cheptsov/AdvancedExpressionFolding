package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

public class CharacterLiteral extends Expr implements CharSequenceLiteral {
    private Character character;

    public CharacterLiteral(PsiElement element, TextRange textRange, Character character) {
        super(element, textRange);
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }
}
