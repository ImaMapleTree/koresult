package com.sylvona.koresult

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns the [value][KoResult.value] if this result [is ok][KoResult.isOk], otherwise `null`.
 *
 * - Elm: [Result.toMaybe](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#toMaybe)
 * - Rust: [Result.ok](https://doc.rust-lang.org/std/result/enum.Result.html#method.ok)
 */
public fun <V, E> KoResult<V, E>.get(): V? {
    return when {
        isOk -> value
        else -> null
    }
}

/**
 * Returns the [error][KoResult.error] if this result [is an error][KoResult.isErr], otherwise `null`.
 *
 * - Rust: [Result.err](https://doc.rust-lang.org/std/result/enum.Result.html#method.err)
 */
public fun <V, E> KoResult<V, E>.getError(): E? {
    return when {
        isErr -> error
        else -> null
    }
}

/**
 * Returns the [value][KoResult.value] if this result [is ok][KoResult.isOk], otherwise [default].
 *
 * - Elm: [Result.withDefault](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#withDefault)
 * - Haskell: [Result.fromLeft](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:fromLeft)
 * - Rust: [Result.unwrap_or](https://doc.rust-lang.org/std/result/enum.Result.html#method.unwrap_or)
 *
 * @param default The value to return if [Err].
 * @return The [value][KoResult.value] if [Ok], otherwise [default].
 */
public infix fun <V, E> KoResult<V, E>.getOr(default: V): V {
    return when {
        isOk -> value
        else -> default
    }
}

/**
 * Returns the [error][KoResult.error] if this result [is an error][KoResult.isErr], otherwise
 * [default].
 *
 * - Haskell: [Result.fromRight](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:fromRight)
 *
 * @param default The error to return if [Ok].
 * @return The [error][KoResult.error] if [Err], otherwise [default].
 */
public infix fun <V, E> KoResult<V, E>.getErrorOr(default: E): E {
    return when {
        isOk -> default
        else -> error
    }
}

/**
 * Returns the [value][KoResult.value] if this result [is ok][KoResult.isOk], otherwise the
 * [transformation][transform] of the [error][KoResult.error].
 *
 * - Elm: [Result.extract](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#extract)
 * - Rust: [Result.unwrap_or_else](https://doc.rust-lang.org/src/core/result.rs.html#735-740)
 */
public inline infix fun <V, E> KoResult<V, E>.getOrElse(transform: (E) -> V): V {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> value
        else -> transform(error)
    }
}

/**
 * Returns the [error][KoResult.error] if this result [is an error][KoResult.isErr], otherwise the
 * [transformation][transform] of the [value][KoResult.value].
 */
public inline infix fun <V, E> KoResult<V, E>.getErrorOrElse(transform: (V) -> E): E {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr -> error
        else -> transform(value)
    }
}

/**
 * Returns the [value][KoResult.value] if this result [is ok][KoResult.isOk], otherwise throws the
 * [error][KoResult.error].
 *
 * This is functionally equivalent to [`getOrElse { throw it }`][getOrElse].
 */
public fun <V, E : Throwable> KoResult<V, E>.getOrThrow(): V {
    return when {
        isOk -> value
        else -> throw error
    }
}

/**
 * Returns the [value][KoResult.value] if this result [is ok][KoResult.isOk], otherwise throws the
 * [transformation][transform] of the [error][KoResult.error] to a [Throwable].
 */
public inline infix fun <V, E> KoResult<V, E>.getOrThrow(transform: (E) -> Throwable): V {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> value
        else -> throw transform(error)
    }
}

/**
 * Merges this [Result<V, E>][KoResult] to [U], returning the [value][KoResult.value] if this result
 * [is ok][KoResult.isOk], otherwise the [error][KoResult.error].
 *
 * - Scala: [MergeableEither.merge](https://www.scala-lang.org/api/2.12.0/scala/util/Either$$MergeableEither.html#merge:A)
 */
public fun <V : U, E : U, U> KoResult<V, E>.merge(): U {
    return when {
        isOk -> value
        else -> error
    }
}
