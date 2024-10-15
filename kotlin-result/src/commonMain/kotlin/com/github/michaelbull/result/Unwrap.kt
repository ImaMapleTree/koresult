package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class UnwrapException(message: String) : Exception(message)

/**
 * Returns the [value][KoResult.value] if this result [is ok][KoResult.isOk], otherwise throws an
 * [UnwrapException].
 *
 * - Rust: [Result.unwrap](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap)
 *
 * @throws UnwrapException if this result [is an error][KoResult.isErr].
 */
public fun <V, E> KoResult<V, E>.unwrap(): V {
    return when {
        isOk -> value
        else -> throw UnwrapException("called Result.unwrap on an Err value $error")
    }
}

/**
 * Returns the [value][KoResult.value] if this result [is ok][KoResult.isOk], otherwise throws an
 * [UnwrapException] with the specified [message].
 *
 * - Rust: [Result.expect](https://doc.rust-lang.org/std/result/enum.Result.html#method.expect)
 *
 * @param message The message to include in the [UnwrapException] if this result
 * [is an error][KoResult.isErr].
 *
 * @throws UnwrapException if this result [is an error][KoResult.isErr].
 */
public inline infix fun <V, E> KoResult<V, E>.expect(message: () -> Any): V {
    contract {
        callsInPlace(message, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> value
        else -> throw UnwrapException("${message()} $error")
    }
}

/**
 * Returns the [error][KoResult.error] if this result [is an error][KoResult.isErr], otherwise throws
 * an [UnwrapException].
 *
 * - Rust: [Result.unwrap_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap_err)
 *
 * @throws UnwrapException if this result [is ok][KoResult.isOk].
 */
public fun <V, E> KoResult<V, E>.unwrapError(): E {
    return when {
        isErr -> error
        else -> throw UnwrapException("called Result.unwrapError on an Ok value $value")
    }
}

/**
 * Returns the [error][KoResult.error] if this result [is an error][KoResult.isErr], otherwise throws
 * an [UnwrapException] with the specified [message].
 *
 * - Rust: [Result.expect_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.expect_err)
 *
 * @param message The message to include in the [UnwrapException] if this result
 * [is ok][KoResult.isOk].
 *
 * @throws UnwrapException if this result [is ok][KoResult.isOk].
 */
public inline infix fun <V, E> KoResult<V, E>.expectError(message: () -> Any): E {
    contract {
        callsInPlace(message, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr -> error
        else -> throw UnwrapException("${message()} $value")
    }
}
