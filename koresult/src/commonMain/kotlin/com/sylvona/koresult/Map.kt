package com.sylvona.koresult

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Maps this [Result<V, E>][KoResult] to [Result<U, E>][KoResult] by either applying the [transform]
 * function to the [value][KoResult.value] if this result [is ok][KoResult.isOk], or returning [this].
 *
 * - Elm: [Result.map](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map)
 * - Haskell: [Data.Bifunctor.first](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:first)
 * - Rust: [Result.map](https://doc.rust-lang.org/std/result/enum.Result.html#method.map)
 */
public inline infix fun <V, E, U> KoResult<V, E>.map(transform: (V) -> U): KoResult<U, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> Ok(transform(value))
        else -> coerceValueType()
    }
}

/**
 * Maps this [Result<V, Throwable>][KoResult] to [Result<U, Throwable>][KoResult] by either applying
 * the [transform] function to the [value][KoResult.value] if this result [is ok][KoResult.isOk], or
 * returning [this].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates
 * it as an [Err].
 *
 * - Elm: [Result.map](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#map)
 * - Haskell: [Data.Bifunctor.first](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:first)
 * - Rust: [Result.map](https://doc.rust-lang.org/std/result/enum.Result.html#method.map)
 */
public inline infix fun <V, U> KoResult<V, Throwable>.mapCatching(transform: (V) -> U): KoResult<U, Throwable> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> runCatching { transform(value) }
        else -> coerceValueType()
    }
}

/**
 * Transposes this [Result<V?, E>][KoResult] to [Result<V, E>][KoResult].
 *
 * Returns null if this [KoResult] is [Ok] and the [value][Ok.value] is `null`, otherwise this [KoResult].
 *
 * - Rust: [Result.transpose][https://doc.rust-lang.org/std/result/enum.Result.html#method.transpose]
 */
public inline fun <V, E> KoResult<V?, E>.transpose(): KoResult<V, E>? {
    return when {
        isOk && value == null -> null
        isOk && value != null -> this.coerceValueType()
        else -> coerceValueType()
    }
}

/**
 * Maps this [Result<Result<V, E>, E>][KoResult] to [Result<V, E>][KoResult].
 *
 * - Rust: [Result.flatten](https://doc.rust-lang.org/std/result/enum.Result.html#method.flatten)
 */
public fun <V, E> KoResult<KoResult<V, E>, E>.flatten(): KoResult<V, E> {
    return when {
        isOk -> value
        else -> coerceValueType()
    }
}

/**
 * Maps this [Result<V, E>][KoResult] to [Result<U, E>][KoResult] by either applying the [transform]
 * function if this result [is ok][KoResult.isOk], or returning [this].
 *
 * This is functionally equivalent to [andThen].
 *
 * - Scala: [Either.flatMap](http://www.scala-lang.org/api/2.12.0/scala/util/Either.html#flatMap[AA>:A,Y](f:B=>scala.util.Either[AA,Y]):scala.util.Either[AA,Y])
 */
public inline infix fun <V, E, U> KoResult<V, E>.flatMap(transform: (V) -> KoResult<U, E>): KoResult<U, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return andThen(transform)
}

/**
 * Maps this [Result<V, E>][KoResult] to [U] by applying either the [success] function if this
 * result [is ok][KoResult.isOk], or the [failure] function if this result
 * [is an error][KoResult.isErr].
 *
 * Unlike [mapEither], [success] and [failure] must both return [U].
 *
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
public inline fun <V, E, U> KoResult<V, E>.mapBoth(
    success: (V) -> U,
    failure: (E) -> U,
): U {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> success(value)
        else -> failure(error)
    }
}

/**
 * Maps this [Result<V, E>][KoResult] to [U] by applying either the [success] function if this
 * result [is ok][KoResult.isOk], or the [failure] function if this result
 * [is an error][KoResult.isErr].
 *
 * Unlike [mapEither], [success] and [failure] must both return [U].
 *
 * This is functionally equivalent to [mapBoth].
 *
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
public inline fun <V, E, U> KoResult<V, E>.fold(
    success: (V) -> U,
    failure: (E) -> U,
): U {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return mapBoth(success, failure)
}

/**
 * Maps this [Result<V, E>][KoResult] to [Result<U, E>][KoResult] by applying either the [success]
 * function if this result [is ok][KoResult.isOk], or the [failure] function if this result
 * [is an error][KoResult.isErr].
 *
 * Unlike [mapEither], [success] and [failure] must both return [U].
 *
 * - Elm: [Result.Extra.mapBoth](http://package.elm-lang.org/packages/elm-community/result-extra/2.2.0/Result-Extra#mapBoth)
 * - Haskell: [Data.Either.either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:either)
 */
public inline fun <V, E, U> KoResult<V, E>.flatMapBoth(
    success: (V) -> KoResult<U, E>,
    failure: (E) -> KoResult<U, E>,
): KoResult<U, E> {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> success(value)
        else -> failure(error)
    }
}

/**
 * Maps this [Result<V, E>][KoResult] to [Result<U, F>][KoResult] by applying either the [success]
 * function if this result [is ok][KoResult.isOk], or the [failure] function if this result
 * [is an error][KoResult.isErr].
 *
 * Unlike [mapBoth], [success] and [failure] may either return [U] or [F] respectively.
 *
 * - Haskell: [Data.Bifunctor.Bimap](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:bimap)
 */
public inline fun <V, E, U, F> KoResult<V, E>.mapEither(
    success: (V) -> U,
    failure: (E) -> F,
): KoResult<U, F> {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> Ok(success(value))
        else -> Err(failure(error))
    }
}

/**
 * Maps this [Result<V, E>][KoResult] to [Result<U, F>][KoResult] by applying either the [success]
 * function if this result [is ok][KoResult.isOk], or the [failure] function if this result
 * [is an error][KoResult.isErr].
 *
 * Unlike [mapBoth], [success] and [failure] may either return [U] or [F] respectively.
 *
 * - Haskell: [Data.Bifunctor.Bimap](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:bimap)
 */
public inline fun <V, E, U, F> KoResult<V, E>.flatMapEither(
    success: (V) -> KoResult<U, F>,
    failure: (E) -> KoResult<U, F>,
): KoResult<U, F> {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(failure, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> success(value)
        else -> failure(error)
    }
}

/**
 * Maps this [Result<V, E>][KoResult] to [Result<V, F>][KoResult] by either applying the [transform]
 * function to the [error][KoResult.error] if this result [is an error][KoResult.isErr], or returning
 * [this].
 *
 * - Elm: [Result.mapError](http://package.elm-lang.org/packages/elm-lang/core/latest/Result#mapError)
 * - Haskell: [Data.Bifunctor.right](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Bifunctor.html#v:second)
 * - Rust: [Result.map_err](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_err)
 */
public inline infix fun <V, E, F> KoResult<V, E>.mapError(transform: (E) -> F): KoResult<V, F> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr -> Err(transform(error))
        else -> coerceErrorType()
    }
}

/**
 * Maps this [Result<V, E>][KoResult] to [U] by either applying the [transform] function to the
 * [value][KoResult.value] if this result [is ok][KoResult.isOk], or returning the [default] if this
 * result [is an error][KoResult.isErr].
 *
 * - Rust: [Result.map_or](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_or)
 */
public inline fun <V, E, U> KoResult<V, E>.mapOr(
    default: U,
    transform: (V) -> U,
): U {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> transform(value)
        else -> default
    }
}

/**
 * Maps this [Result<V, E>][KoResult] to [U] by applying either the [transform] function if this
 * result [is ok][KoResult.isOk], or the [default] function if this result
 * [is an error][KoResult.isErr]. Both of these functions must return the same type ([U]).
 *
 * - Rust: [Result.map_or_else](https://doc.rust-lang.org/std/result/enum.Result.html#method.map_or_else)
 */
public inline fun <V, E, U> KoResult<V, E>.mapOrElse(
    default: (E) -> U,
    transform: (V) -> U,
): U {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> transform(value)
        else -> default(error)
    }
}

/**
 * Returns a [Result<List<U>, E>][KoResult] containing the results of applying the given [transform]
 * function to each element in the original collection, returning early with the first [Err] if a
 * transformation fails.
 */
public inline infix fun <V, E, U> KoResult<Iterable<V>, E>.mapAll(transform: (V) -> KoResult<U, E>): KoResult<List<U>, E> {
    return map { iterable ->
        iterable.map { element ->
            val transformed = transform(element)

            when {
                transformed.isOk -> transformed.value
                else -> return transformed.coerceValueType()
            }
        }
    }
}

/**
 * Returns the [transformation][transform] of the [value][KoResult.value] if this result
 * [is ok][KoResult.isOk] and satisfies the given [predicate], otherwise [this].
 *
 * @see [takeIf]
 */
public inline fun <V, E> KoResult<V, E>.toErrorIf(
    predicate: (V) -> Boolean,
    transform: (V) -> E,
): KoResult<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk && predicate(value) -> Err(transform(value))
        else -> this
    }
}

/**
 * Returns the supplied [error] if this result [is ok][KoResult.isOk] and the [value][KoResult.value]
 * is `null`, otherwise [this].
 *
 * @see [toErrorIf]
 */
public inline fun <V, E> KoResult<V?, E>.toErrorIfNull(error: () -> E): KoResult<V, E> {
    contract {
        callsInPlace(error, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk && value == null -> Err(error())
        isOk && value != null -> coerceValueType()
        else -> coerceValueType()
    }
}

/**
 * Returns the [transformation][transform] of the [value][KoResult.value] if this result
 * [is ok][KoResult.isOk] and _does not_ satisfy the given [predicate], otherwise [this].
 *
 * @see [takeUnless]
 */
public inline fun <V, E> KoResult<V, E>.toErrorUnless(
    predicate: (V) -> Boolean,
    transform: (V) -> E,
): KoResult<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk && !predicate(value) -> Err(transform(value))
        else -> this
    }
}

/**
 * Returns the supplied [error] unless this result [is ok][KoResult.isOk] and the
 * [value][KoResult.value] is `null`, otherwise [this].
 *
 * @see [toErrorUnless]
 */
public inline fun <V, E> KoResult<V, E>.toErrorUnlessNull(error: () -> E): KoResult<V, E> {
    contract {
        callsInPlace(error, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk && value == null -> this
        else -> Err(error())
    }
}
