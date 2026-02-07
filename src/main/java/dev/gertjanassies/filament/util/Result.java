package dev.gertjanassies.filament.util;

import java.util.function.Function;

/**
 * A Result type to represent success or failure of an operation.
 * This is a sealed interface with two implementations: Success and Failure.
 * It can be used to return either a successful result or an error without throwing exceptions.
 * Checked exceptions can be represented as a Failure, allowing for more functional error handling.
 * @param <T> The type of the successful result
 * @param <E> The type of the error
 */
public sealed interface Result<T, E> permits Result.Success, Result.Failure {
    record Success<T, E>(T value) implements Result<T, E> {}
    record Failure<T, E>(E error) implements Result<T, E> {}

    /**
     * Check if the result is a success. 
     * @return true if this is a Success, false otherwise
     */
    default boolean isSuccess() {
        return this instanceof Success<T, E>;
    }

    /**
     * Check if the result is a failure.
     * @return true if this is a Failure, false otherwise
     */
    default boolean isFailure() {
        return this instanceof Failure<T, E>;
    }

    /**
     * Get the successful value if this is a Success, or throw an exception if this is a Failure.
     * @return the successful value
      * @throws IllegalStateException if this is a Failure
     */
    default T value() {
        if (this instanceof Success<T, E> success) {
            return success.value();
        }
        throw new IllegalStateException("Cannot get value from a Failure result");
    }
    /** 
     * Get the error value if this is a Failure, or throw an exception if this is a Success.
     * @return the error value
     * @throws IllegalStateException if this is a Success
     */
    default E error() {
        if (this instanceof Failure<T, E> failure) {
            return failure.error();
        }
        throw new IllegalStateException("Cannot get error from a Success result");
    } 

    /**
     * Transform the successful value using the provided mapper function.
     * If this is a Failure, the error is propagated without applying the mapper.
     * @param mapper The function to transform the successful value
     * @param <U> The type of the transformed successful value
     * @return A new Result containing the transformed value or the original error
     */
    default <U> Result<U, E> map(Function<T, U> mapper) {
        return switch (this) {
            case Success(var value) -> new Success<>(mapper.apply(value));
            case Failure(var error) -> new Failure<>(error);
        };
    }
    
    /** 
     * Transform the successful value using the provided mapper function that returns a Result.
     * This allows for chaining operations that may also fail.
     * If this is a Failure, the error is propagated without applying the mapper.
     * @param mapper The function to transform the successful value into another Result
     * @param <U> The type of the transformed successful value
     * @return A new Result containing the transformed value or the original error
     */
    default <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper) {
        return switch (this) {
            case Success<T, E>(T value) -> mapper.apply(value);
            case Failure<T, E>(E error) -> new Failure<>(error);
        };
    }
    
    /** 
     * Transform the error value using the provided mapper function.
     * If this is a Success, the value is propagated without applying the mapper.
     * @param mapper The function to transform the error value
     * @param <F> The type of the transformed error value
     * @return A new Result containing the original value or the transformed error
     */
    default <F> Result<T, F> mapError(Function<E, F> mapper) {
        return switch (this) {
            case Success<T, E>(T value) -> new Success<>(value);
            case Failure<T, E>(E error) -> new Failure<>(mapper.apply(error));
        };
    }

    /** 
     * Fold the Result into a single value by providing functions to handle both success and failure cases.
     */
    default <R> R fold(Function<E, R> onFailure, Function<T, R> onSuccess) {
        return switch (this) {
            case Success<T, E>(T value) -> onSuccess.apply(value);
            case Failure<T, E>(E error) -> onFailure.apply(error);
        };
    }
}