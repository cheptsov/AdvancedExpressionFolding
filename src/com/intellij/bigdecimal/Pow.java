package com.intellij.bigdecimal;

import java.util.List;

public class Pow extends Operation {
    public Pow(List<Expression> operands) {
        super("**", 200, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Pow(newOperands);
    }

    @Override
    public String format() {
        String a = operands.get(0).format();
        String b = operands.get(1).format();
        String bs = superscript(b);
        if (bs != null && !bs.contains(getCharacter())) {
            return a + bs;
        } else {
            return a + " " + getCharacter() + " " + b;
        }
    }
}
