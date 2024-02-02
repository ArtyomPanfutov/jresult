package com.panfutov.result;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {

    private static final String TEXT = "something went wrong";
    private static final RuntimeException THROWABLE = new RuntimeException(TEXT);
    private static final Map<String, String> METADATA = Map.of("Severity", "error");

    @Test
    void testJustText() {
        // WHEN
        var error = Error.justText(TEXT);

        // THEN
        assertFalse(error.getMessage().isEmpty());
        assertTrue(error.getMetadata().isEmpty());
        assertTrue(error.getThrowable().isEmpty());
    }

    @Test
    void testWithMetadata() {
        // WHEN
        var error = Error.withMetadata(TEXT, METADATA);

        // THEN
        assertTrue(error.getMetadata().isPresent());
    }

    @Test
    void testFromThrowable() {
        // WHEN
        var error = Error.fromThrowable(new RuntimeException(TEXT));

        // THEN
        assertTrue(error.getThrowable().isPresent());
        assertFalse(error.getMessage().isEmpty());
    }

    @Test
    void testFromThrowableWithMetadata() {
        // WHEN
        var error = Error.fromThrowable(THROWABLE, METADATA);

        // THEN
        assertTrue(error.getThrowable().isPresent());
        assertFalse(error.getMessage().isEmpty());
    }

    @Test
    void testMessageWithThrowable() {
        // WHEN
        var error = new Error(TEXT, THROWABLE);

        // THEN
        assertTrue(error.getThrowable().isPresent());
        assertFalse(error.getMessage().isEmpty());
    }

}