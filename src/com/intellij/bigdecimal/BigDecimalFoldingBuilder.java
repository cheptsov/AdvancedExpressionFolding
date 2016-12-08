package com.intellij.bigdecimal;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class BigDecimalFoldingBuilder extends FoldingBuilderEx {

    private static final FoldingDescriptor[] EMPTY = new FoldingDescriptor[0];

    private static Set<String> supportedMethods = new HashSet<String>() {
        {
            add("add");
            add("multiply");
            add("divide");
            add("subtract");
            add("remainder");
            add("scaleByPowerOfTen");
            add("pow");
            add("min");
            add("max");
            add("negate");
            add("plus");
            add("abs");
            add("valueOf");
            add("equals");
            add("and");
            add("gcd");
            add("not");
            add("or");
            add("shiftLeft");
            add("shiftRight");
            add("signum");
            add("xor");
            add("andNot");
            add("mod");
            add("modInverse");
            add("modPow");
            add("acos");
            add("asin");
            add("atan");
            add("atan2");
            add("cbrt");
            add("ceil");
            add("cos");
            add("cosh");
            add("floor");
            add("log");
            add("log10");
            add("log1p");
            add("random");
            add("rint");
            add("round");
            add("sin");
            add("sinh");
            add("sqrt");
            add("tan");
            add("tanh");
            add("toDegrees");
            add("toRadians");
            add("ulp");
            add("hypot");
            add("exp");
            add("expm1");
        }
    };

    private static Set<String> supportedClasses = new HashSet<String>() {
        {
            add("java.math.BigDecimal");
            add("java.math.BigInteger");
            add("java.lang.Math");
            add("java.lang.Long");
            add("java.lang.Integer");
            add("java.lang.Float");
            add("java.lang.Double");
        }
    };

    private static Set<String> supportedPrimitiveTypes = new HashSet<String>() {
        {
            add("int");
            add("long");
            add("float");
            add("double");
        }
    };

    private static Set<String> supportedBinaryOperators = new HashSet<String>() {
        {
            add("+");
            add("-");
            add("*");
            add("/");
        }
    };

    private static Map<String, Object> supportedConstants = new HashMap<String, Object>() {
        {
            put("ZERO", 0);
            put("ONE", 1);
            put("TEN", 10);
            put("PI", "π");
            put("E", "\uD835\uDC52");
        }
    };

    private static Set<String> supportedAssignmentMethods = new HashSet<String>() {
        {
            add("add");
            add("multiply");
            add("divide");
            add("subtract");
            add("remainder");
            add("shiftLeft");
            add("shiftRight");
            add("and");
            add("or");
            add("xor");
        }
    };


    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        FoldingGroup group = FoldingGroup.newGroup(BigDecimalFoldingBuilder.class.getName());
        List<FoldingDescriptor> allDescriptors = null;
        if (BigDecimalFoldingSettings.getInstance().isCollapseOperations()) {
            Expression expression = getExpression(psiElement);
            if (expression != null) {
                if (true/*expression instanceof Operation || expression instanceof Function*/) {
                    expression = expression.simplify();
                    final String text = expression.format();
                    if (!text.replaceAll("\\s+", "").equals(psiElement.getText().replaceAll("\\s+", ""))) {
                        return new FoldingDescriptor[]{new FoldingDescriptor(psiElement.getNode(),
                                psiElement.getTextRange(),
                                group) {
                            @Nullable
                            @Override
                            public String getPlaceholderText() {
                                return text;
                            }
                        }};
                    }
                }
            }
            if (expression == null) {
                for (PsiElement child : psiElement.getChildren()) {
                    FoldingDescriptor[] descriptors = buildFoldRegions(child, document, b);
                    if (descriptors.length > 0) {
                        if (allDescriptors == null) {
                            allDescriptors = new ArrayList<>();
                        }
                        allDescriptors.addAll(Arrays.asList(descriptors));
                    }
                }
            }
        }
        return allDescriptors != null ? allDescriptors.toArray(EMPTY) : EMPTY;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {
        return null;
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        return true;
    }

    // TODO: Replace with ReferenceContributor
    private Expression getExpression(PsiElement element) {
        if (element instanceof PsiMethodCallExpression) {
            return getMethodCallExpression((PsiMethodCallExpression) element);
        } else if (element instanceof PsiReferenceExpression) {
            return getReferenceExpression((PsiReferenceExpression) element);
        } else if (element instanceof PsiNewExpression) {
            return getNewExpression((PsiNewExpression) element);
        } else if (element instanceof PsiLiteralExpression) {
            return getLiteralExpression((PsiLiteralExpression) element);
        } else if (element instanceof PsiAssignmentExpression) {
            return getAssignmentExpression((PsiAssignmentExpression) element);
        } else if (element instanceof PsiBinaryExpression) {
            return getBinaryExpression((PsiBinaryExpression) element);
        } else if (element instanceof PsiPrefixExpression) {
            return getPrefixExpression((PsiPrefixExpression) element);
        } else if (element instanceof PsiParenthesizedExpression) {
            return getExpression(((PsiParenthesizedExpression) element).getExpression());
        }
        return null;
    }

    private Expression getLiteralExpression(PsiLiteralExpression element) {
        if (element.getType() != null) {
            if (supportedPrimitiveTypes.contains(element.getType().getCanonicalText())) {
                Object value = element.getValue();
                if (value instanceof Number) {
                    return new Literal((Number) value);
                }
            }
        }
        return null;
    }

    private Expression getPrefixExpression(PsiPrefixExpression element) {
        if (element.getOperationSign().getText().equals("!")) {
            Expression operand = getExpression(element.getOperand());
            if (operand instanceof Equal) {
                return new NotEqual(((Equal) operand).getOperands());
            }
        } else if (element.getOperationSign().getText().equals("-")) {
            Expression operand = getExpression(element.getOperand());
            if (operand != null) {
                return new Negate(Collections.singletonList(operand));
            }
        }
        return null;
    }

    private Expression getBinaryExpression(PsiBinaryExpression element) {
        if (element.getLOperand() instanceof PsiMethodCallExpression
                && element.getROperand() instanceof PsiLiteralExpression
                || element.getROperand() instanceof PsiMethodCallExpression &&
                element.getLOperand() instanceof PsiLiteralExpression) {
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) (element
                    .getLOperand() instanceof PsiMethodCallExpression
                    ? element.getLOperand() : element.getROperand());

            PsiLiteralExpression literalExpression = (PsiLiteralExpression) (element
                    .getLOperand() instanceof PsiLiteralExpression
                    ? element.getLOperand() : element.getROperand());
            if (literalExpression.getText().equals("0") || literalExpression.getText().equals("-1") || literalExpression.getText().equals("1")) {
                Optional<PsiElement> identifier = Stream.of(methodCallExpression.getMethodExpression().getChildren())
                        .filter(c -> c instanceof PsiIdentifier).findAny();
                if (identifier.isPresent() && identifier.get().getText().equals("compareTo") && methodCallExpression.getArgumentList().getExpressions().length == 1) {
                    PsiMethod method = (PsiMethod) methodCallExpression.getMethodExpression().resolve();
                    if (method != null) {
                        PsiClass psiClass = method.getContainingClass();
                        if (psiClass != null && supportedClasses.contains(psiClass.getQualifiedName())) {
                            Expression qualifier = getExpression(methodCallExpression.getMethodExpression()
                                    .getQualifierExpression());
                            if (qualifier != null) {
                                Expression argument = getExpression(methodCallExpression.getArgumentList()
                                        .getExpressions()[0]);
                                if (argument != null) {
                                    switch (element.getOperationSign().getText()) {
                                        case "==":
                                            switch (literalExpression.getText()) {
                                                case "-1":
                                                    return new Less(Arrays.asList(qualifier, argument));
                                                case "0":
                                                    return new Equal(Arrays.asList(qualifier, argument));
                                                case "1":
                                                    return new Greater(Arrays.asList(qualifier, argument));
                                            }
                                        case "<":
                                            switch (literalExpression.getText()) {
                                                case "1":
                                                    return new LessEqual(Arrays.asList(qualifier, argument));
                                                case "0":
                                                    return new Less(Arrays.asList(qualifier, argument));
                                            }
                                        case ">":
                                            switch (literalExpression.getText()) {
                                                case "-1":
                                                    return new GreaterEqual(Arrays.asList(qualifier, argument));
                                                case "0":
                                                    return new Greater(Arrays.asList(qualifier, argument));
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (supportedBinaryOperators.contains(element.getOperationSign().getText())) {
            Expression leftExpression = getExpression(element.getLOperand());
            if (leftExpression != null) {
                Expression rightExpression = getExpression(element.getROperand());
                if (rightExpression != null) {
                    switch (element.getOperationSign().getText()) {
                        case "+":
                            return new Add(Arrays.asList(leftExpression, rightExpression));
                        case "-":
                            return new Subtract(Arrays.asList(leftExpression, rightExpression));
                        case "*":
                            return new Multiply(Arrays.asList(leftExpression, rightExpression));
                        case "/":
                            return new Divide(Arrays.asList(leftExpression, rightExpression));
                    }
                }
            }
        }
        return null;
    }

    private Expression getAssignmentExpression(PsiAssignmentExpression element) {
        Variable leftVariable = getVariableExpression(element.getLExpression(), false);
        if (leftVariable != null) {
            if (element.getRExpression() instanceof PsiMethodCallExpression) {
                PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) element.getRExpression();
                PsiReferenceExpression referenceExpression = methodCallExpression.getMethodExpression();
                Optional<PsiElement> identifier = Stream.of(referenceExpression.getChildren())
                        .filter(c -> c instanceof PsiIdentifier).findAny();
                if (identifier.isPresent() && supportedMethods.contains(identifier.get().getText())) {
                    PsiMethod method = (PsiMethod) referenceExpression.resolve();
                    if (method != null && supportedAssignmentMethods.contains(method.getName())
                            && methodCallExpression.getArgumentList().getExpressions().length == 1) {
                        PsiClass psiClass = method.getContainingClass();
                        if (psiClass != null) {
                            if (supportedClasses.contains(psiClass.getQualifiedName())) {
                                Variable rightVariable = getVariableExpression(
                                        methodCallExpression.getMethodExpression().getQualifierExpression(), false);
                                if (rightVariable != null && leftVariable.getName().equals(rightVariable.getName())) {
                                    Expression argumentExpression = getExpression(
                                            methodCallExpression.getArgumentList().getExpressions()[0]);
                                    if (argumentExpression != null) {
                                        switch (method.getName()) {
                                            case "add":
                                                return new AddAssign(Arrays.asList(leftVariable, argumentExpression));
                                            case "subtract":
                                                return new SubtractAssign(
                                                        Arrays.asList(leftVariable, argumentExpression));
                                            case "multiply":
                                                return new MultiplyAssign(
                                                        Arrays.asList(leftVariable, argumentExpression));
                                            case "divide":
                                                return new DivideAssign(
                                                        Arrays.asList(leftVariable, argumentExpression));
                                            case "remainder":
                                                return new RemainderAssign(
                                                        Arrays.asList(leftVariable, argumentExpression));
                                            case "shiftLeft":
                                                return new ShiftLeftAssign(
                                                        Arrays.asList(leftVariable, argumentExpression));
                                            case "shiftRight":
                                                return new ShiftRightAssign(
                                                        Arrays.asList(leftVariable, argumentExpression));
                                            case "and":
                                                return new AndAssign(Arrays.asList(leftVariable, argumentExpression));
                                            case "or":
                                                return new OrAssign(Arrays.asList(leftVariable, argumentExpression));
                                            case "xor":
                                                return new XorAssign(Arrays.asList(leftVariable, argumentExpression));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private Expression getNewExpression(PsiNewExpression element) {
        if (element.getType() != null && supportedClasses
                .contains(element.getType().getCanonicalText()) && element.getArgumentList() != null
                && element.getArgumentList().getExpressions().length == 1
                && element.getArgumentList().getExpressions()[0] instanceof PsiLiteralExpression) {
            return getConstructorExpression(element.getArgumentList().getExpressions()[0],
                    element.getType().getCanonicalText());
        } else if (element.getType() != null && supportedClasses
                .contains(element.getType().getCanonicalText()) && element.getArgumentList() != null
                && element.getArgumentList().getExpressions().length == 1
                && element.getArgumentList().getExpressions()[0] instanceof PsiReferenceExpression) {
            return getReferenceExpression((PsiReferenceExpression) element.getArgumentList().getExpressions()[0]);
        }
        return null;
    }

    @Nullable
    private Expression getReferenceExpression(PsiReferenceExpression element) {
        Optional<PsiElement> identifier = Stream.of(element.getChildren())
                .filter(c -> c instanceof PsiIdentifier).findAny();
        if (identifier.isPresent()) {
            Object constant = supportedConstants.get(identifier.get().getText());
            if (constant != null) {
                if (isSupportedClass(element) && constant instanceof Number) {
                    return new Literal((Number) constant);
                } else if (isSupportedClass(element) && constant instanceof String) {
                    return new Variable((String) constant);
                }
            } else {
                Expression variable = getVariableExpression(element, true);
                if (variable != null) return variable;
            }
        }
        return null;
    }

    @Nullable
    private Variable getVariableExpression(PsiElement element, boolean includePrimitiveTypes) {
        PsiReference reference = element.getReference();
        if (reference != null) {
            PsiElement e = reference.resolve();
            if (e instanceof PsiVariable) {
                PsiVariable variable = (PsiVariable) e;
                if (supportedClasses.contains(variable.getType().getCanonicalText())) {
                    return new Variable(variable.getName());
                } else if (supportedPrimitiveTypes
                        .contains(variable.getType().getCanonicalText()) && includePrimitiveTypes) {
                    return new Variable(variable.getName());
                }
            }
        }
        return null;
    }

    private boolean isSupportedClass(PsiElement element) {
        PsiReference reference = element.getReference();
        if (reference != null) {
            PsiElement e = reference.resolve();
            if (e instanceof PsiField) {
                PsiField field = (PsiField) e;
                PsiClass psiClass = field.getContainingClass();
                if (psiClass != null) {
                    if (supportedClasses.contains(psiClass.getQualifiedName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    private Expression getMethodCallExpression(PsiMethodCallExpression element) {
        PsiReferenceExpression referenceExpression = element.getMethodExpression();
        Optional<PsiElement> identifier = Stream.of(referenceExpression.getChildren())
                .filter(c -> c instanceof PsiIdentifier).findAny();
        if (identifier.isPresent() && supportedMethods.contains(identifier.get().getText())) {
            PsiMethod method = (PsiMethod) referenceExpression.resolve();
            if (method != null) {
                PsiClass psiClass = method.getContainingClass();
                if (psiClass != null) {
                    if (supportedClasses.contains(psiClass.getQualifiedName())) {
                        PsiExpression qualifier = element
                                .getMethodExpression().getQualifierExpression();
                        Expression qualifierExpression = getExpression(qualifier);
                        if (qualifierExpression != null) {
                            if (element.getArgumentList().getExpressions().length == 1) {
                                PsiExpression argument = element.getArgumentList().getExpressions()[0];
                                Expression argumentExpression = getExpression(argument);
                                if (argumentExpression != null) {
                                    String methodName = identifier.get().getText();
                                    switch (methodName) {
                                        case "add":
                                            return new Add(Arrays.asList(qualifierExpression, argumentExpression));
                                        case "subtract":
                                            return new Subtract(
                                                    Arrays.asList(qualifierExpression, argumentExpression));
                                        case "multiply":
                                            return new Multiply(
                                                    Arrays.asList(qualifierExpression, argumentExpression));
                                        case "divide":
                                            return new Divide(
                                                    Arrays.asList(qualifierExpression, argumentExpression));
                                        case "remainder":
                                            return new Reminder(
                                                    Arrays.asList(qualifierExpression, argumentExpression));
                                        case "mod":
                                            return new Reminder(
                                                    Arrays.asList(qualifierExpression, argumentExpression));
                                        case "scaleByPowerOfTen":
                                            return new Multiply(
                                                    Arrays.asList(qualifierExpression, new Pow(Arrays
                                                            .asList(new Literal(10),
                                                                    argumentExpression))));
                                        case "andNot":
                                            return new And(
                                                    Arrays.asList(qualifierExpression, new Not(
                                                            Collections.singletonList(argumentExpression))));
                                        case "modInverse":
                                            return new Reminder(
                                                    Arrays.asList(new Pow(
                                                                    Arrays.asList(qualifierExpression, new Literal(-1))),
                                                            argumentExpression));
                                        case "pow":
                                            return new Pow(Arrays.asList(qualifierExpression, argumentExpression));
                                        case "min":
                                            return new Min(Arrays.asList(qualifierExpression, argumentExpression));
                                        case "max":
                                            return new Max(Arrays.asList(qualifierExpression, argumentExpression));
                                        case "gcd":
                                            return new Gcd(Arrays.asList(qualifierExpression, argumentExpression));
                                        case "and":
                                            return new And(Arrays.asList(qualifierExpression, argumentExpression));
                                        case "or":
                                            return new Or(Arrays.asList(qualifierExpression, argumentExpression));
                                        case "xor":
                                            return new Xor(Arrays.asList(qualifierExpression, argumentExpression));
                                        case "shiftLeft":
                                            return new ShiftLeft(
                                                    Arrays.asList(qualifierExpression, argumentExpression));
                                        case "shiftRight":
                                            return new ShiftRight(
                                                    Arrays.asList(qualifierExpression, argumentExpression));
                                        case "equals":
                                            return new Equal(Arrays.asList(qualifierExpression, argumentExpression));
                                    }
                                }
                            } else if (element.getArgumentList().getExpressions().length == 0) {
                                String methodName = identifier.get().getText();
                                switch (methodName) {
                                    case "plus":
                                        return qualifierExpression;
                                    case "negate":
                                        return new Negate(Collections.singletonList(qualifierExpression));
                                    case "not":
                                        return new Not(Collections.singletonList(qualifierExpression));
                                    case "abs":
                                        return new Abs(Collections.singletonList(qualifierExpression));
                                    case "signum":
                                        return new Signum(Collections.singletonList(qualifierExpression));
                                }
                            } else if (element.getArgumentList().getExpressions().length == 2) {
                                PsiExpression a1 = element.getArgumentList().getExpressions()[0];
                                Expression a1Expression = getExpression(a1);
                                PsiExpression a2 = element.getArgumentList().getExpressions()[1];
                                Expression a2Expression = getExpression(a2);
                                if (a1Expression != null && a2Expression != null) {
                                    String methodName = identifier.get().getText();
                                    switch (methodName) {
                                        case "modPow":
                                            return new Reminder(
                                                    Arrays.asList(new Pow(
                                                                    Arrays.asList(qualifierExpression, a1Expression)),
                                                            a2Expression));
                                    }
                                }
                            }
                        } else if (element.getArgumentList().getExpressions().length == 1) {
                            PsiExpression argument = element.getArgumentList().getExpressions()[0];
                            if (method.getName().equals("valueOf") && argument instanceof PsiLiteralExpression) {
                                return getConstructorExpression(argument, psiClass.getQualifiedName());
                            } else if (method.getName().equals("valueOf") && argument instanceof PsiReferenceExpression) {
                                return getReferenceExpression((PsiReferenceExpression) argument);
                            } else {
                                Expression argumentExpression = getExpression(argument);
                                if (argumentExpression != null) {
                                    switch (method.getName()) {
                                        case "abs":
                                            return new Abs(Collections.singletonList(argumentExpression));
                                        case "acos":
                                            return new Acos(Collections.singletonList(argumentExpression));
                                        case "asin":
                                            return new Asin(Collections.singletonList(argumentExpression));
                                        case "atan":
                                            return new Atan(Collections.singletonList(argumentExpression));
                                        case "cbrt":
                                            return new Cbrt(Collections.singletonList(argumentExpression));
                                        case "ceil":
                                            return new Ceil(Collections.singletonList(argumentExpression));
                                        case "cos":
                                            return new Cos(Collections.singletonList(argumentExpression));
                                        case "cosh":
                                            return new Cosh(Collections.singletonList(argumentExpression));
                                        case "floor":
                                            return new Floor(Collections.singletonList(argumentExpression));
                                        case "log":
                                            return new Log(Collections.singletonList(argumentExpression));
                                        case "log1p":
                                            return new Log(Collections.singletonList(
                                                    new Add(Arrays.asList(argumentExpression, new Literal(1)))));
                                        case "Log10":
                                            return new Log10(Collections.singletonList(argumentExpression));
                                        case "rint":
                                            return new Rint(Collections.singletonList(argumentExpression));
                                        case "round":
                                            return new Round(Collections.singletonList(argumentExpression));
                                        case "sin":
                                            return new Sin(Collections.singletonList(argumentExpression));
                                        case "sinh":
                                            return new Sinh(Collections.singletonList(argumentExpression));
                                        case "Sqrt":
                                            return new Sqrt(Collections.singletonList(argumentExpression));
                                        case "tan":
                                            return new Tan(Collections.singletonList(argumentExpression));
                                        case "tanh":
                                            return new Tanh(Collections.singletonList(argumentExpression));
                                        case "toDegrees":
                                            return new ToDegrees(Collections.singletonList(argumentExpression));
                                        case "toRadians":
                                            return new ToRadians(Collections.singletonList(argumentExpression));
                                        case "ulp":
                                            return new Ulp(Collections.singletonList(argumentExpression));
                                        case "exp":
                                            return new Pow(
                                                    Arrays.asList(new Variable((String) supportedConstants.get("E")),
                                                            argumentExpression));
                                        case "expm1":
                                            return new Subtract(Arrays.asList(new Pow(Arrays
                                                    .asList(new Variable((String) supportedConstants.get("E")),
                                                            argumentExpression)), new Literal(1)));
                                    }
                                }
                            }
                        } else if (element.getArgumentList().getExpressions().length == 2) {
                            PsiExpression a1 = element.getArgumentList().getExpressions()[0];
                            Expression a1Expression = getExpression(a1);
                            PsiExpression a2 = element.getArgumentList().getExpressions()[1];
                            Expression a2Expression = getExpression(a2);
                            if (a1Expression != null && a2Expression != null) {
                                String methodName = identifier.get().getText();
                                switch (methodName) {
                                    case "min":
                                        return new Min(Arrays.asList(a1Expression, a2Expression));
                                    case "max":
                                        return new Max(Arrays.asList(a1Expression, a2Expression));
                                    case "pow":
                                        return new Pow(Arrays.asList(a1Expression, a2Expression));
                                    case "hypot":
                                        return new Add(Collections.singletonList(new Sqrt(Collections.singletonList(
                                                new Add(Arrays
                                                        .asList(new Pow(Arrays.asList(a1Expression, new Literal(2))),
                                                                new Pow(Arrays.asList(a2Expression, new Literal(2)))))))));
                                }
                            }
                        } else if (element.getArgumentList().getExpressions().length == 0) {
                            switch (method.getName()) {
                                case "random":
                                    return new Random(Collections.emptyList());
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private Expression getConstructorExpression(PsiExpression argument, String classQualifiedName) {
        Expression literalExpression = getLiteralExpression((PsiLiteralExpression) argument);
        if (literalExpression != null) {
            return literalExpression;
        } else {
            try {
                String value = argument.getText();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                if ("java.lang.Long".equals(classQualifiedName)) {
                    return new Literal(Long.valueOf(value));
                } else if ("java.lang.Integer".equals(classQualifiedName)) {
                    return new Literal(Integer.valueOf(value));
                } else if ("java.lang.Float".equals(classQualifiedName)) {
                    return new Literal(Float.valueOf(value));
                } else if ("java.lang.Double".equals(classQualifiedName)) {
                    return new Literal(Double.valueOf(value));
                }
            } catch (Exception ignore) {
            }
        }
        return null;
    }
}
