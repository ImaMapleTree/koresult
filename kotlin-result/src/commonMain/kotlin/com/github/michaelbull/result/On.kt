package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Invokes an [action] if this result [is ok][KoResult.isOk].
 *
 * - Rust: [Result.inspect](https://doc.rust-lang.org/std/result/enum.Result.html#method.inspect)
 */
public inline infix fun <V, E> KoResult<V, E>.onSuccess(action: (V) -> Unit): KoResult<V, E> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (isOk) {
        action(value)
    }

    return this
}

/**
 * Invokes an [action] if this result [is an error][KoResult.isErr].
 *
 * - Rust [Result.inspect_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.inspect_err)
 */
public inline infix fun <V, E> KoResult<V, E>.onFailure(action: (E) -> Unit): KoResult<V, E> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (isErr) {
        action(error)
    }

    return this
}
