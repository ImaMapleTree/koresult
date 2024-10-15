package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns [result] if this result [is an error][KoResult.isErr], otherwise [this].
 *
 * - Rust: [Result.or](https://doc.rust-lang.org/std/result/enum.Result.html#method.or)
 */
public infix fun <V, E, F> KoResult<V, E>.or(result: KoResult<V, F>): KoResult<V, F> {
    return when {
        isOk -> coerceErrorType()
        else -> result
    }
}

/**
 * Returns the [transformation][transform] of the [error][KoResult.error] if this result
 * [is an error][KoResult.isErr], otherwise [this].
 *
 * - Rust: [Result.or_else](https://doc.rust-lang.org/std/result/enum.Result.html#method.or_else)
 */
public inline infix fun <V, E, F> KoResult<V, E>.orElse(transform: (E) -> KoResult<V, F>): KoResult<V, F> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> coerceErrorType()
        else -> transform(error)
    }
}

/**
 * Throws the [error][KoResult.error] if this result [is an error][KoResult.isErr], otherwise returns
 * [this].
 */
public fun <V, E : Throwable> KoResult<V, E>.orElseThrow(): KoOk<V> {
    return when {
        isOk -> coerceErrorType()
        else -> throw error
    }
}

/**
 * Throws the [error][KoResult.error] if this result [is an error][KoResult.isErr] and satisfies the
 * given [predicate], otherwise returns [this].
 *
 * @see [takeIf]
 */
public inline fun <V, E : Throwable> KoResult<V, E>.throwIf(predicate: (E) -> Boolean): KoResult<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && predicate(error) -> throw error
        else -> this
    }
}

/**
 * Throws the [error][KoResult.error] if this result [is an error][KoResult.isErr] and _does not_
 * satisfy the given [predicate], otherwise returns [this].
 *
 * @see [takeUnless]
 */
public inline fun <V, E : Throwable> KoResult<V, E>.throwUnless(predicate: (E) -> Boolean): KoResult<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && !predicate(error) -> throw error
        else -> this
    }
}
