# junit-fail-logger

junit-fail-logger is the annotation processor that modifies JUnit tests code in order to log failed assertion expressions. 
Processes methods annotated with **org.junit.Test** annotation

**Usage**
1. build 
2. add as a dependency to your project

**Configuration**
There are two configuration properties:
_jufl.method_ - fully qualified static method name that will be invoked for logging
    e.g. `jufl.method=com.github.swfsm.demo.DemoLogger.logFailure`
_jufl.message_ - log message format
    e.g. `jufl.message=class: {0}. test: {1}. assertion: {2}` ({0} - test case class name, {1} - test method name, {2} - assertion expression)

Properties can be provided via system properties or by adding junit-fail-logger.properties to the classpath.
junit-fail-logger-fallback.properties is used as the fallback properties source

**Demo**
run **mvn clean test -Ddemo** to see how it works. Failures will be logged to the **failures.log** file.

## License

See the [LICENSE](LICENSE.md) file for license rights and limitations (MIT).