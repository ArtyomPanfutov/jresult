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
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ResultTest {

    private static final String ERROR_TEXT = "something went wrong";
    private static final GenericError ERROR = new Error(ERROR_TEXT);
    private static final Object OBJECT = new Object();

    private Object object;

    @Mock
    private Consumer<Result<Void>> consumer;

    @Mock
    private Consumer<Result<Object>> objectConsumer;

    @Mock
    private Supplier<Result<Void>> supplier;

    @Mock
    private Supplier<Result<Object>> objectSupplier;

    @Mock
    private Function<Result<Object>, Integer> successMapper;

    @Mock
    private Function<Result<Object>, Integer> failureMapper;

    @BeforeEach
    public void init() {
        object = new Object();
    }

    @Test
    void testSuccess() {
        // WHEN
        var result = Result.success(object);

        // THEN
        assertEquals(object, result.getObject());
        assertTrue(result.isSuccess());
        assertFalse(result.hasErrors());
    }

    @Test
    void testSuccessWithErrors() {
        // WHEN
        var result = Result.success(object, List.of(ERROR));

        // THEN
        assertEquals(object, result.getObject());
        assertTrue(result.isSuccess());
        assertTrue(result.hasErrors());
    }

    @Test
    void testSuccessWithTextError() {
        // WHEN
        var result = Result.success(object, ERROR_TEXT);

        // THEN
        assertEquals(object, result.getObject());
        assertTrue(result.isSuccess());
        assertTrue(result.hasErrors());
    }

    @Test
    void testSuccessWithNullTextError() {
        // GIVEN
        String message = null;

        // WHEN
        Executable command = () -> Result.success(object, message);

        // THEN
        assertThrows(NullPointerException.class, command);
    }

    @Test
    void testSuccessVoid() {
        // WHEN
        var result = Result.successVoid();

        // THEN
        assertNull(result.getObject());
        assertTrue(result.isSuccess());
    }

    @Test
    void testSuccessVoidWithError() {
        // WHEN
        var result = Result.successVoid(ERROR_TEXT);

        // THEN
        assertNull(result.getObject());
        assertTrue(result.isSuccess());
        assertTrue(result.hasErrors());
    }

    @Test
    void testSuccessVoidWithErrorOnNullMessage() {
        // GIVEN`
        String message = null;

        // WHEN
        Executable command = () -> Result.successVoid(message);

        // THEN
        assertThrows(NullPointerException.class, command);
    }

    @Test
    void testSuccessVoidWithErrors() {
        // WHEN
        var result = Result.successVoid(List.of(ERROR));

        // THEN
        assertNull(result.getObject());
        assertTrue(result.isSuccess());
        assertTrue(result.hasErrors());
    }

    @Test
    void testFailureWithError() {
        // WHEN
        var result = Result.failure(ERROR_TEXT);

        // THEN
        assertNull(result.getObject());
        assertTrue(result.isFailure());
        assertTrue(result.hasErrors());
    }

    @Test
    void testFailureWithErrorList() {
        // WHEN
        var result = Result.failure(List.of(ERROR, ERROR));

        // THEN
        assertNull(result.getObject());
        assertTrue(result.isFailure());
        assertTrue(result.hasErrors());
        assertEquals(2, result.errorCount());
    }

    @Test
    void testFailureWithErrorArgs() {
        // WHEN
        var result = Result.failure(ERROR, ERROR, ERROR);

        // THEN
        assertNull(result.getObject());
        assertTrue(result.isFailure());
        assertTrue(result.hasErrors());
    }

    @Test
    void testFailureWithErrors() {
        // WHEN
        var result = Result.failure(ERROR_TEXT, ERROR_TEXT);

        // THEN
        assertNull(result.getObject());
        assertTrue(result.isFailure());
        assertTrue(result.hasErrors());
        assertEquals(2, result.errorCount());
    }

    @Test
    void testFailureWithErrorText() {
        // WHEN
        var result = Result.failure(ERROR_TEXT, ERROR_TEXT);

        // THEN
        assertNull(result.getObject());
        assertTrue(result.isFailure());
        assertTrue(result.hasErrors());
        assertEquals(2, result.errorCount());
    }

    @Test
    void testFailureWithThrowable() {
        // WHEN
        var result = Result.failure(new RuntimeException());

        // THEN
        assertTrue(result.isFailure());
        assertTrue(result.firstError().getThrowable().isPresent());
    }

    @Test
    void testFailureWithMessageAndThrowable() {
        // WHEN
        var result = Result.failure(ERROR_TEXT, new RuntimeException());

        // THEN
        assertTrue(result.isFailure());
        assertTrue(result.firstError().getThrowable().isPresent());
        assertFalse(result.firstError().getMessage().isEmpty());
    }

    @Test
    void testFailureWithThrowableAndMetadata() {
        // WHEN
        var result = Result.failure(ERROR_TEXT, new RuntimeException(), Map.of("Severity", "error"));

        // THEN
        assertTrue(result.isFailure());
        assertTrue(result.firstError().getThrowable().isPresent());
        assertTrue(result.firstError().getMetadata().isPresent());
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
        var result = Result.failure(ERROR_TEXT);

        // THEN
        assertTrue(result.hasErrors());
    }

    @Test
    void testFailureOnNullMessage() {
        // GIVEN
        String message = null;

        // WHEN
        Executable command = () -> Result.failure(message);

        // THEN
        assertThrows(NullPointerException.class, command);
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
        var result = Result.failure(ERROR_TEXT, ERROR_TEXT);

        // THEN
        assertEquals(2, result.errorCount());
    }

    @Test
    void testFirstError() {
        // WHEN
        var result = Result.failure(ERROR_TEXT, "another");

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
    void testIfSuccessForConsumerOnFailedResult() {
        // GIVEN
        var result = Result.failure(ERROR);

        // WHEN
        result.ifSuccess(objectConsumer);

        // THEN
        verifyNoInteractions(objectConsumer);
    }

    @Test
    void testIfSuccessOnNullConsumer() {
        // GIVEN
        var result = Result.failure(ERROR);
        Consumer<Result<Object>> nullConsumer = null;

        // WHEN
        Executable command = () -> result.ifSuccess(nullConsumer);

        // THEN
        assertThrows(NullPointerException.class, command);
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
    void testIfSuccessForSupplierOnFailedResult() {
        // GIVEN
        Result<Object> result = Result.failure(ERROR_TEXT);

        // WHEN
        var anotherResult = result.ifSuccess(objectSupplier);

        // THEN
        verifyNoInteractions(objectSupplier);
        assertEquals(result, anotherResult);
    }

    @Test
    void testIfSuccessOnNullSupplier() {
        // GIVEN
        var result = Result.success(null);
        Supplier<Result<Object>> nullSupplier = null;

        // WHEN
        Executable command = () -> result.ifSuccess(nullSupplier);

        // THEN
        assertThrows(NullPointerException.class, command);
    }

    @Test
    void testIfFailure() {
        // GIVEN
        var result = Result.failure(ERROR_TEXT);

        // WHEN
        result.ifFailure(objectConsumer);

        // THEN
        verify(objectConsumer).accept(any());
    }

    @Test
    void testIfFailureOnSucceededResult() {
        // GIVEN
        var result = Result.successVoid(ERROR_TEXT);

        // WHEN
        var another = result.ifFailure(consumer);

        // THEN
        verifyNoInteractions(consumer);
        assertEquals(result, another);
    }

    @Test
    void testIfFailureOnNullConsumer() {
        // GIVEN
        var result = Result.success(null);

        // WHEN
        Executable command = () -> result.ifFailure(null);

        // THEN
        assertThrows(NullPointerException.class, command);
    }

    @Test
    void testObject() {
        // GIVEN
        var result = Result.success(OBJECT);

        // WHEN
        var object = result.getObject();

        // THEN
        assertEquals(OBJECT, object);
    }

    @Test
    void testObjectOrElse() {
        // GIVEN
        var result = Result.success(null);
        var other = new Object();

        // WHEN
        var retrieved = result.getObjectOrElse(other);

        // THEN
        assertEquals(other, retrieved);
    }

    @Test
    void testObjectOrElseGet() {
        // GIVEN
        var result = Result.success(null);
        var other = new Object();

        // WHEN
        var retrieved = result.getObjectOrElse(() -> other);

        // THEN
        assertEquals(other, retrieved);
    }

    @Test
    void testObjectOrElseGetOnNull() {
        // GIVEN
        var result = Result.success(null);

        // WHEN
        Executable command = () -> result.getObjectOrElse(null);

        // THEN
        assertThrows(NullPointerException.class, command);
    }

    @Test
    void testGetObjectNonNull() {
        // GIVEN
        var result = Result.failure(ERROR_TEXT);

        // WHEN
        Executable command = result::getNonNullObject;

        // THEN
        assertThrows(NullPointerException.class, command);
    }

    @Test
    void testGetObjectOptionalNotPresent() {
        // GIVEN
        var result = Result.successVoid();

        // WHEN
        Optional<Void> optional = result.getOptionalObject();

        // THEN
        assertTrue(optional.isEmpty());
    }

    @Test
    void testGetObjectOptionalPresent() {
        // GIVEN
        var result = Result.success(OBJECT);

        // WHEN
        Optional<Object> optional = result.getOptionalObject();

        // THEN
        assertTrue(optional.isPresent());
    }

    @Test
    void testResolveOnSuccess() {
        // GIVEN
        var result = Result.success(OBJECT);

        // WHEN
        result.resolve(successMapper, failureMapper);

        // THEN
        verifyNoInteractions(failureMapper);
        verify(successMapper).apply(any());
    }

    @Test
    void testResolveOnFailure() {
        // GIVEN
        var result = Result.failure(ERROR_TEXT);

        // WHEN
        result.resolve(successMapper, failureMapper);

        // THEN
        verifyNoInteractions(successMapper);
        verify(failureMapper).apply(any());
    }

    @Test
    void testMapObject() {
        // GIVEN
        var result = Result.success(10);

        // WHEN
        String mapped = result.mapObject(String::valueOf);

        // THEN
        assertEquals("10", mapped);
    }

    @Test
    void testMapObjectOnNullFunction() {
        // GIVEN
        var result = Result.success(10);

        // WHEN
        Executable command = () -> result.mapObject(null);

        // THEN
        assertThrows(NullPointerException.class, command);
    }

    @Test
    void testErrors() {
        // GIVEN
        var result = Result.failure(ERROR_TEXT);

        // WHEN
        var errors = result.getErrors();

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
                Arguments.of(Result.failure(ERROR_TEXT, ERROR_TEXT), Result.failure(ERROR_TEXT), false),
                Arguments.of(Result.success(null), Result.success(null), true),
                Arguments.of(Result.successVoid(ERROR_TEXT), Result.successVoid(ERROR_TEXT), true),
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
        var result = Result.failure(new Error(ERROR_TEXT, new RuntimeException(), emptyMap()));

        // WHEN
        var string = result.toString();

        // THEN
        assertEquals("Result{success=false, object=null, errors=[Error[message=something went wrong, " +
                "throwable=java.lang.RuntimeException, metadata={}]]}", string);
    }

    @Test
    void testBuilder() {
        // WHEN
        var result = Result.builder()
                .success(true)
                .object(1L)
                .errors(List.of(ERROR))
                .error(ERROR)
                .build();

        // THEN
        assertTrue(result.isSuccess());
        assertNotNull(result.getObject());
        assertEquals(ERROR, result.firstError());
    }

    @Test
    void testSuccessBuilder() {
        // WHEN
        var result = Result.successBuilder().build();

        // THEN
        assertTrue(result.isSuccess());
    }

    @Test
    void testFailureBuilder() {
        // WHEN
        var result = Result.failureBuilder().build();

        // THEN
        assertTrue(result.isFailure());
    }

    @Test
    void testThrowOnNotSetSuccessFlag() {
        // WHEN
        Executable command = () -> Result.builder().build();

        // THEN
        assertThrows(ResultException.class, command);
    }
}