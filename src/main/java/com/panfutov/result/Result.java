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


import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

/**
 * A generic container for a result of an operation and the error messages.
 * To be used as a return type in the business code to avoid using the exceptions for the control flow.
 *
 * @param <T> A type parameter of a result object.
 */
public class Result<T> {

    private static final Result<Void> SUCCESS_VOID = new Result<>(true, null, emptyList());

    private final boolean success;

    private final T object;

    private final List<String> errors;

    public Result(boolean success, T object, List<String> errors) {
        this.success = success;
        this.object = object;
        this.errors = errors;
    }

    public Result(boolean success, T object, String errorMessage) {
        this.success = success;
        this.object = object;
        this.errors = List.of(errorMessage);
    }

    /**
     * Constructs a result object for a succeeded operation.
     *
     * @param object An object that is the result of an operation.
     *
     * @return Success result with a wrapped object.
     */
    public static <T> Result<T> success(T object) {
        return new Result<>(true, object, emptyList());
    }

    /**
     * Constructs a result object for a succeeded operation with error messages.
     *
     * @param object An object that is the result of an operation.
     * @param errors The errors that occurred during the operation.
     *
     * @return Success result with a wrapped object and errors.
     */
    public static <T> Result<T> success(T object, List<String> errors) {
        return new Result<>(true, object, errors);
    }

    /**
     * Constructs a result object for a succeeded operation with error messages.
     *
     * @param object An object that is the result of an operation.
     * @param error An error message that occurred during the operation.
     *
     * @return Success result with a wrapped object and errors.
     */
    public static <T> Result<T> success(T object, String error) {
        return new Result<>(true, object, List.of(error));
    }

    /**
     * Constructs a result object for a succeeded operation with no wrapped object.
     *
     * @return Success result.
     */
    public static Result<Void> successVoid() {
        return SUCCESS_VOID;
    }

    /**
     * Constructs a result object for a succeeded operation with no wrapped object.
     *
     * @param error An error message that occurred during the operation.
     *
     * @return Success result.
     */
    public static Result<Void> successVoid(String error) {
        return new Result<>(true, null, List.of(error));
    }

    /**
     * Constructs a result object for a succeeded operation with no wrapped object.
     *
     * @param errors The error messages that occurred during the operation.
     *
     * @return Success result.
     */
    public static Result<Void> successVoid(List<String> errors) {
        return new Result<>(true, null, errors);
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param errors The errors that have been collected during the operation.
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(List<String> errors) {
        return new Result<>(false, null, errors);
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param error The error message that occurred during the operation.
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(String error) {
        return new Result<>(false, null, List.of(error));
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param firstError The error message that occurred during the operation.
     * @param otherErrors The additional error messages to complement the first error
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(String firstError, String... otherErrors) {
        List<String> errors = new ArrayList<>();
        errors.add(firstError);
        Collections.addAll(errors, otherErrors);
        return new Result<>(false, null, errors);
    }

    /**
     * A boolean status of an operation.
     * Could be true even if the errors are not empty (e.g. not critical errors).
     *
     * @return True — if success. False — if error.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Inverted value of {@link this#isSuccess()}
     *
     * @return True — if error. False — if true.
     */
    public boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Checks if a collection with errors has errors.
     *
     * @return True — if there are at least on error message; False — otherwise
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * Gets the size of a collections with errors.
     *
     * @return The number of errors if a collection with errors is not null, otherwise — 0.
     */
    public int errorCount() {
        return errors != null ? errors.size() : 0;
    }

    /**
     * Gets the first error message
     *
     * @return The first error message.
     *
     * @throws NoSuchElementException if errors are null or empty
     */
    public String firstError() {
        if (!hasErrors()) {
            throw new NoSuchElementException("The errors are null or empty");
        }
        return errors.getFirst();
    }

    /**
     * Performs an action if the current result is success;
     *
     * @param action An action to execute.
     *
     * @return A result of the executed action, or the current result if it failed.
     */
    public Result<T> ifSuccess(Supplier<Result<T>> action) {
        if (isSuccess()) {
            return action.get();
        }
        return this;
    }

    /**
     * Performs an action if the current result is success;
     *
     * @param action An action to execute.
     *
     * @return A result of the executed action, or the current result if it failed.
     */
    public void ifSuccess(Consumer<Result<T>> action) {
        if (isSuccess()) {
            action.accept(this);
        }
    }

    /**
     * Performs an action if the current status is error.
     *
     * @param action An action to execute.
     *
     * @return The current result.
     */
    public Result<T> ifFailure(Consumer<Result<T>> action) {
        if (isFailure()) {
            action.accept(this);
        }
        return this;
    }

    /**
     * An object that is a result of an operation.
     * Can be null for the Void result and the failed operations.
     *
     * @return A result object.
     */
    public T object() {
        return object;
    }

    /**
     * An object that is a result of an operation or a default object that is passed as a parameter
     * if the result object is null.
     *
     * @param other a default object
     *
     * @return A result object.
     */
    public T objectOrElse(T other) {
        return object == null ? other : object;
    }

    /**
     * An object that is a result of an operation or a default object from the supplier function if the result object
     * is null.
     *
     * @param supplier a supplier function which is evaluated only if the reuslt object is null.
     *
     * @return A result object.
     */
    public T objectOrElseGet(Supplier<T> supplier) {
        return object == null ? supplier.get() : object;
    }

    /**
     * Returns an object with a non-null check.
     *
     * @return A result object that is not null.
     *
     * @throws NullPointerException if {@code object} is null
     */
    public T objectNonNull() {
        return requireNonNull(object);
    }

    /**
     * The errors that have been collected during the operation.
     *
     * @return A list of error messages.
     */
    public List<String> errors() {
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Result<?> result = (Result<?>) o;
        return success == result.success && Objects.equals(object, result.object) && Objects.equals(errors, result.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, object, errors);
    }

    @Override
    public String toString() {
        return "Result{"
                + "success=" + success
                + ", object=" + object
                + ", errors=" + errors
                + '}';
    }
}