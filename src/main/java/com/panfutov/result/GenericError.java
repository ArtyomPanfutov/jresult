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
 * A generic error interface to be used in the result.
 */
public interface GenericError {

    /**
     * A text message of an error. Required.
     *
     * @return A string containing the error.
     */
    String getMessage();

    /**
     * A some exception that could a cause of the failure. Optional.
     *
     * @return A throwable object.
     */
    Optional<Throwable> getThrowable();

    /**
     * A map with metadata for an error, e.g. severity of an error. Optional.
     *
     * @return A map with metadata.
     */
    Optional<Map<String, ?>> getMetadata();

}
