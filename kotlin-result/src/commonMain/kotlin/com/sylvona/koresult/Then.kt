package com.sylvona.koresult

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns the [value][KoResult.value] if this result [is ok][KoResult.isOk], otherwise returns the
 *  [transformation][transform] of the coerced [error result][KoResult].
 *
 *  This method is particularly useful when cascading an [error][KoErr] between layers.
 *  Example:
 * ```
 * fun providerX(): KoResult<Int, ExampleErr> { ... }
 *
 * fun layer(): KoResult<Int, ExampleErr> {
 *   val x = providerX().ifErrorThen {
 *      return this  // returns `KoErr`
 *   }
 * }
 * ```
 *
 *  @param transform the transform to apply if the result is [KoErr]
 *  @return The [value][KoResult.value] if [KoOk], otherwise the result of the [transformation][transform]
 */
public inline infix fun <V, E> KoResult<V, E>.ifErrorThen(transform: KoErr<E>.() -> V): V {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isErr -> transform(coerceValueType())
        else -> value
    }
}

