package com.polina.appservice;


import com.polina.appservice.aspect.LoggingAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testBeforeAdvice() {
        loggingAspect.beforeAdvice();
        assertTrue(outContent.toString().contains("[AOP] This advice executes before method"));
    }

    @Test
    void testAfterAdvice() {
        loggingAspect.afterAdvice();
        assertTrue(outContent.toString().contains("[AOP] This advice executes after method"));
    }

    @Test
    void testAfterReturningAdvice() {
        Object result = "Test Result";
        loggingAspect.afterReturningAdvice(result);
        assertTrue(outContent.toString().contains("[AOP] Successfully executed method. Returned: " + result));
    }

    @Test
    void testAfterThrowingAdvice() {
        Exception exception = new RuntimeException("Test Exception");
        loggingAspect.afterThrowingAdvice(exception);
        assertTrue(outContent.toString().contains("[AOP] Exception occurred: " + exception.getMessage()));
    }

    @Test
    void testAroundAdvice() throws Throwable {
        Signature signature = mock(Signature.class);
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn("testMethod()");
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"arg1", 2});
        when(proceedingJoinPoint.proceed()).thenReturn("Success");

        Object result = loggingAspect.aroundAdvice(proceedingJoinPoint);

        assertTrue(outContent.toString().contains("[AOP] Method called: testMethod()"));
        assertTrue(outContent.toString().contains("[AOP] Arguments: " + Arrays.toString(new Object[]{"arg1", 2})));
        assertTrue(outContent.toString().contains("[AOP] Execution completed: testMethod() in"));
        assertTrue(result.equals("Success"));
    }
}
