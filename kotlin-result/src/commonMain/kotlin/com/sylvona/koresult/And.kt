package com.sylvona.koresult

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns [result] if this result [is ok][KoResult.isOk], otherwise [this].
 *
 * - Rust: [Result.and](https://doc.rust-lang.org/std/result/enum.Result.html#method.and)
 */
public infix fun <V, E, U> KoResult<V, E>.and(result: KoResult<U, E>): KoResult<U, E> {
    return when {
        isOk -> result
        else -> coerceValueType()
    }
}

/**
 * Maps this [Result<V, E>][KoResult] to [Result<U, E>][KoResult] by either applying the [transform]
 * function if this result [is ok][KoResult.isOk], or returning [this].
 *
 * - Elm: [Result.andThen](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#andThen)
 * - Rust: [Result.and_then](https://doc.rust-lang.org/std/result/enum.Result.html#method.and_then)
 */
public inline infix fun <V, E, U> KoResult<V, E>.andThen(transform: (V) -> KoResult<U, E>): KoResult<U, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> transform(value)
        else -> coerceValueType()
    }
}
