package com.example.multitenant.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;

import com.example.multitenant.exceptions.AsyncOperationException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * scopes are still in preview and they are to be changed in java 25
 * therefore we have a warapper which ease the ability of changing the implementation in case its needed.
 * and its also to have more maintainable and clean code.
 * 
 */
@Slf4j
@RequiredArgsConstructor
public class VirtualThreadsUtils {
    public static <T1, T2, T3> Triple<T1, T2, T3> run(Supplier<T1> left, Supplier<T2> middle, Supplier<T3> right) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var leftTask = scope.fork(() -> left.get());
            var middleTask = scope.fork(() -> middle.get());
            var rightTask = scope.fork(() -> right.get());

            scope.join();
            scope.throwIfFailed();

            return Triple.of(leftTask.get(), middleTask.get(), rightTask.get());
        } catch (InterruptedException | ExecutionException ex) {
            var msg = String.format("an error occurred during concurrent tasks execution: %s", ex.getMessage());
            log.error(msg, ex);
            throw new AsyncOperationException(msg, ex);
        }
    }

    public static <T1, T2> Pair<T1, T2> run(Supplier<T1> left, Supplier<T2> right) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var leftTask = scope.fork(() -> left.get());
            var rightTask = scope.fork(() -> right.get());

            scope.join();
            scope.throwIfFailed();

            return Pair.of(leftTask.get(), rightTask.get());
        } catch (InterruptedException | ExecutionException ex) {
            var msg = String.format("an error occurred during concurrent tasks execution: %s", ex.getMessage());
            log.error(msg, ex);
            throw new AsyncOperationException(msg, ex);
        }
    }
}