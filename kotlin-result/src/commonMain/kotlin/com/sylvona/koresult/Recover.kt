package com.sylvona.koresult

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns the [transformation][transform] of the [error][KoResult.error] if this result
 * [is an error][KoResult.isErr], otherwise [this].
 */
public inline infix fun <V, E> KoResult<V, E>.recover(transform: (E) -> V): KoResult<V, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> this
        else -> Ok(transform(error))
    }
}

/**
 * Returns the [transformation][transform] of the [error][KoResult.error] if this result
 * [is an error][KoResult.isErr], catching and encapsulating any thrown exception as an [Err],
 * otherwise [this].
 */
public inline infix fun <V, E> KoResult<V, E>.recoverCatching(transform: (E) -> V): KoResult<V, Throwable> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> coerceErrorType()
        else -> runCatching { transform(error) }
    }
}

/**
 * Returns the [transformation][transform] of the [error][KoResult.error] if this result
 * [is an error][KoResult.isErr] and satisfies the given [predicate], otherwise [this].
 */
public inline fun <V, E> KoResult<V, E>.recoverIf(
    predicate: (E) -> Boolean,
    transform: (E) -> V,
): KoResult<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && predicate(error) -> Ok(transform(error))
        else -> this
    }
}

/**
 * Returns the [transformation][transform] of the [error][KoResult.error] if this result
 * [is an error][KoResult.isErr] and _does not_ satisfy the given [predicate], otherwise [this].
 */
public inline fun <V, E> KoResult<V, E>.recoverUnless(predicate: (E) -> Boolean, transform: (E) -> V): KoResult<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && !predicate(error) -> Ok(transform(error))
        else -> this
    }
}

/**
 * Returns the [transformation][transform] of the [error][KoResult.error] if this result
 * [is an error][KoResult.isErr], otherwise [this].
 */
public inline fun <V, E> KoResult<V, E>.andThenRecover(transform: (E) -> KoResult<V, E>): KoResult<V, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isOk -> this
        else -> transform(error)
    }
}

/**
 * Returns the [transformation][transform] of the [error][KoResult.error] if this result
 * [is an error][KoResult.isErr] and satisfies the given [predicate], otherwise [this].
 */
public inline fun <V, E> KoResult<V, E>.andThenRecoverIf(
    predicate: (E) -> Boolean,
    transform: (E) -> KoResult<V, E>,
): KoResult<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && predicate(error) -> transform(error)
        else -> this
    }
}

/**
 * Returns the [transformation][transform] of the [error][KoResult.error] if this result
 * [is an error][KoResult.isErr] and _does not_ satisfy the given [predicate], otherwise [this].
 */
public inline fun <V, E> KoResult<V, E>.andThenRecoverUnless(
    predicate: (E) -> Boolean,
    transform: (E) -> KoResult<V, E>,
): KoResult<V, E> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr && !predicate(error) -> transform(error)
        else -> this
    }
}
