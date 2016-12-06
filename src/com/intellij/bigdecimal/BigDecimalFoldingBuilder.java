package com.intellij.bigdecimal;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

public class BigDecimalFoldingBuilder extends FoldingBuilderEx {

    private static final FoldingDescriptor[] EMPTY = new FoldingDescriptor[0];

    private static Set<String> constants = new HashSet<String>() {
        {
            add("TEN");
            add("ONE");
            add("ZERO");
        }
    };
    private static Set<String> methodParameters = new HashSet<String>() {
        {
            add("add");
            add("multiply");
            add("divide");
            add("subtract");
            add("pow");
            add("min");
            add("max");
            add("negate");
            add("plus");
        }
    };

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        FoldingGroup group = FoldingGroup.newGroup("BigDecimal");
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
            PsiMethodCallExpressionImpl methodCallExpression = (PsiMethodCallExpressionImpl) element;
            PsiReferenceExpression referenceExpression = methodCallExpression.getMethodExpression();
            Optional<PsiElement> identifier = Stream.of(referenceExpression.getChildren())
                    .filter(c -> c instanceof PsiIdentifier).findAny();
            if (identifier.isPresent() && methodParameters.contains(identifier.get().getText())) {
                PsiMethod method = (PsiMethod) referenceExpression.resolve();
                if (method != null) {
                    PsiClass psiClass = method.getContainingClass();
                    if (psiClass != null) {
                        if ("java.math.BigDecimal".equals(psiClass.getQualifiedName())) {
                            PsiExpression qualifier = ((PsiMethodCallExpressionImpl) element)
                                    .getMethodExpression().getQualifierExpression();
                            Expression qualifierExpression = getExpression(qualifier);
                            if (qualifierExpression != null) {
                                if (methodCallExpression.getArgumentList().getExpressions().length == 1) {
                                    PsiExpression argument = methodCallExpression.getArgumentList().getExpressions()[0];
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
                                            case "pow":
                                                return new Pow(Arrays.asList(qualifierExpression, argumentExpression));
                                            case "min":
                                                return new Min(Arrays.asList(qualifierExpression, argumentExpression));
                                            case "max":
                                                return new Max(Arrays.asList(qualifierExpression, argumentExpression));
                                        }
                                    }
                                } else if (methodCallExpression.getArgumentList().getExpressions().length == 0) {
                                    String methodName = identifier.get().getText();
                                    switch (methodName) {
                                        case "plus":
                                            return qualifierExpression;
                                        case "negate":
                                            return new Subtract(
                                                    Arrays.asList(new Literal(BigDecimal.ZERO), qualifierExpression));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (element instanceof PsiReferenceExpression) {
            Optional<PsiElement> identifier = Stream.of(element.getChildren())
                    .filter(c -> c instanceof PsiIdentifier).findAny();
            if (identifier.isPresent() && constants.contains(identifier.get().getText())) {
                PsiReference reference = element.getReference();
                if (reference != null) {
                    PsiElement e = reference.resolve();
                    if (e instanceof PsiField) {
                        PsiField field = (PsiField) e;
                        PsiClass psiClass = field.getContainingClass();
                        if (psiClass != null) {
                            if ("java.math.BigDecimal".equals(psiClass.getQualifiedName())) {
                                if (identifier.get().getText().equals("TEN")) {
                                    return new Literal(BigDecimal.TEN);
                                } else if (identifier.get().getText().equals("ONE")) {
                                    return new Literal(BigDecimal.ONE);
                                } else if (identifier.get().getText().equals("ZERO")) {
                                    return new Literal(BigDecimal.ZERO);
                                }
                            }
                        }
                    }
                }
            } else if (identifier.isPresent()) {
                PsiReference reference = element.getReference();
                if (reference != null) {
                    PsiElement e = reference.resolve();
                    if (e instanceof PsiVariable) {
                        PsiVariable variable = (PsiVariable) e;
                        if ("java.math.BigDecimal"
                                .equals(variable.getType().getCanonicalText())) {
                            return new Variable(variable.getName());
                        } else if ("int".equals(variable.getType().getCanonicalText())) {
                            return new Variable(variable.getName());
                        }
                    }
                }
            }
        } else if (element instanceof PsiNewExpression) {
            PsiNewExpression newExpression = (PsiNewExpression) element;
            if (newExpression.getType() != null && "java.math.BigDecimal"
                    .equals(newExpression.getType().getCanonicalText()) && newExpression.getArgumentList() != null
                    && newExpression.getArgumentList().getExpressions().length == 1
                    && newExpression.getArgumentList().getExpressions()[0] instanceof PsiLiteralExpression) {
                PsiLiteralExpression psiExpression = (PsiLiteralExpression) newExpression.getArgumentList()
                        .getExpressions()[0];
                String value = psiExpression.getText();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                return new Literal(new BigDecimal(value));
            }
        } else if (element instanceof PsiLiteralExpression && "int"
                .equals(((PsiLiteralExpression) element).getType().getCanonicalText())) {
            return new Literal(new BigDecimal(element.getText()));
        }
        return null;
    }
}
