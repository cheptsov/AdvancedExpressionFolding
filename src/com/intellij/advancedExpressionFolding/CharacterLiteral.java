package com.intellij.advancedExpressionFolding;

public class CharacterLiteral extends Expression {
    private Character character;

    public CharacterLiteral(Character character) {
        this.character = character;
    }

    @Override
    public String format() {
        return "'" + character + "'";
    }

    public Character getCharacter() {
        return character;
    }
}
