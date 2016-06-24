package com.github.swfsm.processor;

import com.github.swfsm.logger.LoggerConfig;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementScanner7;
import java.text.MessageFormat;
import java.util.Arrays;

public class JUnitAssertionWrapper extends ElementScanner7<Void, Void> {

    public static final String EXCEPTION_VAR_NAME = "e";

    private final Trees jcTrees;
    private final Names jcNames;
    private final TreeMaker jcTreeMaker;

    private final LoggerConfig loggerConfig;

    private final String className;
    private final String methodName;

    public JUnitAssertionWrapper(ProcessingEnvironment env, LoggerConfig loggerConfig, String className, String methodName) {
        JavacProcessingEnvironment javacEnv = (JavacProcessingEnvironment) env;
        this.jcTrees = Trees.instance(env);
        this.jcNames = Names.instance(javacEnv.getContext());
        this.jcTreeMaker = TreeMaker.instance(javacEnv.getContext());

        this.loggerConfig = loggerConfig;

        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public Void visitExecutable(ExecutableElement method, Void aVoid) {

        ((JCTree) jcTrees.getTree(method)).accept(new TreeTranslator() {

            @Override
            public void visitExec(JCTree.JCExpressionStatement jcExpressionStatement) {
                super.visitExec(jcExpressionStatement);

                if (isJUnitAssert(jcExpressionStatement)) {

                    JCTree.JCCatch catchBlock = jcTreeMaker.Catch(
                            jcTreeMaker.VarDef(
                                    jcTreeMaker.Modifiers(0),
                                    jcNames.fromString(EXCEPTION_VAR_NAME),
                                    jcTreeMaker.Ident(jcNames.fromString(AssertionError.class.getSimpleName())),
                                    null
                            ),
                            jcTreeMaker.Block(
                                    0,
                                    List.of(invokeLogger(generateLogEntry(jcExpressionStatement))
                                            ,jcTreeMaker.Throw(jcTreeMaker.Ident(jcNames.fromString(EXCEPTION_VAR_NAME))))
                            )
                    );

                    result = jcTreeMaker.Try(
                            jcTreeMaker.Block(0, List.of((JCTree.JCStatement)jcExpressionStatement)),
                            List.of(catchBlock),
                            null);
                }
            }
        });

        return super.visitExecutable(method, aVoid);
    }

    private String generateLogEntry(JCTree.JCExpressionStatement jcExpressionStatement) {
        String message = MessageFormat.format(loggerConfig.getMessage(), className, methodName, jcExpressionStatement.toString());
        return message;
    }

    private boolean isJUnitAssert(JCTree.JCExpressionStatement jcExpressionStatement) {
        return jcExpressionStatement.toString().contains("assert");
    }

    private JCTree.JCExpression invocationChain(java.util.List<String> elems) {
        JCTree.JCExpression e = null;
        for (String elem : elems) {
            if (e == null) e = jcTreeMaker.Ident(jcNames.fromString(elem));
            else e = jcTreeMaker.Select(e, jcNames.fromString(elem));
        }
        return e;
    }

    private JCTree.JCStatement invokeLogger(String message) {
        JCTree.JCExpression fn = invocationChain(Arrays.asList(loggerConfig.getMethod().split("\\.")));

        List<JCTree.JCExpression> args = List.<JCTree.JCExpression> of(jcTreeMaker.Literal(message));
        JCTree.JCMethodInvocation m = jcTreeMaker.Apply(List.<JCTree.JCExpression> nil(), fn, args);
        return jcTreeMaker.Exec(m);
    }

}
