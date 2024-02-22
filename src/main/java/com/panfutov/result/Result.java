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
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * A generic container for a result of an operation and errors.
 * To be used as a return type in the business code to avoid using the exceptions for the control flow.
 *
 * @param <T> A type parameter of a result object.
 */
public class Result<T> {

    /**
     * The Void result instance.
     */
    protected static final Result<Void> SUCCESS_VOID = new Result<>(true, null, emptyList());

    /**
     * A flag indicating the success/failure of an operation.
     */
    protected final boolean success;

    /**
     * An object that is the result of an operation.
     */
    protected final T object;

    /**
     * A list with errors.
     */
    protected final List<GenericError> errors;

    protected Result(boolean success, T object, List<GenericError> errors) {
        this.success = success;
        this.object = object;
        this.errors = errors;
    }

    /**
     * Constructs a result object for a succeeded operation.
     *
     * @param object An object that is the result of an operation.
     * @param <T> A type parameter.
     *
     * @return Success result with a wrapped object.
     */
    public static <T> Result<T> success(T object) {
        return new Result<>(true, object, emptyList());
    }

    /**
     * Constructs a result object for a succeeded operation with not critical errors.
     *
     * @param object An object that is the result of an operation.
     * @param errors The list of errors that occurred during an operation.
     * @param <T> A type parameter.
     *
     * @return Success result with a wrapped object and errors.
     */
    public static <T> Result<T> success(T object, List<GenericError> errors) {
        return new Result<>(true, object, unmodifiableList(errors));
    }

    /**
     * Constructs a result object for a succeeded operation with not critical error message.
     *
     * @param object An object that is the result of an operation.
     * @param message An error message that occurred during an operation.
     * @param <T> A type parameter.
     *
     * @return Success result with a wrapped object and errors.
     */
    public static <T> Result<T> success(T object, String message) {
        return new Result<>(true, object, List.of(new Error(message)));
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
     * Constructs a result object for a succeeded operation with not critical error message and no object.
     *
     * @param message An error message that occurred during an operation.
     *
     * @return Success result.
     */
    public static Result<Void> successVoid(String message) {
        return new Result<>(true, null, List.of(new Error(message)));
    }

    /**
     * Constructs a result object for a succeeded operation with no wrapped object.
     *
     * @param errors The error messages that occurred during an operation.
     *
     * @return Success result.
     */
    public static Result<Void> successVoid(List<GenericError> errors) {
        return new Result<>(true, null, unmodifiableList(errors));
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param errors The errors that have been collected during an operation.
     * @param <T> A type parameter.
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(List<GenericError> errors) {
        return new Result<>(false, null, unmodifiableList(errors));
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param error The error message that occurred during an operation.
     * @param <T> A type parameter.
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(GenericError error) {
        return new Result<>(false, null, List.of(error));
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param errorMessage The error message that occurred during an operation.
     * @param <T> A type parameter.
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(String errorMessage) {
        return new Result<>(false, null, List.of(new Error(errorMessage)));
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param throwable An exception that occurred during an operation
     * @param <T> A type parameter.
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(Throwable throwable) {
        return new Result<>(false, null, List.of(new Error(throwable)));
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param message An error message that occurred during an operation.
     * @param throwable An exception that occurred during an operation.
     * @param <T> A type parameter.
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(String message, Throwable throwable) {
        return new Result<>(false, null, List.of(new Error(message, throwable)));
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param message An error message that occurred during an operation.
     * @param throwable An exception that occurred during an operation.
     * @param metadata A metadata map for additional information about the error.
     * @param <T> A type parameter.
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(String message, Throwable throwable, Map<String, ?> metadata) {
        return new Result<>(false, null, List.of(new Error(message, throwable, metadata)));
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param firstError The error message that occurred during an operation.
     * @param otherErrors The additional error messages to complement the first error
     * @param <T> A type parameter.
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(GenericError firstError, GenericError... otherErrors) {
        var builder = Result.<T>failureBuilder()
                .error(firstError);

        for (var another : otherErrors) {
            builder.error(another);
        }
        return builder.build();
    }

    /**
     * Constructs a result object for a failed operation.
     *
     * @param firstError The error message that occurred during an operation.
     * @param otherErrors The additional error messages to complement the first error
     * @param <T> A type parameter.
     *
     * @return Error result.
     */
    public static <T> Result<T> failure(String firstError, String ... otherErrors) {
        var builder = Result.<T>failureBuilder()
                .error(new Error(firstError));

        for (var another : otherErrors) {
            builder.error(new Error(another));
        }
        return builder.build();
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
     * Inverted value of isSuccess() method.
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
    public GenericError firstError() {
        if (!hasErrors()) {
            throw new ResultException("Can't get the first error. The errors are null or empty.");
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
        requireNonNull(action);
        if (isSuccess()) {
            return action.get();
        }
        return this;
    }

    /**
     * Performs a function if the current result is success producing a new result.
     *
     * @param function A function to apply.
     */
    public Result<T> ifSuccessApply(Function<Result<T>, Result<T>> function) {
        requireNonNull(function);
        if (isSuccess()) {
            return function.apply(this);
        }
        return this;
    }

    /**
     * Performs a function using the value object if the current result is success producing a new result.
     *
     * @param function A function to apply.
     */
    public Result<T> ifSuccessUseObject(Function<T, Result<T>> function) {
        requireNonNull(function);
        if (isSuccess()) {
            return function.apply(this.getNonNullObject());
        }
        return this;
    }

    /**
     * Performs an action if the current result is success;
     *
     * @param action An action to execute.
     */
    public void ifSuccess(Consumer<Result<T>> action) {
        requireNonNull(action);
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
        requireNonNull(action);
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
    public T getObject() {
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
    public T getObjectOrElse(T other) {
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
    public T getObjectOrElse(Supplier<T> supplier) {
        requireNonNull(supplier);
        return object == null ? supplier.get() : object;
    }

    /**
     * Returns a non null object.
     *
     * @return A result object that is not null.
     *
     * @throws NullPointerException if {@code object} is null
     */
    public T getNonNullObject() {
        return requireNonNull(object);
    }

    /**
     * Returns an optional of an object.
     *
     * @return An optional of a result object.
     */
    public Optional<T> getOptionalObject() {
        return Optional.ofNullable(object);
    }

    /**
     * Resolves an operation result object to a target object depending on an operation outcome.
     *
     * @param successResolver A success resolver function. Required.
     * @param failureResolver A failure resolver function. Required.
     *
     * @return Resolved object.
     *
     * @param <U> A target type parameter.
     */
    public <U> U resolve(Function<Result<T>, ? extends U> successResolver,
                         Function<Result<T>, ? extends U> failureResolver) {
        requireNonNull(successResolver);
        requireNonNull(failureResolver);
        if (isSuccess()) {
            return successResolver.apply(this);
        }
        return failureResolver.apply(this);
    }

    /**
     * Maps an operation result object to a target object.
     *
     * @param mapper A mapper function. Required.
     *
     * @return A mapped object.
     *
     * @param <U> A target type parameter.
     */
    public <U> U mapObject(Function<T, ? extends U> mapper) {
        requireNonNull(mapper);
        return mapper.apply(this.getNonNullObject());
    }

    /**
     * The errors that have been collected during an operation.
     *
     * @return A list of errors.
     */
    public List<GenericError> getErrors() {
        return errors;
    }

    /**
     * Performs an action on each error.
     * To use without explicit access to errors list.
     *
     * @param action An action to execute.
     */
    public void forEachError(Consumer<? super GenericError> action) {
        requireNonNull(action);
        errors.forEach(action);
    }

    /**
     * A builder for operation result.
     *
     * @return A builder object.
     *
     * @param <T> a type parameter of a result object.
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * A builder for a succeeded operation.
     *
     * @return A builder object with the success flag set to true.
     *
     * @param <T> a type parameter of a result object.
     */
    public static <T> Builder<T> successBuilder() {
        return Result.<T>builder()
                .success(true);
    }

    /**
     * A builder for a failed operation.
     *
     * @return A builder object with the success flag set to false.
     *
     * @param <T> a type parameter of a result object.
     */
    public static <T> Builder<T> failureBuilder() {
        return Result.<T>builder()
                .success(false);
    }

    /**
     * A builder class to make constructor of the result object more readable.
     *
     * @param <T> a type parameter of a result object.
     */
    public static class Builder<T> {
        private Boolean success;
        private T object;
        private List<GenericError> errors;


        /**
         * A private constructor.
         */
        private Builder() {
        }

        /**
         * Sets the success flag.
         *
         * @param success True — if operation succeeded, otherwise — False.
         *
         * @return Builder, for chaining.
         */
        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        /**
         * Sets the object.
         *
         * @param object A result object of an operation. Optional.
         *
         * @return Builder, for chaining.
         */
        public Builder<T> object(T object) {
            this.object = object;
            return this;
        }

        /**
         * Sets the errors.
         *
         * @param errors The error list that occurred during an operation.
         *
         * @return Builder, for chaining.
         */
        public Builder<T> errors(List<GenericError> errors) {
            ensureErrors();
            this.errors.addAll(errors);
            return this;
        }

        /**
         * Adds the error to the error list.
         *
         * @param error The error that occurred during an operation.
         *
         * @return Builder, for chaining.
         */
        public Builder<T> error(GenericError error) {
            ensureErrors();
            this.errors.add(error);
            return this;
        }

        private void ensureErrors() {
            if (this.errors == null) {
                this.errors = new ArrayList<>();
            }
        }

        /**
         * Builds the result.
         *
         * @return Result object.
         */
        public Result<T> build() {
            if (success == null) {
                throw new ResultException("The required success field is not set!");
            }
            List<GenericError> immutableErrors = errors == null
                    ? emptyList()
                    : Collections.unmodifiableList(errors);
            return new Result<>(success, object, immutableErrors);
        }
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