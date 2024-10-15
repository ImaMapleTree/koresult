package com.github.michaelbull.result

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Calls the specified function [block] with [BindingScope] as its receiver and returns its
 * [KoResult].
 *
 * When inside a binding [block], the [bind][BindingScope.bind] function is accessible on any
 * [KoResult]. Calling the [bind][BindingScope.bind] function will attempt to unwrap the [KoResult]
 * and locally return its [value][KoResult.value].
 *
 * If a [bind][BindingScope.bind] returns an error, the [block] will terminate immediately.
 *
 * Example:
 * ```
 * fun provideX(): Result<Int, ExampleErr> { ... }
 * fun provideY(): Result<Int, ExampleErr> { ... }
 *
 * val result: Result<Int, ExampleErr> = binding {
 *   val x = provideX().bind()
 *   val y = provideY().bind()
 *   x + y
 * }
 * ```
 */
public inline fun <V, E> binding(crossinline block: BindingScope<E>.() -> V): KoResult<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return with(BindingScopeImpl<E>()) {
        try {
            Ok(block())
        } catch (_: BindException) {
            result!!
        }
    }
}

internal expect object BindException : Exception

public interface BindingScope<E> {
    public fun <V> KoResult<V, E>.bind(): V
}

@PublishedApi
internal class BindingScopeImpl<E> : BindingScope<E> {

    var result: KoErr<E>? = null

    override fun <V> KoResult<V, E>.bind(): V {
        return if (isOk) {
            value
        } else {
            result = coerceValueType()
            throw BindException
        }
    }
}
