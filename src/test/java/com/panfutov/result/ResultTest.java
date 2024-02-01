package com.panfutov.result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResultTest {
    private static final String ERROR = "something went wrong";
    private static final Object OBJECT = new Object();

    private Object object;

    @Mock
    private Consumer<Result<Void>> consumer;

    @Mock
    private Consumer<Result<Object>> objectConsumer;

    @Mock
    private Supplier<Result<Void>> supplier;

    @BeforeEach
    public void init() {
        object = new Object();
    }

    @Test
    void testSuccess() {
        // WHEN
        var result = Result.success(object);

        // THEN
        assertEquals(object, result.object());
        assertTrue(result.isSuccess());
        assertFalse(result.hasErrors());
    }

    @Test
    void testSuccessWithErrors() {
        // WHEN
        var result = Result.success(object, List.of(ERROR));

        // THEN
        assertEquals(object, result.object());
        assertTrue(result.isSuccess());
        assertTrue(result.hasErrors());
    }

    @Test
    void testSuccessWithError() {
        // WHEN
        var result = Result.success(object, ERROR);

        // THEN
        assertEquals(object, result.object());
        assertTrue(result.isSuccess());
        assertTrue(result.hasErrors());
    }

    @Test
    void testSuccessVoid() {
        // WHEN
        var result = Result.successVoid();

        // THEN
        assertNull(result.object());
        assertTrue(result.isSuccess());
    }

    @Test
    void testSuccessVoidWithError() {
        // WHEN
        var result = Result.successVoid(ERROR);

        // THEN
        assertNull(result.object());
        assertTrue(result.isSuccess());
        assertTrue(result.hasErrors());
    }

    @Test
    void testSuccessVoidWithErrors() {
        // WHEN
        var result = Result.successVoid(List.of(ERROR));

        // THEN
        assertNull(result.object());
        assertTrue(result.isSuccess());
        assertTrue(result.hasErrors());
    }

    @Test
    void testFailureWithError() {
        // WHEN
        var result = Result.failure(ERROR);

        // THEN
        assertNull(result.object());
        assertTrue(result.isFailure());
        assertTrue(result.hasErrors());
    }

    @Test
    void testFailureWithErrors() {
        // WHEN
        var result = Result.failure(ERROR, ERROR);

        // THEN
        assertNull(result.object());
        assertTrue(result.isFailure());
        assertTrue(result.hasErrors());
        assertEquals(2, result.errorCount());
    }

    @Test
    void testFailureWithErrorList() {
        // WHEN
        var result = Result.failure(List.of(ERROR, ERROR));

        // THEN
        assertNull(result.object());
        assertTrue(result.isFailure());
        assertTrue(result.hasErrors());
        assertEquals(2, result.errorCount());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false } )
    void testIsSuccess(boolean input) {
        // WHEN
        var result = new Result<>(input, null, emptyList());

        // THEN
        assertEquals(input, result.isSuccess());
        assertNotEquals(input, result.isFailure());
    }

    @Test
    void testHasErrors() {
        // WHEN
        var result = Result.failure(ERROR);

        // THEN
        assertTrue(result.hasErrors());
    }

    @Test
    void testHasErrorsOnSuccess() {
        // WHEN
        var result = Result.successVoid();

        // THEN
        assertFalse(result.hasErrors());
    }

    @Test
    void testErrorCount() {
        // WHEN
        var result = Result.failure(ERROR, ERROR);

        // THEN
        assertEquals(2, result.errorCount());
    }

    @Test
    void testFirstError() {
        // WHEN
        var result = Result.failure(ERROR, "another");

        // THEN
        assertEquals(ERROR, result.firstError());
    }

    @Test
    void testIfSuccessForConsumer() {
        // GIVEN
        var result = Result.successVoid();

        // WHEN
        result.ifSuccess(consumer);

        // THEN
        verify(consumer).accept(any());
    }

    @Test
    void testIfSuccessForSupplier() {
        // GIVEN
        var result = Result.successVoid();

        // WHEN
        result.ifSuccess(supplier);

        // THEN
        verify(supplier).get();
    }

    @Test
    void testIfFailure() {
        // GIVEN
        var result = Result.failure(ERROR);

        // WHEN
        result.ifFailure(objectConsumer);

        // THEN
        verify(objectConsumer).accept(any());
    }

    @Test
    void testObject() {
        // GIVEN
        var result = Result.success(OBJECT);

        // WHEN
        var object = result.object();

        // THEN
        assertEquals(OBJECT, object);
    }

    @Test
    void testObjectOrElse() {
        // GIVEN
        var result = Result.success(null);
        var other = new Object();

        // WHEN
        var retrieved = result.objectOrElse(other);

        // THEN
        assertEquals(other, retrieved);
    }

    @Test
    void testObjectOrElseGet() {
        // GIVEN
        var result = Result.success(null);
        var other = new Object();

        // WHEN
        var retrieved = result.objectOrElseGet(() -> other);

        // THEN
        assertEquals(other, retrieved);
    }

    @Test
    void testGetObjectNonNull() {
        // GIVEN
        var result = Result.failure(ERROR);

        // WHEN
        Executable command = result::objectNonNull;

        // THEN
        assertThrows(NullPointerException.class, command);
    }

    @Test
    void testErrors() {
        // GIVEN
        var result = Result.failure(ERROR);

        // WHEN
        var errors = result.errors();

        // THEN
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(ERROR, errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("equalsInput")
    void testEquals(Result<?> one, Result<?> another, boolean expected) {
        assertEquals(expected, one.equals(another));
    }

    private static Stream<Arguments> equalsInput() {
        return Stream.of(
                Arguments.of(Result.success(new Object()), Result.success(new Object()), false),
                Arguments.of(Result.success(null), Result.success(new Object()), false),
                Arguments.of(Result.failure(ERROR, ERROR), Result.failure(ERROR), false),
                Arguments.of(Result.success(null), Result.success(null), true),
                Arguments.of(Result.successVoid(ERROR), Result.successVoid(List.of(ERROR)), true),
                Arguments.of(Result.successVoid(), Result.success(null), true),
                Arguments.of(Result.success(OBJECT), Result.success(OBJECT), true),
                Arguments.of(Result.successVoid(), Result.successVoid(), true)
        );
    }

    @ParameterizedTest
    @MethodSource("hashCodeInput")
    void testHashCode(Result<?> one, Result<?> another, boolean expected) {
        assertEquals(expected, one.hashCode() == another.hashCode());
    }

    private static Stream<Arguments> hashCodeInput() {
        return Stream.of(
                Arguments.of(Result.successVoid(), Result.successVoid(), true),
                Arguments.of(Result.successVoid(), Result.success(OBJECT), false)
        );
    }

    @Test
    void testToString() {
        // GIVEN
        String content = "content";
        var result = Result.success(content, ERROR);

        // WHEN
        var string = result.toString();

        // THEN
        assertEquals("Result{success=true, object=content, errors=[something went wrong]}", string);
    }
}