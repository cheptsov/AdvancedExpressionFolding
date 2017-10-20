package com.intellij.advancedExpressionFolding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiAssignmentExpressionImpl;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdvancedExpressionFoldingBuilder extends FoldingBuilderEx {

    private static final FoldingDescriptor[] NO_DESCRIPTORS = new FoldingDescriptor[0];

    private static Set<String> supportedMethods = new HashSet<String>() {
        {
            add("add");
            add("multiply");
            add("divide");
            add("subtract");
            add("remainder");
            /*add("scaleByPowerOfTen");*/
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
            /*add("modInverse");*/
            /*add("modPow");*/
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
            /*add("log1p");*/
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
            /*add("hypot");*/
            add("exp");
            /*add("expm1");*/
            add("append");
            add("substring");
            add("subList");
            add("contains");
            add("containsKey");
            add("get");
            add("isPresent");
            add("charAt");
            add("put");
            add("set");
            add("asList");
            add("singletonList");
            add("addAll");
            add("removeAll");
            add("remove");
            add("collect");
            add("stream");
            add("unmodifiableSet");
            add("unmodifiableList");
            add("toString");
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
            add("java.lang.Character");
            add("java.lang.String");
            add("java.lang.StringBuilder");
            add("java.lang.AbstractStringBuilder");
            add("java.util.List");
            add("java.util.ArrayList");
            add("java.util.Map");
            add("java.util.HashMap");
            add("java.util.Set");
            add("java.util.HashSet");
            add("java.lang.Object");
            add("java.util.Arrays");
            add("java.util.Optional");
            add("java.util.Collection");
            add("java.util.Collections");
            add("java.util.Objects");
            add("java.util.stream.Stream");
        }
    };

    private static Set<String> unsupportedClassesMethodsExceptions = new HashSet<String>() {
        {
            add("equals");
            add("compareTo");
        }
    };

    private static Set<String> supportedPrimitiveTypes = new HashSet<String>() {
        {
            add("int");
            add("long");
            add("float");
            add("double");
            add("char");
            add("java.lang.String");
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

    @Nullable
    private static Expression getForStatementExpression(@NotNull PsiForStatement element, @NotNull Document document) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        @Nullable PsiJavaToken lParenth = element.getLParenth();
        @Nullable PsiJavaToken rParenth = element.getRParenth();
        @Nullable PsiStatement initialization = element.getInitialization();
        @Nullable PsiStatement update = element.getUpdate();
        @Nullable PsiExpression condition = element.getCondition();
        if (settings.getState().isRangeExpressionsCollapse()
            && lParenth != null && rParenth != null
                && initialization instanceof PsiDeclarationStatement
                && ((PsiDeclarationStatement) initialization).getDeclaredElements().length == 1
                && ((PsiDeclarationStatement) initialization).getDeclaredElements()[0] instanceof PsiVariable
                && ((PsiVariable) ((PsiDeclarationStatement) initialization).getDeclaredElements()[0]).getInitializer() != null
                && update != null && update.getChildren().length == 1
                && update.getChildren()[0] instanceof PsiPostfixExpression
                && ((PsiPostfixExpression) update.getChildren()[0]).getOperand() instanceof PsiReferenceExpression
                && ((PsiPostfixExpression) update.getChildren()[0]).getOperationSign().getText().equals("++")
                && ((PsiPostfixExpression) update.getChildren()[0]).getOperand().getReference() != null
                && condition instanceof PsiBinaryExpression
                && ((PsiBinaryExpression) condition).getLOperand() instanceof PsiReferenceExpression
                && ((PsiBinaryExpression) condition).getLOperand().getReference() != null
                && ((PsiBinaryExpression) condition).getROperand() != null) {
            @SuppressWarnings("ConstantConditions")
            @Nullable PsiVariable updateVariable = (PsiVariable) ((PsiPostfixExpression) update.getChildren()[0]).getOperand().getReference().resolve();
            @SuppressWarnings("ConstantConditions")
            @Nullable PsiExpression conditionROperand = ((PsiBinaryExpression) condition).getROperand();
            @Nullable PsiReference reference = ((PsiBinaryExpression) condition).getLOperand().getReference();
            if (reference != null) {
                PsiVariable conditionVariable = (PsiVariable) reference.resolve();
                if (updateVariable != null && conditionROperand != null
                        && updateVariable == ((PsiDeclarationStatement) initialization).getDeclaredElements()[0]
                        && updateVariable == conditionVariable
                        && ("int".equals(updateVariable.getType().getCanonicalText())
                        || "long".equals(updateVariable.getType().getCanonicalText()))) {
                    Optional<PsiElement> identifier = Stream.of(((PsiDeclarationStatement) initialization).getDeclaredElements()[0].getChildren())
                            .filter(c -> c instanceof PsiIdentifier).findAny();
                    if (identifier.isPresent()) {
                        Variable variable = new Variable(identifier.get(), identifier.get().getTextRange(), null, identifier.get().getText(), false);
                        //noinspection ConstantConditions
                        @NotNull Expression start = getAnyExpression(
                                ((PsiVariable) ((PsiDeclarationStatement) initialization).getDeclaredElements()[0]).getInitializer(), document);
                        @NotNull Expression end = getAnyExpression(conditionROperand, document);
                        String sign = ((PsiBinaryExpression) condition).getOperationSign().getText();
                        if ("<".equals(sign) || "<=".equals(sign)) {
                            if (element.getBody() instanceof PsiBlockStatement
                                    && ((PsiBlockStatement) element.getBody()).getCodeBlock().getStatements().length > 0
                                    && ((PsiBlockStatement) element.getBody()).getCodeBlock().getStatements()[0] instanceof PsiDeclarationStatement
                                    && ((PsiDeclarationStatement) ((PsiBlockStatement) element.getBody()).getCodeBlock()
                                    .getStatements()[0]).getDeclaredElements().length == 1) {
                                if (start instanceof NumberLiteral && ((NumberLiteral) start).getNumber().equals(0)) {
                                    PsiVariable declaration = (PsiVariable) ((PsiDeclarationStatement) ((PsiBlockStatement) element.getBody())
                                            .getCodeBlock()
                                            .getStatements()[0]).getDeclaredElements()[0];
                                    @Nullable PsiIdentifier variableName = declaration.getNameIdentifier();
                                    @Nullable PsiExpression initializer = declaration.getInitializer();
                                    if (variableName != null
                                            && initializer instanceof PsiArrayAccessExpression
                                            && ((PsiArrayAccessExpression) initializer).getIndexExpression() instanceof PsiReferenceExpression
                                            && isReferenceTo(((PsiReferenceExpression) ((PsiArrayAccessExpression) initializer).getIndexExpression()), conditionVariable)
                                            && conditionROperand instanceof PsiReferenceExpression
                                            && ((PsiReferenceExpression) conditionROperand).getQualifierExpression() instanceof PsiReferenceExpression
                                            && ((PsiArrayAccessExpression) initializer).getArrayExpression() instanceof PsiReferenceExpression
                                            && isReferenceTo((PsiReferenceExpression) ((PsiReferenceExpression) conditionROperand).getQualifierExpression(),
                                            ((PsiReferenceExpression) ((PsiArrayAccessExpression) initializer).getArrayExpression()).resolve())) {
                                        // TODO: ((PsiArrayAccessExpression) initializer).getArrayExpression() can be a method call expression, e.g. getArgs()
                                        PsiExpression arrayExpression = ((PsiArrayAccessExpression) initializer)
                                                .getArrayExpression();
                                        List<PsiElement> references = SyntaxTraverser.psiTraverser(element.getBody()).filter(e -> e instanceof PsiReferenceExpression
                                                && ((PsiReferenceExpression) e).isReferenceTo(conditionVariable)).toList();
                                        //noinspection Duplicates
                                        if (references.size() == 1) {
                                            return new ForEachStatement(element, TextRange.create(
                                                    initialization.getTextRange().getStartOffset(),
                                                    declaration.getTextRange().getEndOffset()),
                                                    declaration.getTextRange(), variableName.getTextRange(),
                                                    arrayExpression.getTextRange()
                                            );
                                        } else {
                                            @Nullable PsiIdentifier indexName = conditionVariable.getNameIdentifier();
                                            boolean isFinal = calculateIfFinal(declaration) && calculateIfFinal(updateVariable);
                                            if (indexName != null) {
                                                return new ForEachIndexedStatement(element, TextRange.create(
                                                        initialization.getTextRange().getStartOffset() - 1,
                                                        declaration.getTextRange().getEndOffset()),
                                                        declaration.getTextRange(),
                                                        indexName.getTextRange(), variableName.getTextRange(),
                                                        arrayExpression.getTextRange(),
                                                        settings.getState().isVarExpressionsCollapse(),
                                                        isFinal);
                                            }
                                        }
                                    } else if (variableName != null
                                            && initializer instanceof PsiMethodCallExpression
                                            && ((PsiMethodCallExpression) initializer).getArgumentList().getExpressions().length == 1
                                            && ((PsiMethodCallExpression) initializer).getArgumentList().getExpressions()[0] instanceof PsiReferenceExpression
                                            && ((PsiReferenceExpression) ((PsiMethodCallExpression) initializer).getArgumentList().getExpressions()[0]).isReferenceTo(conditionVariable)
                                            && conditionROperand instanceof PsiMethodCallExpression
                                            && ((PsiMethodCallExpression) conditionROperand).getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression
                                            && ((PsiMethodCallExpression) initializer).getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression
                                            && isReferenceToReference((PsiReferenceExpression) ((PsiMethodCallExpression) conditionROperand).getMethodExpression().getQualifierExpression(), ((PsiReferenceExpression) ((PsiMethodCallExpression) initializer).getMethodExpression()
                                            .getQualifierExpression()))) {
                                        @Nullable PsiExpression arrayExpression = ((PsiMethodCallExpression) initializer).getMethodExpression().getQualifierExpression();
                                        if (arrayExpression != null) {
                                            List<PsiElement> references = SyntaxTraverser.psiTraverser(element.getBody()).filter(e -> e instanceof PsiReferenceExpression
                                                    && ((PsiReferenceExpression) e).isReferenceTo(conditionVariable)).toList();
                                            //noinspection Duplicates
                                            if (references.size() == 1) {
                                                return new ForEachStatement(element, TextRange.create(
                                                        initialization.getTextRange().getStartOffset(),
                                                        declaration.getTextRange().getEndOffset()),
                                                        declaration.getTextRange(), variableName.getTextRange(),
                                                        arrayExpression.getTextRange()
                                                );
                                            } else {
                                                @Nullable PsiIdentifier indexName = conditionVariable.getNameIdentifier();
                                                if (indexName != null) {
                                                    boolean isFinal = calculateIfFinal(declaration) && calculateIfFinal(updateVariable);
                                                    return new ForEachIndexedStatement(element, TextRange.create(
                                                            initialization.getTextRange().getStartOffset() - 1,
                                                            declaration.getTextRange().getEndOffset()),
                                                            declaration.getTextRange(),
                                                            indexName.getTextRange(), variableName.getTextRange(),
                                                            arrayExpression.getTextRange(),
                                                            settings.getState().isVarExpressionsCollapse(),
                                                            isFinal);
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                            int startOffset = lParenth.getTextRange().getStartOffset() + 1;
                            int endOffset = rParenth.getTextRange().getEndOffset() - 1;
                            return new ForStatement(element, TextRange.create(startOffset, endOffset), variable,
                                    start, true, end, "<=".equals(sign));
                        }
                    }
                }
            }
        }
        if (element.getCondition() != null
                && element.getLParenth() != null && element.getRParenth() != null
                && settings.getState().isCompactControlFlowSyntaxCollapse()) {
            return new CompactControlFlowExpression(element,
                    TextRange.create(element.getLParenth().getTextRange().getStartOffset(),
                            element.getRParenth().getTextRange().getEndOffset()));
        }
        return null;
    }

    private static boolean isReferenceTo(@Nullable PsiReferenceExpression referenceExpression, @Nullable PsiElement element) {
        return referenceExpression != null && element != null && referenceExpression.isReferenceTo(element);
    }

    private static boolean isReferenceToReference(@Nullable PsiReferenceExpression referenceExpression, @Nullable PsiReference reference) {
        if (reference != null) {
            @Nullable PsiElement element = reference.resolve();
            return referenceExpression != null && element != null && referenceExpression.isReferenceTo(element);
        } else {
            return false;
        }
    }

    @Contract("_, _, true -> !null")
    private static Expression getExpression(@NotNull PsiElement element, @NotNull Document document, boolean synthetic) {
        if (synthetic) {
            return CachedValuesManager.getCachedValue(element,
                    () -> CachedValueProvider.Result.create(buildExpression(element, document, true),
                            PsiModificationTracker.MODIFICATION_COUNT));
        } else {
            return CachedValuesManager.getCachedValue(element,
                    () -> CachedValueProvider.Result.create(buildExpression(element, document, false),
                            PsiModificationTracker.MODIFICATION_COUNT));
        }
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public static Expression getAnyExpression(@NotNull PsiElement element, @Nullable Document document) throws IndexNotReadyException {
        //noinspection ConstantConditions
        return getExpression(element, document, true);
    }

    /**
     * TODO: Think how we can prevent IndexNotReadyException (e.g. via "is dumb mode")
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public static Expression getNonSyntheticExpression(@NotNull PsiElement element, @Nullable Document document) throws IndexNotReadyException {
        //noinspection ConstantConditions
        return getExpression(element, document, false);
    }

    // 💩💩💩 Define the AdvancedExpressionFoldingProvider extension point
    @Contract("_, _, true -> !null")
    private static Expression buildExpression(@NotNull PsiElement element, @NotNull Document document, boolean synthetic) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (element instanceof PsiForStatement) {
            Expression expression = getForStatementExpression((PsiForStatement) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiForeachStatement) {
            Expression expression = getForeachStatementExpression((PsiForeachStatement) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiIfStatement) {
            Expression expression = getIfExpression((PsiIfStatement) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiWhileStatement) {
            Expression expression = getWhileStatement((PsiWhileStatement) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiJavaToken && ((PsiJavaToken) element).getTokenType() == JavaTokenType.SEMICOLON
                && settings.getState().isSemicolonsCollapse()) {
            return new SemicolonExpression(element, element.getTextRange());
        }
        if (element instanceof PsiCatchSection) {
            Expression expression = getCatchStatement((PsiCatchSection) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiDoWhileStatement) {
            Expression expression = getDoWhileStatement((PsiDoWhileStatement) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiSwitchStatement) {
            Expression expression = getSwitchStatement((PsiSwitchStatement) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiArrayAccessExpression && settings.getState().isGetExpressionsCollapse()) {
            Expression expression = getArrayAccessExpression((PsiArrayAccessExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiMethodCallExpression) {
            Expression expression = getMethodCallExpression((PsiMethodCallExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiReferenceExpression) {
            Expression expression = getReferenceExpression((PsiReferenceExpression) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiNewExpression) {
            Expression expression = getNewExpression((PsiNewExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiLiteralExpression) {
            Expression expression = getLiteralExpression((PsiLiteralExpression) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiAssignmentExpression) {
            Expression expression = getAssignmentExpression((PsiAssignmentExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiPolyadicExpression) {
            Expression expression = getPolyadicExpression((PsiPolyadicExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiBinaryExpression) {
            Expression expression = getBinaryExpression((PsiBinaryExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiConditionalExpression) {
            Expression expression = getConditionalExpression((PsiConditionalExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiPrefixExpression) {
            Expression expression = getPrefixExpression((PsiPrefixExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiParenthesizedExpression) {
            if (((PsiParenthesizedExpression) element).getExpression() instanceof PsiTypeCastExpression) {
                PsiTypeCastExpression e = (PsiTypeCastExpression) ((PsiParenthesizedExpression) element).getExpression();
                if (e != null) {
                    TypeCast typeCast = getTypeCastExpression(e, document);
                    if (typeCast != null) {
                        return new TypeCast(element, element.getTextRange(), typeCast.getObject());
                    }
                }
            }
            @Nullable PsiExpression e = ((PsiParenthesizedExpression) element).getExpression();
            if (e != null) {
                @Nullable Expression expression = getExpression(e, document, synthetic);
                if (expression != null) {
                    return expression;
                }
            }
        }
        if (element instanceof PsiTypeCastExpression) {
            TypeCast expression = getTypeCastExpression((PsiTypeCastExpression) element, document);
            if (expression != null) {
                return expression;
            }
        }
        if (settings.getState().isVarExpressionsCollapse()
                && element instanceof PsiVariable
                && (element.getParent() instanceof PsiDeclarationStatement
                || element.getParent() instanceof PsiForeachStatement)) {
            Expression expression = getVariableDeclaration((PsiVariable) element);
            if (expression != null) {
                return expression;
            }
        }
        if (element instanceof PsiCodeBlock) {
            Expression expression = getCodeBlockExpression((PsiCodeBlock) element);
            if (expression != null) {
                return expression;
            }
        }
        if (synthetic) {
            ArrayList<Expression> children = new ArrayList<>();
            findChildExpressions(element, children, document);
            return new SyntheticExpressionImpl(element, element.getTextRange(), document.getText(element.getTextRange()), children);
        }
        return null;
    }

    private static Expression getForeachStatementExpression(PsiForeachStatement element) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (element.getIteratedValue() != null && element.getRParenth() != null &&
                settings.getState().isCompactControlFlowSyntaxCollapse()) {
            return new CompactControlFlowExpression(element,
                    TextRange.create(element.getLParenth().getTextRange().getStartOffset(),
                            element.getRParenth().getTextRange().getEndOffset()));
        }
        return null;
    }

    private static Expression getWhileStatement(PsiWhileStatement element) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (element.getCondition() != null
                && element.getLParenth() != null && element.getRParenth() != null
                && settings.getState().isCompactControlFlowSyntaxCollapse()) {
            return new CompactControlFlowExpression(element,
                    TextRange.create(element.getLParenth().getTextRange().getStartOffset(),
                            element.getRParenth().getTextRange().getEndOffset()));
        }
        return null;
    }

    private static Expression getDoWhileStatement(PsiDoWhileStatement element) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (element.getCondition() != null
                && element.getLParenth() != null && element.getRParenth() != null
                && settings.getState().isCompactControlFlowSyntaxCollapse()) {
            return new CompactControlFlowExpression(element,
                    TextRange.create(element.getLParenth().getTextRange().getStartOffset(),
                            element.getRParenth().getTextRange().getEndOffset()));
        }
        return null;
    }

    private static Expression getSwitchStatement(PsiSwitchStatement element) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (element.getExpression() != null
                && element.getLParenth() != null && element.getRParenth() != null
                && settings.getState().isCompactControlFlowSyntaxCollapse()) {
            return new CompactControlFlowExpression(element,
                    TextRange.create(element.getLParenth().getTextRange().getStartOffset(),
                            element.getRParenth().getTextRange().getEndOffset()));
        }
        return null;
    }

    private static Expression getCatchStatement(PsiCatchSection element) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (element.getParameter() != null
                && element.getLParenth() != null && element.getRParenth() != null
                && settings.getState().isCompactControlFlowSyntaxCollapse()) {
            return new CompactControlFlowExpression(element,
                    TextRange.create(element.getLParenth().getTextRange().getStartOffset(),
                            element.getRParenth().getTextRange().getEndOffset()));
        }
        return null;
    }

    private static Expression getCodeBlockExpression(PsiCodeBlock element) {
        PsiElement parent = element.getParent();
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (parent instanceof PsiBlockStatement
                && (
                (parent.getParent() instanceof PsiIfStatement || parent.getParent() instanceof PsiLoopStatement)
                        && element.getRBrace() != null
                        && element.getLBrace() != null
        )
                || parent instanceof PsiSwitchStatement
                || parent instanceof PsiTryStatement
                || parent instanceof PsiCatchSection) {
            if (element.getStatements().length == 1 || parent instanceof PsiSwitchStatement) {
                if (settings.getState().isControlFlowSingleStatementCodeBlockCollapse()
                        && !settings.getState().isAssertsCollapse()) {
                    // TODO: Find another way to avoid colliding "control flow single statement" and "assert"
                    return new ControlFlowSingleStatementCodeBlockExpression(element, element.getTextRange());
                }
            } else {
                if (settings.getState().isControlFlowMultiStatementCodeBlockCollapse()) {
                    return new ControlFlowMultiStatementCodeBlockExpression(element, element.getTextRange());
                }
            }
        }
        return null;
    }

    @Nullable
    private static Expression getArrayAccessExpression(@NotNull PsiArrayAccessExpression element, @NotNull Document document) {
        @Nullable PsiExpression index = element.getIndexExpression();
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (!(element.getParent() instanceof PsiAssignmentExpression
                && ((PsiAssignmentExpressionImpl) element.getParent()).getLExpression() == element) && index != null && settings.getState().isGetExpressionsCollapse()) {
            /*@Nullable Expression indexExpression = getNonSyntheticExpression(index, document);*/
            @NotNull Expression arrayExpression = getAnyExpression(element.getArrayExpression(), document);
            /*if (indexExpression instanceof NumberLiteral && ((NumberLiteral) indexExpression).getNumber().equals(0)) {
                return new ArrayGet(element, element.getTextRange(), arrayExpression, ArrayGet.Style.FIRST);
            } else */
            if (index instanceof PsiBinaryExpression) {
                PsiBinaryExpression a2b = (PsiBinaryExpression) index;
                NumberLiteral position = getSlicePosition(element, arrayExpression, a2b, document);
                if (position != null && position.getNumber().equals(-1)) {
                    return new ArrayGet(element, element.getTextRange(), arrayExpression/*, ArrayGet.Style.LAST*/);
                }
            }
        }
        return null;
    }

    @Nullable
    private static Expression getIfExpression(PsiIfStatement element, Document document) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (settings.getState().isCheckExpressionsCollapse()
                && element.getCondition() instanceof PsiBinaryExpression) {
            PsiBinaryExpression condition = (PsiBinaryExpression) element.getCondition();
            if (condition.getOperationSign().getText().equals("!=")
                    && element.getElseBranch() == null
                    && (condition.getLOperand().getType() == PsiType.NULL
                    && condition.getROperand() != null
                    || condition.getROperand() != null && condition.getROperand().getType() == PsiType.NULL)
                    && element.getThenBranch() != null) {
                PsiStatement thenStatement = element.getThenBranch();
                if (thenStatement.getChildren().length == 1 && thenStatement
                        .getChildren()[0] instanceof PsiCodeBlock) {
                    PsiStatement[] statements = ((PsiCodeBlock) thenStatement.getChildren()[0]).getStatements();
                    if (statements.length == 1) {
                        thenStatement = statements[0];
                    } else {
                        return null;
                    }
                }
                PsiElement qualifier = condition.getLOperand().getType() == PsiType.NULL
                        ? condition.getROperand()
                        : condition.getLOperand();
                if (qualifier instanceof PsiReferenceExpression
                        || (qualifier instanceof PsiMethodCallExpression
                        && startsWith(((PsiMethodCallExpression) qualifier).getMethodExpression().getReferenceName(), "get")
                        && ((PsiMethodCallExpression) qualifier).getArgumentList().getExpressions().length == 0)) {
                    PsiElement r = findSameQualifier(thenStatement, qualifier);
                    if (r != null) {
                        return new ShortElvisExpression(element, element.getTextRange(),
                                getAnyExpression(thenStatement, document),
                                Collections.singletonList(r.getTextRange()));
                    }
                }
            }
        }
        /*if (element.getCondition() != null
                && element.getLParenth() != null && element.getRParenth() != null) {
            return new CompactControlFlowExpression(element,
                    TextRange.create(element.getLParenth().getTextRange().getStartOffset(),
                            element.getRParenth().getTextRange().getEndOffset()));
        }*/
        return new IfExpression(element, element.getTextRange());
    }

    @Nullable
    private static PsiElement findSameQualifier(@NotNull PsiElement element, @NotNull PsiElement qualifier) {
        if (element instanceof PsiStatement && element.getFirstChild() != null) {
            return findSameQualifier(element.getFirstChild(), qualifier);
        }
        if (equal(qualifier, element)) {
            return element;
        }
        if (element instanceof PsiMethodCallExpression && ((PsiMethodCallExpression) element).getMethodExpression().getQualifierExpression() != null) {
            @Nullable PsiExpression q = ((PsiMethodCallExpression) element).getMethodExpression().getQualifierExpression();
            if (q != null) {
                return findSameQualifier(q, qualifier);
            }
        }
        if (element instanceof PsiReferenceExpression && ((PsiReferenceExpression) element).getQualifierExpression() != null) {
            PsiExpression q = ((PsiReferenceExpression) element).getQualifierExpression();
            if (q != null) {
                return findSameQualifier(q, qualifier);
            }
        }
        return null;
    }

    @Nullable
    private static Expression getConditionalExpression(@NotNull PsiConditionalExpression element, @NotNull Document document) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (settings.getState().isCheckExpressionsCollapse()
                && element.getCondition() instanceof PsiBinaryExpression) {
            @NotNull PsiBinaryExpression condition = (PsiBinaryExpression) element.getCondition();
            if (condition.getOperationSign().getText().equals("!=")
                    && condition.getROperand() != null
                    && (condition.getLOperand().getType() == PsiType.NULL
                    || condition.getROperand().getType() == PsiType.NULL)
                    && element.getThenExpression() != null
                    && element.getElseExpression() != null) {
                PsiElement qualifier = condition.getLOperand().getType() == PsiType.NULL
                        ? condition.getROperand()
                        : condition.getLOperand();
                if (qualifier instanceof PsiReferenceExpression
                        || (qualifier instanceof PsiMethodCallExpression
                        && startsWith(((PsiMethodCallExpression) qualifier).getMethodExpression().getReferenceName(), "get")
                        && ((PsiMethodCallExpression) qualifier).getArgumentList().getExpressions().length == 0)) {
                    PsiReferenceExpression r = qualifier instanceof PsiReferenceExpression
                            ? ((PsiReferenceExpression) qualifier)
                            : ((PsiMethodCallExpression) qualifier).getMethodExpression();
                    List<PsiElement> references = SyntaxTraverser.psiTraverser(element.getThenExpression())
                            .filter(e ->
                                    e instanceof PsiReferenceExpression
                                            && !(e.getParent() instanceof PsiMethodCallExpression)
                                            && ((PsiReferenceExpression) e).isReferenceTo(r.resolve())
                                            || e instanceof PsiMethodCallExpression && ((PsiMethodCallExpression) e).getMethodExpression().isReferenceTo(r.resolve())
                            ).toList();
                    if (references.size() > 0) {
                        return new ElvisExpression(element, element.getTextRange(),
                                getAnyExpression(element.getThenExpression(), document),
                                getAnyExpression(element.getElseExpression(), document),
                                references.stream().map(PsiElement::getTextRange).collect(Collectors.toList()));
                    }
                }
            }
        }
        return null;
    }

    private static boolean startsWith(@Nullable String string, @NotNull String prefix) {
        return string != null && string.startsWith(prefix);
    }

    private static boolean equal(@Nullable PsiElement e1, @Nullable PsiElement e2) {
        // TODO: Use a cache for the resolved instance
        if (e2 instanceof PsiReferenceExpression && e1 instanceof PsiReferenceExpression) {
            return Objects.equals(((PsiReferenceExpression) e2).getReferenceName(), ((PsiReferenceExpression) e1).getReferenceName())
                    && ((PsiReferenceExpression) e2).isReferenceTo(((PsiReferenceExpression) e1).resolve());
        } else if (e2 instanceof PsiMethodCallExpression && e1 instanceof PsiMethodCallExpression) {
            return equal(((PsiMethodCallExpression) e2).getMethodExpression(),
                    ((PsiMethodCallExpression) e1).getMethodExpression())
                    && equal(((PsiMethodCallExpression) e2).getMethodExpression().getQualifierExpression(),
                    ((PsiMethodCallExpression) e1).getMethodExpression().getQualifierExpression());
        }
        return false;
    }

    @Nullable
    private static VariableDeclarationImpl getVariableDeclaration(@NotNull PsiVariable element) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (settings.getState().isVarExpressionsCollapse()
                && element.getName() != null
                && element.getTypeElement() != null
                && (element.getInitializer() != null || element.getParent() instanceof PsiForeachStatement)
                && element.getTextRange().getStartOffset() < element.getTypeElement().getTextRange().getEndOffset()) {
            boolean isFinal = calculateIfFinal(element);
            return new VariableDeclarationImpl(element, TextRange.create(
                    element.getTextRange().getStartOffset(),
                    element.getTypeElement().getTextRange().getEndOffset()),
                    element.getModifierList() != null && isFinal);
        }
        return null;
    }

    private static boolean calculateIfFinal(@NotNull PsiVariable element) {
        PsiModifierList modifiers = element.getModifierList();
        if (modifiers != null) {
            boolean isFinal = modifiers.hasExplicitModifier(PsiModifier.FINAL);
            if (!isFinal) {
                PsiElement body = element.getParent() instanceof PsiDeclarationStatement
                        ? element.getParent().getParent()
                        : element.getParent() instanceof PsiLoopStatement
                        ? ((PsiLoopStatement) element.getParent()).getBody()
                        : element.getParent();
                if (body instanceof PsiLoopStatement) {
                    body = ((PsiLoopStatement) body).getBody();
                }
                List<PsiElement> references = SyntaxTraverser.psiTraverser(body)
                        .filter(e ->
                                e instanceof PsiAssignmentExpression
                                        && ((PsiAssignmentExpression) e).getLExpression() instanceof PsiReferenceExpression
                                        && ((PsiReferenceExpression) ((PsiAssignmentExpression) e).getLExpression()).isReferenceTo(element)
                                        || e instanceof PsiPostfixExpression
                                        && (((PsiPostfixExpression) e).getOperationSign().getText().equals("++")
                                        || ((PsiPostfixExpression) e).getOperationSign().getText().equals("--"))
                                        && ((PsiPostfixExpression) e).getOperand() instanceof PsiReferenceExpression
                                        && ((PsiReferenceExpression) ((PsiPostfixExpression) e).getOperand()).isReferenceTo(element)
                        ).toList();
                if (references.size() == 0) {
                    isFinal = true;
                }
            }
            return isFinal;
        }
        return false;
    }

    private static void findChildExpressions(@NotNull PsiElement element, @NotNull List<Expression> expressions, @NotNull Document document) {
        for (PsiElement child : element.getChildren()) {
            @Nullable Expression expression = getNonSyntheticExpression(child, document);
            if (expression != null) {
                expressions.add(expression);
            }
            if (expression == null || !expression.getTextRange().equals(child.getTextRange())) {
                findChildExpressions(child, expressions, document);
            }
        }
    }

    @Nullable
    private static TypeCast getTypeCastExpression(@NotNull PsiTypeCastExpression expression, @NotNull Document document) {
        PsiExpression operand = expression.getOperand();
        return operand != null
                ? new TypeCast(expression, expression.getTextRange(), getAnyExpression(operand, document))
                : null;
    }

    @Nullable
    private static Expression getLiteralExpression(@NotNull PsiLiteralExpression element) {
        if (element.getType() != null) {
            if (supportedPrimitiveTypes.contains(element.getType().getCanonicalText())) {
                Object value = element.getValue();
                if (value instanceof Number) {
                    return new NumberLiteral(element, element.getTextRange(), null, (Number) value, false);
                } else if (value instanceof String) {
                    return new StringLiteral(element, element.getTextRange(), (String) value);
                } else if (value instanceof Character) {
                    return new CharacterLiteral(element, element.getTextRange(), (Character) value);
                }
            }
        }
        return null;
    }

    @Nullable
    private static Expression getPrefixExpression(@NotNull PsiPrefixExpression element, @NotNull Document document) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (element.getOperand() != null) {
            if (element.getOperationSign().getText().equals("!") && settings.getState().isComparingExpressionsCollapse()) {
                @NotNull Expression operand = getAnyExpression(element.getOperand(), document);
                if (operand instanceof Equal) {
                    return new NotEqual(element, element.getTextRange(), ((Equal) operand).getOperands());
                }
            } else if (element.getOperationSign().getText().equals("-")) {
                @NotNull Expression operand = getAnyExpression(element.getOperand(), document);
                return new Negate(element, element.getTextRange(), Collections.singletonList(operand));
            }
        }
        return null;
    }

    @Nullable
    private static Expression getPolyadicExpression(@NotNull PsiPolyadicExpression element, @NotNull Document document) {
        boolean add = true;
        boolean string = false;
        Expression[] operands = null;
        for (int i = 0; i < element.getOperands().length - 1; i++) {
            PsiExpression a = element.getOperands()[i];
            PsiExpression b = element.getOperands()[i + 1];
            PsiJavaToken token = element.getTokenBeforeOperand(b);
            if (token != null) {
                if ("&&".equals(token.getText())
                        && a instanceof PsiBinaryExpression
                        && b instanceof PsiBinaryExpression) {
                    Expression twoBinaryExpression = getAndTwoBinaryExpressions(element,
                            ((PsiBinaryExpression) a), ((PsiBinaryExpression) b), document);
                    if (twoBinaryExpression != null) {
                        return twoBinaryExpression;
                    }
                }
                if (add && "+".equals(token.getText())) {
                    if (operands == null) {
                        operands = new Expression[element.getOperands().length];
                    }
                    operands[i] = getAnyExpression(element.getOperands()[i], document);
                    if (operands[i] instanceof StringLiteral) {
                        string = true;
                    }
                } else {
                    add = false;
                }
            }
        }
        if (add && operands != null) {
            operands[element.getOperands().length - 1] = getAnyExpression(
                    element.getOperands()[element.getOperands().length - 1], document);
            if (operands[element.getOperands().length - 1] instanceof StringLiteral) {
                string = true;
            }
        }
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (add && operands != null && string && settings.getState().isConcatenationExpressionsCollapse()) {
            return new InterpolatedString(element, element.getTextRange(), Arrays.asList(operands));
        } else if (add && operands != null) {
            return new Add(element, element.getTextRange(), Arrays.asList(operands)); // TODO: Support other operations as well
        }
        if (element instanceof PsiBinaryExpression) {
            Expression binaryExpression = getBinaryExpression((PsiBinaryExpression) element, document);
            if (binaryExpression != null) {
                return binaryExpression;
            }
        }
        return null;
    }

    @Nullable
    private static Expression getAndTwoBinaryExpressions(@NotNull PsiElement parent, @NotNull PsiBinaryExpression a,
                                                         @NotNull PsiBinaryExpression b, @NotNull Document document) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if (settings.getState().isRangeExpressionsCollapse()) {
            if ((a.getOperationSign().getText().equals("<") || a.getOperationSign().getText().equals("<="))
                    && (b.getOperationSign().getText().equals(">") || b.getOperationSign().getText().equals(">="))
                    && a.getROperand() != null
                    && b.getROperand() != null) //noinspection Duplicates
            {
                @NotNull Expression e1 = getAnyExpression(a.getLOperand(), document);
                @NotNull Expression e2 = getAnyExpression(a.getROperand(), document);
                @NotNull Expression e3 = getAnyExpression(b.getLOperand(), document);
                @NotNull Expression e4 = getAnyExpression(b.getROperand(), document);
                if (/*e1 instanceof Variable && e3 instanceof Variable && */e1.equals(e3)) {
                    return new Range(parent, TextRange.create(a.getTextRange().getStartOffset(),
                            b.getTextRange().getEndOffset()), e1,
                            e4, b.getOperationSign().getText().equals(">="), e2, a.getOperationSign().getText().equals("<="));
                }
            }
            if ((a.getOperationSign().getText().equals(">") || a.getOperationSign().getText().equals(">="))
                    && (b.getOperationSign().getText().equals("<") || b.getOperationSign().getText().equals("<="))
                    && a.getROperand() != null
                    && b.getROperand() != null) //noinspection Duplicates
            {
                @NotNull Expression e1 = getAnyExpression(a.getLOperand(), document);
                @NotNull Expression e2 = getAnyExpression(a.getROperand(), document);
                @NotNull Expression e3 = getAnyExpression(b.getLOperand(), document);
                @NotNull Expression e4 = getAnyExpression(b.getROperand(), document);
                if (/*e1 instanceof Variable && e3 instanceof Variable && */e1.equals(e3)) {
                    return new Range(parent, TextRange.create(a.getTextRange().getStartOffset(),
                            b.getTextRange().getEndOffset()), e1,
                            e2, a.getOperationSign().getText().equals(">="), e4, b.getOperationSign().getText().equals("<="));
                }
            }
        }
        return null;
    }

    @Nullable
    private static Expression getBinaryExpression(@NotNull PsiBinaryExpression element, @NotNull Document document) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        if ((element.getLOperand() instanceof PsiMethodCallExpression
                && isLiteralOrNegatedLiteral(element.getROperand())
                || element.getROperand() instanceof PsiMethodCallExpression &&
                isLiteralOrNegatedLiteral(element.getLOperand()))
                && settings.getState().isComparingExpressionsCollapse()) {
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) (element
                    .getLOperand() instanceof PsiMethodCallExpression
                    ? element.getLOperand() : element.getROperand());

            @Nullable PsiExpression literalExpression = isLiteralOrNegatedLiteral(element.getLOperand())
                    ? element.getLOperand() : element.getROperand();
            if (literalExpression != null && literalExpression.getText().equals("0")
                    || literalExpression != null && literalExpression.getText().equals("-1")
                    || literalExpression != null && literalExpression.getText().equals("1")) {
                @NotNull Optional<PsiElement> identifier = Stream.of(methodCallExpression.getMethodExpression().getChildren())
                        .filter(c -> c instanceof PsiIdentifier).findAny();
                if (identifier.isPresent() && identifier.get().getText().equals("compareTo") && methodCallExpression.getArgumentList().getExpressions().length == 1) {
                    @Nullable PsiMethod method = (PsiMethod) methodCallExpression.getMethodExpression().resolve();
                    if (method != null) {
                        @Nullable PsiClass psiClass = method.getContainingClass();
                        if (psiClass != null && (psiClass.getQualifiedName() != null && supportedClasses.contains(eraseGenerics(psiClass.getQualifiedName()))
                                || unsupportedClassesMethodsExceptions.contains(method.getName()))) {
                            @Nullable Expression qualifier = methodCallExpression.getMethodExpression()
                                    .getQualifierExpression() != null ? getAnyExpression(methodCallExpression.getMethodExpression()
                                    .getQualifierExpression(), document) : null;
                            if (qualifier != null) {
                                @NotNull Expression argument = getAnyExpression(methodCallExpression.getArgumentList()
                                        .getExpressions()[0], document);
                                switch (element.getOperationSign().getText()) {
                                    case "==":
                                        switch (literalExpression.getText()) {
                                            case "-1":
                                                return new Less(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                            case "0":
                                                return new Equal(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                            case "1":
                                                return new Greater(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                        }
                                    case "!=":
                                        switch (literalExpression.getText()) {
                                            case "1":
                                                return new LessEqual(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                            case "0":
                                                return new NotEqual(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                            case "-1":
                                                return new GreaterEqual(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                        }
                                    case "<":
                                        switch (literalExpression.getText()) {
                                            case "1":
                                                return new LessEqual(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                            case "0":
                                                return new Less(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                        }
                                    case ">":
                                        switch (literalExpression.getText()) {
                                            case "-1":
                                                return new GreaterEqual(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                            case "0":
                                                return new Greater(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                        }
                                    case "<=":
                                        switch (literalExpression.getText()) {
                                            case "0":
                                                return new LessEqual(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                        }
                                    case ">=":
                                        switch (literalExpression.getText()) {
                                            case "0":
                                                return new GreaterEqual(element, element.getTextRange(), Arrays.asList(qualifier, argument));
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (supportedBinaryOperators.contains(element.getOperationSign().getText()) && element.getROperand() != null) {
            @NotNull Expression leftExpression = getAnyExpression(element.getLOperand(), document);
            @NotNull Expression rightExpression = getAnyExpression(element.getROperand(), document);
            switch (element.getOperationSign().getText()) {
                case "+":
                    return new Add(element, element.getTextRange(), Arrays.asList(leftExpression, rightExpression));
                case "-":
                    return new Subtract(element, element.getTextRange(), Arrays.asList(leftExpression, rightExpression));
                case "*":
                    return new Multiply(element, element.getTextRange(), Arrays.asList(leftExpression, rightExpression));
                case "/":
                    return new Divide(element, element.getTextRange(), Arrays.asList(leftExpression, rightExpression));
            }
        }
        if ("&&".equals(element.getOperationSign().getText())
                && element.getLOperand() instanceof PsiBinaryExpression
                && element.getROperand() instanceof PsiBinaryExpression) {
            return getAndTwoBinaryExpressions(element,
                    ((PsiBinaryExpression) element.getLOperand()), ((PsiBinaryExpression) element.getROperand()), document);
        }
        return null;
    }

    private static boolean isLiteralOrNegatedLiteral(PsiElement element) {
        return element instanceof PsiLiteralExpression
                || element instanceof PsiPrefixExpression
                && ((PsiPrefixExpression) element).getOperand() instanceof PsiLiteralExpression
                && "-".equals(((PsiPrefixExpression) element).getOperationSign().getText());
    }

    @Nullable
    private static Expression getAssignmentExpression(PsiAssignmentExpression element, @Nullable Document document) {
        Variable leftVariable = getVariableExpression(element.getLExpression());
        if (leftVariable != null && element.getRExpression() != null) {
            @NotNull Expression leftExpression = getAnyExpression(element.getRExpression(), document);
            if (leftExpression instanceof Operation) {
                Operation operation = (Operation) leftExpression;
                if (operation.getOperands().size() >= 2 && operation.getOperands().get(0).equals(leftVariable)) {
                    if (operation instanceof Add) {
                        return new AddAssign(element, element.getTextRange(),
                                Arrays.asList(leftVariable, operation.getOperands().size() > 2 ?
                                        new Add(element, TextRange.create(operation.getOperands().get(1).getTextRange().getStartOffset(),
                                                operation.getOperands().get(operation.getOperands().size() - 1).getTextRange().getEndOffset()), operation.getOperands()
                                                .subList(1, operation.getOperands().size())) : operation
                                        .getOperands().get(1)));
                    } else if (operation instanceof Subtract) {
                        return new SubtractAssign(element, element.getTextRange(),
                                Arrays.asList(leftVariable, operation.getOperands().size() > 2 ?
                                        new Add(element, TextRange.create(operation.getOperands().get(1).getTextRange().getStartOffset(),
                                                operation.getOperands().get(operation.getOperands().size() - 1).getTextRange().getEndOffset()), operation.getOperands()
                                                .subList(1, operation.getOperands().size())) : operation
                                        .getOperands().get(1)));
                    } else if (operation instanceof And) {
                        return new AndAssign(element, element.getTextRange(),
                                Arrays.asList(leftVariable, operation.getOperands().size() > 2 ?
                                        new And(element, TextRange.create(operation.getOperands().get(1).getTextRange().getStartOffset(),
                                                operation.getOperands().get(operation.getOperands().size() - 1).getTextRange().getEndOffset()), operation.getOperands()
                                                .subList(1, operation.getOperands().size())) : operation
                                        .getOperands().get(1)));
                    } else if (operation instanceof Or) {
                        return new AndAssign(element, element.getTextRange(),
                                Arrays.asList(leftVariable, operation.getOperands().size() > 2 ?
                                        new Or(element, TextRange.create(operation.getOperands().get(1).getTextRange().getStartOffset(),
                                                operation.getOperands().get(operation.getOperands().size() - 1).getTextRange().getEndOffset()), operation.getOperands()
                                                .subList(1, operation.getOperands().size())) : operation
                                        .getOperands().get(1)));
                    } else if (operation instanceof Xor) {
                        return new AndAssign(element, element.getTextRange(),
                                Arrays.asList(leftVariable, operation.getOperands().size() > 2 ?
                                        new Xor(element, TextRange.create(operation.getOperands().get(1).getTextRange().getStartOffset(),
                                                operation.getOperands().get(operation.getOperands().size() - 1).getTextRange().getEndOffset()), operation.getOperands()
                                                .subList(1, operation.getOperands().size())) : operation
                                        .getOperands().get(1)));
                    } else if (operation instanceof Multiply) {
                        return new MultiplyAssign(element, element.getTextRange(),
                                Arrays.asList(leftVariable, operation.getOperands().size() > 2 ?
                                        new Multiply(element, TextRange.create(operation.getOperands().get(1).getTextRange().getStartOffset(),
                                                operation.getOperands().get(operation.getOperands().size() - 1).getTextRange().getEndOffset()), operation.getOperands()
                                                .subList(1, operation.getOperands().size())) : operation
                                        .getOperands().get(1)));
                    } else if (operation instanceof Divide) {
                        return new DivideAssign(element, element.getTextRange(),
                                Arrays.asList(leftVariable, operation.getOperands().size() > 2 ?
                                        new Multiply(element, TextRange.create(operation.getOperands().get(1).getTextRange().getStartOffset(),
                                                operation.getOperands().get(operation.getOperands().size() - 1).getTextRange().getEndOffset()), operation.getOperands()
                                                .subList(1, operation.getOperands().size())) : operation
                                        .getOperands().get(1)));
                    } else if (operation instanceof ShiftRight && operation.getOperands().size() == 2) {
                        return new ShiftRightAssign(element, element.getTextRange(),
                                Arrays.asList(leftVariable, operation.getOperands().get(1)));
                    } else if (operation instanceof ShiftLeft && operation.getOperands().size() == 2) {
                        return new ShiftLeftAssign(element, element.getTextRange(),
                                Arrays.asList(leftVariable, operation.getOperands().get(1)));
                    } else if (operation instanceof Remainder && operation.getOperands().size() == 2) {
                        return new RemainderAssign(element, element.getTextRange(),
                                Arrays.asList(leftVariable, operation.getOperands().get(1)));
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private static Expression getNewExpression(PsiNewExpression element, @NotNull Document document) {
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        @Nullable PsiType type = element.getType();

        @Nullable String erasedType = type != null ? eraseGenerics(type.getCanonicalText()) : null;
        if (type != null && supportedClasses.contains(erasedType)) {
            if (element.getArgumentList() != null && element.getArgumentList().getExpressions().length == 1) {
                if (element.getArgumentList().getExpressions()[0] instanceof PsiLiteralExpression) {
                    return getConstructorExpression(element,
                            (PsiLiteralExpression) element.getArgumentList().getExpressions()[0],
                            erasedType);
                } else if (element.getArgumentList().getExpressions()[0] instanceof PsiReferenceExpression) {
                    return getReferenceExpression(
                            (PsiReferenceExpression) element.getArgumentList().getExpressions()[0], true);
                } else if (erasedType.equals("java.util.ArrayList")
                        && element.getArgumentList().getExpressions()[0] instanceof PsiMethodCallExpression) {
                    Expression methodCallExpression = getMethodCallExpression(((PsiMethodCallExpression) element.getArgumentList().getExpressions()[0]), document);
                    if (methodCallExpression instanceof ListLiteral && settings.getState().isGetExpressionsCollapse()) {
                        return new ListLiteral(element, element.getTextRange(), ((ListLiteral) methodCallExpression).getItems());
                    }
                }
            } else if (element.getArgumentList() != null && element.getArgumentList().getExpressions().length == 0) {
                switch (erasedType) {
                    case "java.lang.String":
                    case "java.lang.StringBuilder":
                        return new StringLiteral(element, element.getTextRange(), "");
                    case "java.util.ArrayList":
                        if (settings.getState().isGetExpressionsCollapse()) {
                            return new ListLiteral(element, element.getTextRange(), Collections.emptyList());
                        } else {
                            return null;
                        }
                }
            }
        }
        @Nullable PsiArrayInitializerExpression arrayInitializer = element.getArrayInitializer();
        if (type != null && arrayInitializer != null && settings.getState().isGetExpressionsCollapse()) {
            return new ArrayLiteral(element, element.getTextRange(),
                    Arrays.stream(arrayInitializer.getInitializers())
                            .map(i -> getAnyExpression(i, document)).collect(
                            Collectors.toList()));
        }
        @Nullable PsiAnonymousClass anonymousClass = element.getAnonymousClass();
        if (type != null && anonymousClass != null && anonymousClass.getLBrace() != null && anonymousClass.getRBrace() != null) {
            if (erasedType.equals("java.util.HashSet")) {
                if (anonymousClass.getInitializers().length == 1) {
                    @NotNull PsiStatement[] statements = anonymousClass.getInitializers()[0].getBody().getStatements();
                    if (statements.length > 0) {
                        boolean flag = true;
                        ArrayList<PsiElement> arguments = new ArrayList<>();
                        for (PsiStatement statement : statements) {
                            if (statement instanceof PsiExpressionStatement
                                    && ((PsiExpressionStatement) statement).getExpression() instanceof PsiMethodCallExpression) {
                                @NotNull PsiMethodCallExpression methodCall = (PsiMethodCallExpression) ((PsiExpressionStatement) statement).getExpression();
                                if (methodCall.getMethodExpression().getText().equals("add") && methodCall.getArgumentList().getExpressions().length == 1) {
                                    PsiMethod method = (PsiMethod) methodCall.getMethodExpression().resolve();
                                    if (method != null && method.getContainingClass() != null
                                            && method.getContainingClass().getQualifiedName() != null
                                            && method.getContainingClass().getQualifiedName().equals("java.util.HashSet")) {
                                        arguments.add(methodCall.getArgumentList().getExpressions()[0]);
                                    } else {
                                        flag = false;
                                        break;
                                    }
                                } else {
                                    flag = false;
                                    break;
                                }
                            } else {
                                flag = false;
                                break;
                            }
                        }
                        if (flag && arguments.size() > 0) {
                            if (settings.getState().isGetExpressionsCollapse())
                                return new SetLiteral(element, element.getTextRange(),
                                        TextRange.create(anonymousClass.getLBrace().getTextRange().getStartOffset(),
                                                anonymousClass.getRBrace().getTextRange().getEndOffset()),
                                        anonymousClass.getInitializers()[0].getTextRange(),
                                        arguments.stream().map(a -> getAnyExpression(a, document)).collect(Collectors.toList()));
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private static Expression getReferenceExpression(PsiReferenceExpression element) {
        return getReferenceExpression(element, false);
    }

    @Nullable
    private static Expression getReferenceExpression(PsiReferenceExpression element, boolean copy) {
        Optional<PsiElement> found = Optional.empty();
        for (PsiElement c : element.getChildren()) {
            if (c instanceof PsiIdentifier) {
                found = Optional.of(c);
                break;
            }
        }
        Optional<PsiElement> identifier = found;
        if (identifier.isPresent()) {
            Object constant = supportedConstants.get(identifier.get().getText());
            if (constant != null) {
                if (isSupportedClass(element) && constant instanceof Number) {
                    return new NumberLiteral(element, element.getTextRange(), element.getTextRange(), (Number) constant, true);
                } else if (isSupportedClass(element) && constant instanceof String) {
                    return new Variable(element, element.getTextRange(), null, (String) constant, copy);
                }
            } else {
                Expression variable = getVariableExpression(element, copy);
                if (variable != null) return variable;
            }
        }
        return null;
    }

    @Nullable
    private static Variable getVariableExpression(PsiElement element) {
        return getVariableExpression(element, false);
    }

    @Nullable
    private static Variable getVariableExpression(PsiElement element, boolean copy) {
        PsiReference reference = element.getReference();
        if (reference != null) {
            PsiElement e = reference.resolve();
            if (e instanceof PsiVariable && ((PsiVariable) e).getName() != null
                    && Objects.equals(((PsiVariable) e).getName(), element.getText())) {
                PsiVariable variable = (PsiVariable) e;
                String name = variable.getName();
                if (name != null) {
                    // TODO: Please make sure this fix works
                    /*if (supportedClasses.contains(eraseGenerics(variable.getType().getCanonicalText()))) {
                        return new Variable(element, element.getTextRange(), null, name, copy);
                    } else if (supportedPrimitiveTypes
                            .contains(eraseGenerics(variable.getType().getCanonicalText()))) {*/
                    return new Variable(element, element.getTextRange(), null, name, copy);
                    /*}*/
                }
            }
        }
        return null;
    }

    @NotNull
    private static String eraseGenerics(@NotNull String signature) {
        String re = "<[^<>]*>";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(signature);
        while (m.find()) {
            signature = m.replaceAll("");
            m = p.matcher(signature);
        }
        return signature;
    }

    private static boolean isSupportedClass(@NotNull PsiElement element) {
        PsiReference reference = element.getReference();
        if (reference != null) {
            PsiElement e = reference.resolve();
            if (e instanceof PsiField) {
                PsiField field = (PsiField) e;
                PsiClass psiClass = field.getContainingClass();
                if (psiClass != null && psiClass.getQualifiedName() != null) {
                    return supportedClasses.contains(eraseGenerics(psiClass.getQualifiedName()));
                }
            }
        }
        return false;
    }

    @Nullable
    private static Expression getMethodCallExpression(PsiMethodCallExpression element, @NotNull Document document) {
        @NotNull AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        PsiReferenceExpression referenceExpression = element.getMethodExpression();
        Optional<PsiElement> identifier = Stream.of(referenceExpression.getChildren())
                .filter(c -> c instanceof PsiIdentifier).findAny();
        @Nullable PsiExpression qualifier = element.getMethodExpression().getQualifierExpression();
        if (identifier.isPresent() && supportedMethods.contains(identifier.get().getText())) {
            PsiMethod method = (PsiMethod) referenceExpression.resolve();
            if (method != null) {
                PsiClass psiClass = method.getContainingClass();
                if (psiClass != null && psiClass.getQualifiedName() != null) {
                    String className = eraseGenerics(psiClass.getQualifiedName());
                    if ((supportedClasses.contains(className) || unsupportedClassesMethodsExceptions.contains(method.getName()))
                            && qualifier != null) {
                        @NotNull Expression qualifierExpression = getAnyExpression(qualifier, document);
                        String methodName = identifier.get().getText();
                        if (methodName.equals("asList") || methodName.equals("singletonList")) {
                            if (!methodName.equals("asList") ||
                                    element.getArgumentList().getExpressions().length != 1 ||
                                    !(element.getArgumentList().getExpressions()[0].getType() instanceof PsiArrayType)) {
                                if (settings.getState().isGetExpressionsCollapse()) {
                                    return new ListLiteral(element, element.getTextRange(),
                                            Stream.of(element.getArgumentList().getExpressions())
                                                    .map(e -> getAnyExpression(e, document)).collect(
                                                    Collectors.toList()));
                                }
                            }
                        } else if (element.getArgumentList().getExpressions().length == 1) {
                            @NotNull PsiExpression argument = element.getArgumentList().getExpressions()[0];
                            @NotNull Expression argumentExpression = getAnyExpression(argument, document);
                            switch (methodName) {
                                case "add":
                                    switch (className) {
                                        case "java.util.List":
                                        case "java.util.ArrayList":
                                        case "java.util.Set":
                                        case "java.util.HashSet":
                                        case "java.util.Map":
                                        case "java.util.HashMap":
                                        case "java.util.Collection":
                                            if (element.getParent() instanceof PsiStatement && settings.getState().isConcatenationExpressionsCollapse()) {
                                                return new AddAssignForCollection(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                            } else {
                                                return null;
                                            }
                                        default:
                                            return new Add(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));

                                    }
                                case "remove":
                                    if (method.getParameterList().getParameters().length == 1
                                            && !method.getParameterList().getParameters()[0].getType().equals(PsiType.INT)) {
                                        if (element.getParent() instanceof PsiStatement && settings.getState().isConcatenationExpressionsCollapse()) {
                                            return new RemoveAssignForCollection(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                        }
                                    }
                                    break;
                                case "subtract":
                                    return new Subtract(element, element.getTextRange(),
                                            Arrays.asList(qualifierExpression, argumentExpression));
                                case "multiply":
                                    return new Multiply(element, element.getTextRange(),
                                            Arrays.asList(qualifierExpression, argumentExpression));
                                case "divide":
                                    return new Divide(element, element.getTextRange(),
                                            Arrays.asList(qualifierExpression, argumentExpression));
                                case "remainder":
                                    return new Remainder(element, element.getTextRange(),
                                            Arrays.asList(qualifierExpression, argumentExpression));
                                case "mod":
                                    return new Remainder(element, element.getTextRange(),
                                            Arrays.asList(qualifierExpression, argumentExpression));
                                case "andNot":
                                    return new And(element, element.getTextRange(),
                                            Arrays.asList(qualifierExpression, new Not(element, argumentExpression.getTextRange(),
                                                    Collections.singletonList(argumentExpression))));
                                case "pow":
                                    return new Pow(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                case "min":
                                    return new Min(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                case "max":
                                    return new Max(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                case "gcd":
                                    return new Gcd(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                case "and":
                                    return new And(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                case "or":
                                    return new Or(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                case "xor":
                                    return new Xor(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                case "shiftLeft":
                                    return new ShiftLeft(element, element.getTextRange(),
                                            Arrays.asList(qualifierExpression, argumentExpression));
                                case "shiftRight":
                                    return new ShiftRight(element, element.getTextRange(),
                                            Arrays.asList(qualifierExpression, argumentExpression));
                                case "equals":
                                    if (settings.getState().isComparingExpressionsCollapse()) {
                                        return new Equal(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                    } else {
                                        break;
                                    }
                                case "append":
                                    if (settings.getState().isConcatenationExpressionsCollapse()) {
                                        if (qualifierExpression instanceof Append) {
                                            List<Expression> operands = new ArrayList<>(((Append) qualifierExpression).getOperands());
                                            operands.add(argumentExpression);
                                            return new Append(element, element.getTextRange(),
                                                    operands);
                                        } else {
                                            if (qualifierExpression instanceof StringLiteral
                                                    && ((StringLiteral) qualifierExpression).getString().isEmpty()) {
                                                return new Append(element, element.getTextRange(),
                                                        Collections.singletonList(argumentExpression));
                                            } else {
                                                return new Append(element, element.getTextRange(),
                                                        Arrays.asList(qualifierExpression, argumentExpression));
                                            }
                                        }
                                    } else {
                                        break;
                                    }
                                case "charAt":
                                    if (settings.getState().isGetExpressionsCollapse()) {
                                        return new Get(element, element.getTextRange(), qualifierExpression,
                                                argumentExpression, Get.Style.NORMAL);
                                    } else {
                                        break;
                                    }
                                case "get":
                                    if (argumentExpression instanceof NumberLiteral && ((NumberLiteral) argumentExpression).getNumber().equals(0)) {
                                        /*return new Get(element, element.getTextRange(), qualifierExpression,
                                                argumentExpression, Get.Style.FIRST)*/
                                        ;
                                        return null;
                                    } else if (argument instanceof PsiBinaryExpression) {
                                        PsiBinaryExpression a2b = (PsiBinaryExpression) argument;
                                        NumberLiteral position = getSlicePosition(element, qualifierExpression, a2b, document);
                                        if (position != null && position.getNumber().equals(-1)) {
                                            if (settings.getState().isGetExpressionsCollapse()) {
                                                return new Get(element, element.getTextRange(), qualifierExpression,
                                                        argumentExpression, Get.Style.LAST);
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                    if (settings.getState().isGetExpressionsCollapse()) {
                                        return new Get(element, element.getTextRange(), qualifierExpression,
                                                argumentExpression, Get.Style.NORMAL);
                                    } else {
                                        break;
                                    }
                                case "subList":
                                case "substring":
                                    if (settings.getState().isSlicingExpressionsCollapse()) {
                                        if (argument instanceof PsiBinaryExpression) {
                                            NumberLiteral position = getSlicePosition(element,
                                                    qualifierExpression, (PsiBinaryExpression) argument, document);
                                            if (position != null) {
                                                return new Slice(element, element.getTextRange(),
                                                        Arrays.asList(qualifierExpression, position));
                                            }
                                        }
                                        return new Slice(element, element.getTextRange(),
                                                Arrays.asList(qualifierExpression, argumentExpression));
                                    } else {
                                        break;
                                    }
                                case "addAll":
                                    if (settings.getState().isConcatenationExpressionsCollapse()) {
                                        return new AddAssignForCollection(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                    } else {
                                        break;
                                    }
                                case "removeAll":
                                    if (settings.getState().isConcatenationExpressionsCollapse()) {
                                        return new RemoveAssignForCollection(element, element.getTextRange(), Arrays.asList(qualifierExpression, argumentExpression));
                                    } else {
                                        break;
                                    }
                                case "collect":
                                    if (argument instanceof PsiMethodCallExpression
                                            && startsWith(((PsiMethodCallExpression) argument).getMethodExpression().getReferenceName(), "to")) {
                                        @Nullable PsiExpression q = ((PsiMethodCallExpression) argument).getMethodExpression().getQualifierExpression();
                                        if (q instanceof PsiReferenceExpression && Objects.equals(((PsiReferenceExpression) q).getReferenceName(), "Collectors")) {
                                            Optional<PsiElement> i = Arrays.stream(((PsiMethodCallExpression) argument).getMethodExpression().getChildren()).filter(c -> c instanceof PsiIdentifier && c.getText().startsWith("to")).findAny();
                                            if (i.isPresent()) {
                                                if (settings.getState().isConcatenationExpressionsCollapse()) {
                                                    return new Collect(element, TextRange.create(identifier.get().getTextRange().getStartOffset(),
                                                            element.getTextRange().getEndOffset()), qualifierExpression,
                                                            TextRange.create(i.get().getTextRange().getStartOffset(),
                                                                    argument.getTextRange().getEndOffset()));
                                                } else {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                case "stream":
                                    if (element.getParent() instanceof PsiReferenceExpression &&
                                            ((PsiReferenceExpression) element.getParent()).getQualifierExpression() == element) {
                                        if (settings.getState().isConcatenationExpressionsCollapse()) {
                                            return new ArrayStream(element, TextRange.create(
                                                    element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset()), argumentExpression);
                                        } else {
                                            break;
                                        }
                                    }
                            }
                        } else if (element.getArgumentList().getExpressions().length == 0) {
                            switch (methodName) {
                                case "plus":
                                    return qualifierExpression;
                                case "negate":
                                    return new Negate(element, element.getTextRange(), Collections.singletonList(qualifierExpression));
                                case "not":
                                    return new Not(element, element.getTextRange(), Collections.singletonList(qualifierExpression));
                                case "abs":
                                    return new Abs(element, element.getTextRange(), Collections.singletonList(qualifierExpression));
                                case "signum":
                                    return new Signum(element, element.getTextRange(), Collections.singletonList(qualifierExpression));
                                case "stream":
                                    if (element.getParent() instanceof PsiReferenceExpression
                                            && ((PsiReferenceExpression) element.getParent()).getQualifierExpression() == element
                                            && settings.getState().isConcatenationExpressionsCollapse()) {
                                        return new StreamExpression(element, TextRange.create(identifier.get().getTextRange().getStartOffset(),
                                                element.getTextRange().getEndOffset()));
                                    } else {
                                        break;
                                    }
                                case "toString": // TODO: Generalize for literals and variables
                                    if (qualifierExpression instanceof Append) {
                                        Append append = (Append) qualifierExpression;
                                        return new Append(element, element.getTextRange(), append.getOperands());
                                    } else if (qualifierExpression instanceof StringLiteral) {
                                        StringLiteral stringLiteral = (StringLiteral) qualifierExpression;
                                        return new StringLiteral(element, element.getTextRange(), stringLiteral.getString());
                                    } else if (qualifierExpression instanceof NumberLiteral) {
                                        NumberLiteral numberLiteral = (NumberLiteral) qualifierExpression;
                                        return new NumberLiteral(element, element.getTextRange(), numberLiteral.getNumberTextRange(), numberLiteral.getNumber(), true);
                                    } else if (qualifierExpression instanceof Variable) {
                                        Variable variable = (Variable) qualifierExpression;
                                        return new Variable(element, element.getTextRange(), variable.getTextRange(), variable.getName(), true);
                                    } else {
                                        break;
                                    }
                            }
                        } else if (element.getArgumentList().getExpressions().length == 2) {
                            PsiExpression a1 = element.getArgumentList().getExpressions()[0];
                            PsiExpression a2 = element.getArgumentList().getExpressions()[1];
                            @NotNull Expression a1Expression = getAnyExpression(a1, document);
                            @NotNull Expression a2Expression = getAnyExpression(a2, document);
                            switch (methodName) {
                                case "put":
                                case "set":
                                    if (element.getParent() instanceof PsiStatement && settings.getState().isGetExpressionsCollapse()) {
                                        return new Put(element, element.getTextRange(), qualifierExpression, a1Expression, a2Expression);
                                    } else {
                                        break;
                                    }
                                case "atan2":
                                    return new Atan2(element, element.getTextRange(), Arrays.asList(qualifierExpression, a1Expression,
                                            a2Expression));
                                case "substring":
                                case "subList":
                                    if (settings.getState().isSlicingExpressionsCollapse()) {
                                        if (a1 instanceof PsiBinaryExpression) {
                                            NumberLiteral p1 = getSlicePosition(element, qualifierExpression, (PsiBinaryExpression) a1, document);
                                            if (p1 != null) {
                                                if (a2Expression instanceof NumberLiteral) {
                                                    return new Slice(element, element.getTextRange(), Arrays.asList(qualifierExpression,
                                                            p1, a2Expression));
                                                } else if (a2 instanceof PsiBinaryExpression) {
                                                    NumberLiteral p2 = getSlicePosition(element, qualifierExpression, (PsiBinaryExpression) a2, document);
                                                    if (p2 != null) {
                                                        return new Slice(element, element.getTextRange(),
                                                                Arrays.asList(qualifierExpression, p1, p2));
                                                    }
                                                } else //noinspection Duplicates
                                                    if (a2 instanceof PsiMethodCallExpression) {
                                                        @NotNull PsiMethodCallExpression a2m = (PsiMethodCallExpression) a2;
                                                        @NotNull PsiReferenceExpression a2me = a2m.getMethodExpression();
                                                        Optional<PsiElement> a2i = Stream.of(a2me.getChildren())
                                                                .filter(c -> c instanceof PsiIdentifier).findAny();
                                                        @Nullable PsiExpression q = a2me.getQualifierExpression();
                                                        if (a2i.isPresent() && q != null && (a2i.get().getText().equals("length") || a2i.get()
                                                                .getText().equals("size"))) {
                                                            @NotNull Expression a2qe = getAnyExpression(q, document);
                                                            if (a2qe.equals(qualifierExpression)) {
                                                                return new Slice(element, element.getTextRange(), Arrays.asList(qualifierExpression, p1));
                                                            }
                                                        }
                                                    }
                                            }
                                        }
                                        if (a2 instanceof PsiBinaryExpression) {
                                            @NotNull PsiBinaryExpression a2b = (PsiBinaryExpression) a2;
                                            @Nullable NumberLiteral position = getSlicePosition(element, qualifierExpression, a2b, document);
                                            if (position != null) {
                                                return new Slice(element, element.getTextRange(), Arrays.asList(qualifierExpression, a1Expression,
                                                        position));
                                            }
                                        } else //noinspection Duplicates
                                            if (a2 instanceof PsiMethodCallExpression) {
                                                @NotNull PsiMethodCallExpression a2m = (PsiMethodCallExpression) a2;
                                                @NotNull PsiReferenceExpression a2me = a2m.getMethodExpression();
                                                Optional<PsiElement> a2i = Stream.of(a2me.getChildren())
                                                        .filter(c -> c instanceof PsiIdentifier).findAny();
                                                @Nullable PsiExpression q = a2me.getQualifierExpression();
                                                if (a2i.isPresent() && q != null && (a2i.get().getText().equals("length") || a2i.get()
                                                        .getText().equals("size"))) {
                                                    @NotNull Expression a2qe = getAnyExpression(q, document);
                                                    if (a2qe.equals(qualifierExpression)) {
                                                        return new Slice(element, element.getTextRange(), Arrays.asList(qualifierExpression, a1Expression));
                                                    }
                                                }
                                            }
                                        return new Slice(element, element.getTextRange(), Arrays.asList(qualifierExpression, a1Expression, a2Expression));
                                    } else {
                                        break;
                                    }
                            }
                        }
                        if (element.getArgumentList().getExpressions().length == 1) {
                            PsiExpression argument = element.getArgumentList().getExpressions()[0];
                            if (method.getName().equals("valueOf") && argument instanceof PsiLiteralExpression) {
                                return getConstructorExpression(element, (PsiLiteralExpression) argument,
                                        className);
                            } else if (method.getName().equals("valueOf") && argument instanceof PsiReferenceExpression) {
                                Expression refExpr = getReferenceExpression((PsiReferenceExpression) argument);
                                if (refExpr instanceof Variable) {
                                    return new Variable(element, element.getTextRange(), refExpr.getTextRange(), ((Variable) refExpr).getName(), true);
                                } else {
                                    return null;
                                }
                            } else {
                                @NotNull Expression argumentExpression = getAnyExpression(argument, document);
                                switch (method.getName()) {
                                    case "abs":
                                        return new Abs(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "acos":
                                        return new Acos(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "asin":
                                        return new Asin(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "atan":
                                        return new Atan(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "cbrt":
                                        return new Cbrt(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "ceil":
                                        return new Ceil(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "cos":
                                        return new Cos(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "cosh":
                                        return new Cosh(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "floor":
                                        return new Floor(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "log":
                                        return new Log(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "log10":
                                        return new Log10(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "rint":
                                        return new Rint(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "round":
                                        return new Round(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "sin":
                                        return new Sin(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "sinh":
                                        return new Sinh(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "Sqrt":
                                        return new Sqrt(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "tan":
                                        return new Tan(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "tanh":
                                        return new Tanh(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "toDegrees":
                                        return new ToDegrees(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "toRadians":
                                        return new ToRadians(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "ulp":
                                        return new Ulp(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "exp":
                                        return new Exp(element, element.getTextRange(), Collections.singletonList(argumentExpression));
                                    case "unmodifiableSet":
                                        if (argumentExpression instanceof SetLiteral && settings.getState().isGetExpressionsCollapse()) {
                                            SetLiteral setLiteral = (SetLiteral) argumentExpression;
                                            return new SetLiteral(element, element.getTextRange(),
                                                    setLiteral.getFirstBracesRange(), setLiteral.getSecondBracesRange(),
                                                    setLiteral.getOperands());
                                        } else {
                                            break;
                                        }
                                    case "unmodifiableList":
                                        if (argumentExpression instanceof ListLiteral && settings.getState().isGetExpressionsCollapse()) {
                                            ListLiteral setLiteral = (ListLiteral) argumentExpression;
                                            return new ListLiteral(element, element.getTextRange(),
                                                    setLiteral.getItems());
                                        } else {
                                            break;
                                        }
                                }
                            }
                        } else if (element.getArgumentList().getExpressions().length == 2) {
                            PsiExpression a1 = element.getArgumentList().getExpressions()[0];
                            @NotNull Expression a1Expression = getAnyExpression(a1, document);
                            PsiExpression a2 = element.getArgumentList().getExpressions()[1];
                            @NotNull Expression a2Expression = getAnyExpression(a2, document);
                            switch (methodName) {
                                case "min":
                                    return new Min(element, element.getTextRange(), Arrays.asList(a1Expression, a2Expression));
                                case "max":
                                    return new Max(element, element.getTextRange(), Arrays.asList(a1Expression, a2Expression));
                                case "pow":
                                    return new Pow(element, element.getTextRange(), Arrays.asList(a1Expression, a2Expression));
                                case "addAll":
                                    if (settings.getState().isConcatenationExpressionsCollapse()) {
                                        return new AddAssignForCollection(element, element.getTextRange(), Arrays.asList(a1Expression, a2Expression));
                                    } else {
                                        break;
                                    }
                                case "equals":
                                    if (settings.getState().isComparingExpressionsCollapse()) {
                                        return new Equal(element, element.getTextRange(), Arrays.asList(a1Expression, a2Expression));
                                    } else {
                                        break;
                                    }
                            }
                        } else if (element.getArgumentList().getExpressions().length == 0) {
                            switch (method.getName()) {
                                case "random":
                                    return new Random(element, element.getTextRange(), Collections.emptyList());
                            }
                        }
                    }
                }
            }

        }
        if (settings.getState().isGetSetExpressionsCollapse()) {
            if (identifier.isPresent() && ((startsWith(identifier.get().getText(), "get") && identifier.get().getText().length() > 3)
                    || (identifier.get().getText().startsWith("is") && identifier.get().getText().length() > 2))
                    && element.getArgumentList().getExpressions().length == 0) {
                return new Getter(element, element.getTextRange(), TextRange.create(identifier.get().getTextRange().getStartOffset(),
                        element.getTextRange().getEndOffset()),
                        qualifier != null
                                ? getAnyExpression(qualifier, document)
                                : null,
                        guessPropertyName(identifier.get().getText()));
            } else if (identifier.isPresent()
                    && identifier.get().getText().startsWith("set")
                    && identifier.get().getText().length() > 3
                    && Character.isUpperCase(identifier.get().getText().charAt(3))
                    && element.getArgumentList().getExpressions().length == 1
                    && element.getParent() instanceof PsiStatement
                    && (qualifier == null
                    || !(qualifier instanceof PsiMethodCallExpression)
                    || !(startsWith(((PsiMethodCallExpression) qualifier).getMethodExpression().getReferenceName(), "set")))) {
                return new Setter(element, element.getTextRange(), TextRange.create(identifier.get().getTextRange().getStartOffset(),
                        element.getTextRange().getEndOffset()),
                        qualifier != null ? getAnyExpression(qualifier, document) : null,
                        guessPropertyName(identifier.get().getText()),
                        getAnyExpression(element.getArgumentList().getExpressions()[0], document));
            }
        }
        return null;
    }

    static int findDot(@NotNull Document document, int position, int i) {
        int offset = 0;
        while (Math.abs(offset) < 100 && position > 0 && position < document.getText().length() && !document.getText(TextRange.create(position, position + 1)).equals(".")) {
            position += i;
            offset += i;
        }
        return offset;
    }

    @NotNull
    private static String guessPropertyName(@NotNull String text) {
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

    @Nullable
    private static NumberLiteral getSlicePosition(@NotNull PsiElement parent, @NotNull Expression qualifierExpression,
                                                  @NotNull PsiBinaryExpression a2b, @NotNull Document document) {
        PsiExpression rOperand = a2b.getROperand();
        PsiExpression lOperand = a2b.getLOperand();
        if (a2b.getOperationSign().getText().equals("-")
                && rOperand != null
                && (lOperand instanceof PsiMethodCallExpression
                || lOperand instanceof PsiReferenceExpression)) {
            @NotNull Expression s = getAnyExpression(rOperand, document);
            if (s instanceof NumberLiteral) {
                @NotNull PsiReferenceExpression a2me = lOperand instanceof PsiMethodCallExpression
                        ? ((PsiMethodCallExpression) lOperand).getMethodExpression() : (PsiReferenceExpression) lOperand;
                @NotNull Optional<PsiElement> a2i = Stream.of(a2me.getChildren())
                        .filter(c -> c instanceof PsiIdentifier).findAny();
                if (a2i.isPresent() && (a2i.get().getText().equals("length")
                        || a2i.get().getText().equals("size")) && a2me.getQualifierExpression() != null) {
                    @NotNull Expression a2qe = getAnyExpression(a2me.getQualifierExpression(), document);
                    if (a2qe.equals(qualifierExpression)) {
                        return new NumberLiteral(parent,
                                TextRange.create(a2b.getOperationSign().getTextRange().getStartOffset(),
                                        a2b.getTextRange().getEndOffset()), null, -((NumberLiteral) s).getNumber().intValue(), false);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private static Expression getConstructorExpression(@NotNull PsiElement parent, @NotNull PsiLiteralExpression argument, @NotNull String classQualifiedNameNoGenerics) {
        Expression literalExpression = getLiteralExpression(argument);
        if (literalExpression instanceof NumberLiteral) {
            return new NumberLiteral(parent, parent.getTextRange(), literalExpression.getTextRange(), ((NumberLiteral) literalExpression).getNumber(), false);
        } else {
            try {
                String value = argument.getText();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                switch (classQualifiedNameNoGenerics) {
                    case "java.lang.Long":
                        return new NumberLiteral(parent, parent.getTextRange(), argument.getTextRange(), Long.valueOf(value),
                                !(argument.getValue() instanceof Number));
                    case "java.lang.Integer":
                        return new NumberLiteral(parent, parent.getTextRange(), argument.getTextRange(), Integer.valueOf(value),
                                !(argument.getValue() instanceof Number));
                    case "java.lang.Float":
                        return new NumberLiteral(parent, parent.getTextRange(), argument.getTextRange(), Float.valueOf(value),
                                !(argument.getValue() instanceof Number));
                    case "java.lang.Double":
                        return new NumberLiteral(parent, parent.getTextRange(), argument.getTextRange(), Double.valueOf(value),
                                !(argument.getValue() instanceof Number));
                    case "java.math.BigDecimal":
                        return new NumberLiteral(parent, parent.getTextRange(), argument.getTextRange(), new BigDecimal(value),
                                !(argument.getValue() instanceof Number));
                    case "java.math.BigInteger":
                        return new NumberLiteral(parent, parent.getTextRange(), argument.getTextRange(), new BigInteger(value),
                                !(argument.getValue() instanceof Number));
                    case "java.lang.StringBuilder":
                        return new StringLiteral(parent, parent.getTextRange(), value);
                    case "java.lang.String":
                        return new StringLiteral(parent, parent.getTextRange(), value);
                }
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> allDescriptors = null;
        try {
            @Nullable Expression expression = getNonSyntheticExpression(element, document);
            if (expression != null && expression.supportsFoldRegions(document, null)) {
                allDescriptors = new ArrayList<>();
                FoldingDescriptor[] descriptors = expression.buildFoldRegions(expression.getElement(), document, null);
                Collections.addAll(allDescriptors, descriptors);
            }
            if (expression == null || expression.isNested()) {
                for (PsiElement child : element.getChildren()) {
                    FoldingDescriptor[] descriptors = buildFoldRegions(child, document, quick);
                    if (descriptors.length > 0) {
                        if (allDescriptors == null) {
                            allDescriptors = new ArrayList<>();
                        }
                        allDescriptors.addAll(Arrays.asList(descriptors));
                    }
                }
            }
        } catch (IndexNotReadyException e) {
            // ignore
        }
        return allDescriptors != null ? allDescriptors.toArray(NO_DESCRIPTORS) : NO_DESCRIPTORS;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {
        return null;
    }

    // TODO: Collapse everything by default but use these settings when actually building the folding descriptors
    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        try {
            PsiElement element = astNode.getPsi();
            @Nullable Document document = PsiDocumentManager.getInstance(astNode.getPsi().getProject()).getDocument(astNode.getPsi().getContainingFile());
            if (document != null) {
                @Nullable Expression expression = getNonSyntheticExpression(element, document);
                return expression != null && expression.isCollapsedByDefault();
            }
        } catch (IndexNotReadyException e) {
            return false;
        }
        return false;
    }
}
