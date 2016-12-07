package com.intellij.bigdecimal;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
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
        }
    };

    private static Set<String> supportedClasses = new HashSet<String>() {
        {
            add("java.math.BigDecimal");
            add("java.math.BigInteger");
        }
    };

    private static Set<String> supportedPrimitiveTypes = new HashSet<String>() {
        {
            add("int");
            add("long");
            add("double");
        }
    };

    private static Map<String, Number> supportedFields = new HashMap<String, Number>() {
        {
            put("ZERO", 0);
            put("ONE", 1);
            put("TEN", 10);
        }
    };

    private static Set<String> supportedAssignmentMethods = new HashSet<String>() {
        {
            add("add");
            add("multiply");
            add("divide");
            add("subtract");
            add("remainder");
            add("pow");
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
                if (expression instanceof Operation || expression instanceof Function) {
                    expression = expression.simplify();
                    final String text = expression.format();
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

    private Expression getExpression(PsiElement element) {
        if (element instanceof PsiMethodCallExpression) {
            return getMethodCallExpression((PsiMethodCallExpression) element);
        } else if (element instanceof PsiReferenceExpression) {
            return getReferenceExpression((PsiReferenceExpression) element);
        } else if (element instanceof PsiNewExpression) {
            return getNewExpression((PsiNewExpression) element);
        } else if (element instanceof PsiLiteralExpression) {
            if (getLiteralExpression((PsiLiteralExpression) element)) return new Literal(new BigDecimal(element.getText()));
        } else if (element instanceof PsiAssignmentExpression) {
            return getAssignmentExpression((PsiAssignmentExpression) element);
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
                                Variable rightVariable = getVariableExpression(methodCallExpression.getMethodExpression().getQualifierExpression(), false);
                                if (rightVariable != null && leftVariable.getName().equals(rightVariable.getName())) {
                                    Expression argumentExpression = getExpression(
                                            methodCallExpression.getArgumentList().getExpressions()[0]);
                                    if (argumentExpression != null) {
                                        switch (method.getName()) {
                                            case "add":
                                                return new AddAssign(Arrays.asList(leftVariable, argumentExpression));
                                            case "subtract":
                                                return new SubtractAssign(Arrays.asList(leftVariable, argumentExpression));
                                            case "multiply":
                                                return new MultiplyAssign(Arrays.asList(leftVariable, argumentExpression));
                                            case "divide":
                                                return new DivideAssign(Arrays.asList(leftVariable, argumentExpression));
                                            case "remainder":
                                                return new RemainderAssign(Arrays.asList(leftVariable, argumentExpression));
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

    private boolean getLiteralExpression(PsiLiteralExpression element) {
        return element.getType() != null && supportedPrimitiveTypes.contains(element.getType().getCanonicalText());
    }

    @Nullable
    private Expression getNewExpression(PsiNewExpression element) {
        if (element.getType() != null && supportedClasses
                .contains(element.getType().getCanonicalText()) && element.getArgumentList() != null
                && element.getArgumentList().getExpressions().length == 1
                && element.getArgumentList().getExpressions()[0] instanceof PsiLiteralExpression) {
            PsiLiteralExpression psiExpression = (PsiLiteralExpression) element.getArgumentList()
                    .getExpressions()[0];
            String value = psiExpression.getText();
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            return new Literal(new BigDecimal(value));
        }
        return null;
    }

    @Nullable
    private Expression getReferenceExpression(PsiReferenceExpression element) {
        Optional<PsiElement> identifier = Stream.of(element.getChildren())
                .filter(c -> c instanceof PsiIdentifier).findAny();
        if (identifier.isPresent()) {
            Number number = supportedFields.get(identifier.get().getText());
            if (number != null) {
                if (getFieldExpression(element)) return new Literal(number);
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
                } else if (supportedPrimitiveTypes.contains(variable.getType().getCanonicalText()) && includePrimitiveTypes) {
                    return new Variable(variable.getName());
                }
            }
        }
        return null;
    }

    private boolean getFieldExpression(PsiElement element) {
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
                                        case "scaleByPowerOfTen":
                                            return new Multiply(
                                                    Arrays.asList(qualifierExpression, new Pow(Arrays
                                                            .asList(new Literal(10),
                                                                    argumentExpression))));
                                        case "pow":
                                            return new Pow(Arrays.asList(qualifierExpression, argumentExpression));
                                        case "min":
                                            return new Min(Arrays.asList(qualifierExpression, argumentExpression));
                                        case "max":
                                            return new Max(Arrays.asList(qualifierExpression, argumentExpression));
                                    }
                                }
                            } else if (element.getArgumentList().getExpressions().length == 0) {
                                String methodName = identifier.get().getText();
                                switch (methodName) {
                                    case "plus":
                                        return qualifierExpression;
                                    case "negate":
                                        return new Negate(Collections.singletonList(qualifierExpression));
                                    case "abs":
                                        return new Abs(Collections.singletonList(qualifierExpression));
                                }
                            }
                        } else if ("valueOf".equals(method.getName()) && element.getArgumentList()
                                .getExpressions().length == 1) {
                            PsiExpression argument = element.getArgumentList().getExpressions()[0];
                            Expression argumentExpression = getExpression(argument);
                            if (argumentExpression != null) {
                                return argumentExpression;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
