package com.github.swfsm.processor;

import com.github.swfsm.logger.LoggerConfig;
import com.github.swfsm.logger.LoggerConfigResolver;
import org.junit.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes("org.junit.Test")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class JUnitTestAnnotationProcessor extends AbstractProcessor {

    private ProcessingEnvironment processingEnvironment;

    private Set<String> processedClasses = new HashSet<>();

    private LoggerConfig loggerConfig;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        processingEnvironment = processingEnv;
        loggerConfig = new LoggerConfigResolver().resolve();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        for (Element el: roundEnv.getElementsAnnotatedWith(Test.class)) {

            String className = el.getEnclosingElement().getSimpleName().toString();

            JUnitAssertionWrapper wrapper
                    = new JUnitAssertionWrapper(processingEnvironment, loggerConfig, className, el.getSimpleName().toString());

            el.accept(wrapper, null);
        };
        return true;
    }

}
