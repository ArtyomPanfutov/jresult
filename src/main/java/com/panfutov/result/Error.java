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

import java.util.Map;
import java.util.Optional;

/**
 * A concrete error implementation.
 *
 * @param message {@link GenericError#getMessage()}
 * @param throwable {@link GenericError#getThrowable()}
 * @param metadata {@link GenericError#getMetadata()}
 */
public record Error(String message, Throwable throwable, Map<String, ?> metadata) implements GenericError {

    public Error(String message) {
        this(message, null, null);
    }

    public Error(Throwable throwable) {
        this(throwable.getMessage(), throwable, null);
    }

    public Error(Throwable throwable, Map<String, ?> metadata) {
        this(throwable.getMessage(), throwable, metadata);
    }

    public Error(String message, Throwable throwable) {
        this(message, throwable, null);
    }

    public Error(String message, Map<String, ?> metadata) {
        this(message, null, metadata);
    }

    public static Error justText(String message) {
        return new Error(message);
    }

    public static Error withMetadata(String text, Map<String, ?> metadata) {
        return new Error(text, metadata);
    }

    public static Error fromThrowable(Throwable throwable) {
        return new Error(throwable);
    }

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