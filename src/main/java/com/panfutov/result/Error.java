/*
 * MIT License
 *
 * Copyright (c) 2024 Artyom Panfutov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.panfutov.result;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.unmodifiableMap;

/**
 * A concrete error implementation.
 *
 * @param message {@link GenericError#getMessage()}
 * @param throwable {@link GenericError#getThrowable()}
 * @param metadata {@link GenericError#getMetadata()}
 */
public record Error(String message, Throwable throwable, Map<String, ?> metadata) implements GenericError {

    /**
     * Creates an error with message.
     *
     * @param message An error message.
     */
    public Error(String message) {
        this(message, null, null);
    }

    /**
     * Creates an error from a throwable.
     *
     * @param throwable A source throwable.
     */
    public Error(Throwable throwable) {
        this(throwable.getMessage(), throwable, null);
    }

    /**
     * Creates an error from a throwable and metadata map.
     *
     * @param throwable A source throwable.
     * @param metadata A metadata map.
     */
    public Error(Throwable throwable, Map<String, ?> metadata) {
        this(throwable.getMessage(), throwable, unmodifiableMap(metadata));
    }

    /**
     * Creates an error from throwable with a custom message.
     *
     * @param message An error message.
     * @param throwable A throwable.
     */
    public Error(String message, Throwable throwable) {
        this(message, throwable, null);
    }

    /**
     * Creates an error with message and metadata map.
     *
     * @param message An error message.
     * @param metadata A metadata map.
     */
    public Error(String message, Map<String, ?> metadata) {
        this(message, null, Collections.unmodifiableMap(metadata));
    }

    /**
     * Creates an error object with a message.
     *
     * @param message An error message.
     *
     * @return Created error.
     */
    public static Error justText(String message) {
        return new Error(message);
    }

    /**
     * Creates an error object with metadata and a message.
     *
     * @param text An error message.
     * @param metadata A metadata map.
     *
     * @return Created error.
     */
    public static Error withMetadata(String text, Map<String, ?> metadata) {
        return new Error(text, metadata);
    }

    /**
     * Creates an error from a Throwable.
     * The error message is derived from the Throwable#getMessage()
     *
     * @param throwable A source throwable.
     *
     * @return Created error.
     */
    public static Error fromThrowable(Throwable throwable) {
        return new Error(throwable);
    }

    /**
     * Creates an error from a Throwable and metadata map.
     * The error message is derived from the Throwable#getMessage()
     *
     * @param throwable A source throwable.
     * @param metadata Metadata map.
     *
     * @return Created error.
     */
    public static Error fromThrowable(Throwable throwable, Map<String, ?> metadata) {
        return new Error(throwable, metadata);
    }

    @Override
    public String getMessage() {
        return message();
    }

    @Override
    public Optional<Throwable> getThrowable() {
        return Optional.ofNullable(throwable());
    }

    @Override
    public Optional<Map<String, ?>> getMetadata() {
        return Optional.ofNullable(metadata());
    }
}