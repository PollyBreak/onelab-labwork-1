package com.polina.lab1.aspect;

import com.polina.lab1.repository.TestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = LoggingAspectTest.TestConfig.class)
class LoggingAspectTest {
    @Autowired
    private TestRepository testRepository;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        reset(testRepository);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void givenMethodCall_whenInvoked_thenLoggingAspectBeforeAndAfterAdviceExecuted() {
        testRepository.someMethod();
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("[AOP] This advice executes before method"),
                "Before advice should be logged");
        assertTrue(output.contains("[AOP] This advice executes after method"),
                "After advice should be logged");
    }

    @Test
    void givenMethodReturnsValue_whenExecuted_thenLoggingAspectAfterReturningExecuted() {
        when(testRepository.someReturningMethod()).thenReturn("Test Result");
        String result = testRepository.someReturningMethod();
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("[AOP] This advice executes successfully after method returning"),
                "AfterReturning advice should be logged");
        assertTrue(output.contains("Returned: Test Result"),
                "Returned value should be logged");
    }

    @Test
    void givenMethodThrowsException_whenExecuted_thenLoggingAspectAfterThrowingExecuted() {
        doThrow(new RuntimeException("Test exception")).when(testRepository).someExceptionMethod();
        assertThrows(RuntimeException.class, testRepository::someExceptionMethod);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("[AOP] Exception occurred: Test exception"),
                "Exception message should be logged");
    }

    @Test
    void givenMethodExecution_whenInvoked_thenLoggingAspectAroundAdviceExecuted() {
        when(testRepository.someReturningMethod()).thenReturn("Test Result");
        String result = testRepository.someReturningMethod();
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("[AOP] Method called:"), "Around advice should log method call");
        assertTrue(output.contains("[AOP] Execution completed:"),
                "Around advice should log execution completion");
    }

    @Configuration
    static class TestConfig {
        @Bean
        public LoggingAspect loggingAspect() {
            return new LoggingAspect();
        }
        @Bean
        public TestRepository testRepository() {
            TestRepository mockRepo = mock(TestRepository.class);
            AspectJProxyFactory factory = new AspectJProxyFactory(mockRepo);
            factory.addAspect(new LoggingAspect());
            return factory.getProxy();
        }
    }
}
