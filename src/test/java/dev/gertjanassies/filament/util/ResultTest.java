package dev.gertjanassies.filament.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ResultTest {

    @Test
    void testSuccessCreation() {
        // Given
        Result<String, String> result = new Result.Success<>("value");

        // Then
        assertThat(result).isInstanceOf(Result.Success.class);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
    }

    @Test
    void testFailureCreation() {
        // Given
        Result<String, String> result = new Result.Failure<>("error");

        // Then
        assertThat(result).isInstanceOf(Result.Failure.class);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void testValueOnSuccess() {
        // Given
        Result<String, String> result = new Result.Success<>("test value");

        // When
        String value = result.value();

        // Then
        assertThat(value).isEqualTo("test value");
    }

    @Test
    void testValueOnFailureThrowsException() {
        // Given
        Result<String, String> result = new Result.Failure<>("error");

        // When/Then
        assertThatThrownBy(result::value)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot get value from a Failure result");
    }

    @Test
    void testErrorOnFailure() {
        // Given
        Result<String, String> result = new Result.Failure<>("error message");

        // When
        String error = result.error();

        // Then
        assertThat(error).isEqualTo("error message");
    }

    @Test
    void testErrorOnSuccessThrowsException() {
        // Given
        Result<String, String> result = new Result.Success<>("value");

        // When/Then
        assertThatThrownBy(result::error)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot get error from a Success result");
    }

    @Test
    void testMapOnSuccess() {
        // Given
        Result<Integer, String> result = new Result.Success<>(5);

        // When
        Result<String, String> mapped = result.map(i -> "Number: " + i);

        // Then
        assertThat(mapped.isSuccess()).isTrue();
        assertThat(mapped.value()).isEqualTo("Number: 5");
    }

    @Test
    void testMapOnFailure() {
        // Given
        Result<Integer, String> result = new Result.Failure<>("error");

        // When
        Result<String, String> mapped = result.map(i -> "Number: " + i);

        // Then
        assertThat(mapped.isFailure()).isTrue();
        assertThat(mapped.error()).isEqualTo("error");
    }

    @Test
    void testMapChaining() {
        // Given
        Result<Integer, String> result = new Result.Success<>(10);

        // When
        Result<String, String> mapped = result
            .map(i -> i * 2)
            .map(i -> i + 5)
            .map(i -> "Result: " + i);

        // Then
        assertThat(mapped.isSuccess()).isTrue();
        assertThat(mapped.value()).isEqualTo("Result: 25");
    }

    @Test
    void testFlatMapOnSuccess() {
        // Given
        Result<Integer, String> result = new Result.Success<>(5);

        // When
        Result<String, String> flatMapped = result.flatMap(i -> 
            i > 0 ? new Result.Success<>("Positive: " + i) : new Result.Failure<>("Not positive")
        );

        // Then
        assertThat(flatMapped.isSuccess()).isTrue();
        assertThat(flatMapped.value()).isEqualTo("Positive: 5");
    }

    @Test
    void testFlatMapOnSuccessReturningFailure() {
        // Given
        Result<Integer, String> result = new Result.Success<>(-5);

        // When
        Result<String, String> flatMapped = result.flatMap(i -> 
            i > 0 ? new Result.Success<>("Positive: " + i) : new Result.Failure<>("Not positive")
        );

        // Then
        assertThat(flatMapped.isFailure()).isTrue();
        assertThat(flatMapped.error()).isEqualTo("Not positive");
    }

    @Test
    void testFlatMapOnFailure() {
        // Given
        Result<Integer, String> result = new Result.Failure<>("original error");

        // When
        Result<String, String> flatMapped = result.flatMap(i -> 
            new Result.Success<>("Value: " + i)
        );

        // Then
        assertThat(flatMapped.isFailure()).isTrue();
        assertThat(flatMapped.error()).isEqualTo("original error");
    }

    @Test
    void testFlatMapChaining() {
        // Given
        Result<Integer, String> result = new Result.Success<>(10);

        // When
        Result<String, String> chained = result
            .flatMap(i -> i > 0 ? new Result.Success<>(i * 2) : new Result.Failure<>("Negative"))
            .flatMap(i -> i < 100 ? new Result.Success<>(i + 5) : new Result.Failure<>("Too large"))
            .map(i -> "Final: " + i);

        // Then
        assertThat(chained.isSuccess()).isTrue();
        assertThat(chained.value()).isEqualTo("Final: 25");
    }

    @Test
    void testMapErrorOnFailure() {
        // Given
        Result<String, String> result = new Result.Failure<>("error");

        // When
        Result<String, Integer> mapped = result.mapError(String::length);

        // Then
        assertThat(mapped.isFailure()).isTrue();
        assertThat(mapped.error()).isEqualTo(5);
    }

    @Test
    void testMapErrorOnSuccess() {
        // Given
        Result<String, String> result = new Result.Success<>("value");

        // When
        Result<String, Integer> mapped = result.mapError(String::length);

        // Then
        assertThat(mapped.isSuccess()).isTrue();
        assertThat(mapped.value()).isEqualTo("value");
    }

    @Test
    void testFoldOnSuccess() {
        // Given
        Result<Integer, String> result = new Result.Success<>(42);

        // When
        String folded = result.fold(
            error -> "Error: " + error,
            value -> "Success: " + value
        );

        // Then
        assertThat(folded).isEqualTo("Success: 42");
    }

    @Test
    void testFoldOnFailure() {
        // Given
        Result<Integer, String> result = new Result.Failure<>("oops");

        // When
        String folded = result.fold(
            error -> "Error: " + error,
            value -> "Success: " + value
        );

        // Then
        assertThat(folded).isEqualTo("Error: oops");
    }

    @Test
    void testComplexChaining() {
        // Given
        Result<String, String> result = new Result.Success<>("123");

        // When
        Result<Integer, String> complex = result
            .map(String::trim)
            .flatMap(s -> {
                try {
                    int num = Integer.parseInt(s);
                    return new Result.Success<>(num);
                } catch (NumberFormatException e) {
                    return new Result.Failure<>("Invalid number: " + s);
                }
            })
            .map(i -> i * 2)
            .flatMap(i -> i < 1000 ? new Result.Success<>(i) : new Result.Failure<>("Number too large"));

        // Then
        assertThat(complex.isSuccess()).isTrue();
        assertThat(complex.value()).isEqualTo(246);
    }

    @Test
    void testComplexChainingWithFailure() {
        // Given
        Result<String, String> result = new Result.Success<>("abc");

        // When
        Result<Integer, String> complex = result
            .map(String::trim)
            .flatMap(s -> {
                try {
                    int num = Integer.parseInt(s);
                    return new Result.Success<>(num);
                } catch (NumberFormatException e) {
                    return new Result.Failure<>("Invalid number: " + s);
                }
            })
            .map(i -> i * 2);

        // Then
        assertThat(complex.isFailure()).isTrue();
        assertThat(complex.error()).isEqualTo("Invalid number: abc");
    }

    @Test
    void testSuccessRecordEquality() {
        // Given
        Result<String, String> result1 = new Result.Success<>("value");
        Result<String, String> result2 = new Result.Success<>("value");
        Result<String, String> result3 = new Result.Success<>("different");

        // Then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
    }

    @Test
    void testFailureRecordEquality() {
        // Given
        Result<String, String> result1 = new Result.Failure<>("error");
        Result<String, String> result2 = new Result.Failure<>("error");
        Result<String, String> result3 = new Result.Failure<>("different");

        // Then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
    }

    @Test
    void testSuccessAndFailureNotEqual() {
        // Given
        Result<String, String> success = new Result.Success<>("value");
        Result<String, String> failure = new Result.Failure<>("value");

        // Then
        assertThat(success).isNotEqualTo(failure);
    }

    @Test
    void testFoldToSameType() {
        // Given
        Result<Integer, String> success = new Result.Success<>(42);
        Result<Integer, String> failure = new Result.Failure<>("error");

        // When
        int successResult = success.fold(e -> -1, v -> v);
        int failureResult = failure.fold(e -> -1, v -> v);

        // Then
        assertThat(successResult).isEqualTo(42);
        assertThat(failureResult).isEqualTo(-1);
    }

    @Test
    void testAttemptWithSuccessfulOperation() {
        // When
        Result<Integer, String> result = Result.attempt(() -> 42);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.value()).isEqualTo(42);
    }

    @Test
    void testAttemptWithThrowingOperation() {
        // When
        Result<Integer, String> result = Result.attempt(() -> {
            throw new RuntimeException("Something went wrong");
        });

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).isEqualTo("Something went wrong");
    }

    @SuppressWarnings("null")
    @Test
    void testAttemptWithNullPointerException() {
        // When
        Result<String, String> result = Result.attempt(() -> {
            String str = null;
            return str.toUpperCase();
        });

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).contains("null");
    }

    @Test
    void testAttemptWithComplexOperation() {
        // When
        Result<Integer, String> result = Result.attempt(() -> {
            int a = 10;
            int b = 5;
            return a / b;
        });

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.value()).isEqualTo(2);
    }

    @Test
    void testAttemptWithDivisionByZero() {
        // When
        Result<Integer, String> result = Result.attempt(() -> {
            int a = 10;
            int b = 0;
            return a / b;
        });

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).contains("by zero");
    }

    @Test
    void testOfWithSuccessfulOperation() {
        // When
        Result<Integer, Integer> result = Result.of(
            () -> 42,
            e -> e.getMessage().length()
        );

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.value()).isEqualTo(42);
    }

    @Test
    void testOfWithThrowingOperation() {
        // When
        Result<Integer, Integer> result = Result.of(
            () -> {
                throw new RuntimeException("error");
            },
            e -> e.getMessage().length()
        );

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).isEqualTo(5); // "error" has length 5
    }

    @Test
    void testOfWithCustomErrorMapping() {
        // When
        Result<Integer, String> result = Result.of(
            () -> {
                throw new IllegalArgumentException("Invalid argument");
            },
            e -> e.getClass().getSimpleName() + ": " + e.getMessage()
        );

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).isEqualTo("IllegalArgumentException: Invalid argument");
    }

    @Test
    void testAttemptCanBeChainedWithMap() {
        // When
        Result<String, String> result = Result.attempt(() -> 42)
            .map(i -> "Number: " + i);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.value()).isEqualTo("Number: 42");
    }

    @Test
    void testAttemptCanBeChainedWithFlatMap() {
        // When
        Result<Integer, String> result = Result.attempt(() -> "123")
            .flatMap(s -> Result.attempt(() -> Integer.parseInt(s)));

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.value()).isEqualTo(123);
    }

    @Test
    void testAttemptChainPropagatesFailure() {
        // When
        Result<Integer, String> result = Result.attempt(() -> "abc")
            .flatMap(s -> Result.attempt(() -> Integer.parseInt(s)));

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).contains("abc");
    }

    @Test
    void testAttemptWithCheckedExceptionSuccess() {
        // Given
        Path tempFile = Path.of("test-file.txt");
        
        // When
        Result<String, String> result = Result.attempt(() -> {
            // This throws checked IOException
            return Files.readString(tempFile);
        });

        // Then - file doesn't exist, so should fail
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).isNotNull();
    }

    @SuppressWarnings("unused")
    @Test
    void testAttemptWithCheckedExceptionIOException() {
        // When
        Result<String, String> result = Result.attempt(() -> {
            // Simulating code that throws checked IOException
            if (true) {
                throw new IOException("File not found");
            }
            return "content";
        });

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).isEqualTo("File not found");
    }

    @Test
    void testOfWithCheckedExceptionSuccess() {
        // When
        Result<byte[], String> result = Result.of(
            () -> Files.readAllBytes(Path.of("non-existent.txt")),
            e -> "IO Error: " + e.getMessage()
        );

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).startsWith("IO Error:");
    }

    @Test
    void testOfWithCheckedExceptionCustomMapping() {
        // When
        Result<String, Integer> result = Result.of(
            () -> {
                throw new IOException("Disk full");
            },
            e -> e.getMessage().length()
        );

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).isEqualTo(9); // "Disk full" length
    }

    @Test
    void testThrowingSupplierAllowsCheckedExceptions() {
        // Given - this demonstrates ThrowingSupplier accepts checked exceptions
        Result.ThrowingSupplier<String> supplier = () -> {
            // This would not compile without ThrowingSupplier
            throw new IOException("Checked exception");
        };

        // When
        Result<String, String> result = Result.attempt(supplier);

        // Then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error()).isEqualTo("Checked exception");
    }
}
