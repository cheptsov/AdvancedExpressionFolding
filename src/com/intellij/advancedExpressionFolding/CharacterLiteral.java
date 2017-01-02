package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;

public class CharacterLiteral extends Expression {
    private Character character;

    public CharacterLiteral(TextRange textRange, Character character) {
        super(textRange);
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
