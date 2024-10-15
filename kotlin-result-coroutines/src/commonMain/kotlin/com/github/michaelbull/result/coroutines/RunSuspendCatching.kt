package com.github.michaelbull.result.coroutines

import com.github.michaelbull.result.KoResult
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.throwIf
import kotlinx.coroutines.CancellationException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was
 * successful, catching any [Throwable] exception that was thrown from the [block] function
 * execution and encapsulating it as a failure. If the encapsulated failure is a
 * [CancellationException], the exception is thrown to indicate  _normal_ cancellation of a
 * coroutine.
 */
public inline fun <V> runSuspendCatching(block: () -> V): KoResult<V, Throwable> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return runCatching(block).throwIf {
        it is CancellationException
    }
}

/**
 * Calls the specified function [block] with [this] value as its receiver and returns its
 * encapsulated result if invocation was successful, catching any [Throwable] exception that was
 * thrown from the [block] function execution and encapsulating it as a failure. If the
 * encapsulated failure is a [CancellationException], the exception is thrown to indicate  _normal_
 * cancellation of a coroutine.
 */
public inline infix fun <T, V> T.runSuspendCatching(block: T.() -> V): KoResult<V, Throwable> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return runCatching(block).throwIf {
        it is CancellationException
    }
}
