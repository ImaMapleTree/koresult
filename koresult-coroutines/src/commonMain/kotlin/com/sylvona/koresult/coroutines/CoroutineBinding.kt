package com.sylvona.koresult.coroutines

import com.sylvona.koresult.KoErr
import com.sylvona.koresult.Ok
import com.sylvona.koresult.KoResult
import com.sylvona.koresult.binding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Calls the specified function [block] with [CoroutineBindingScope] as its receiver and returns
 * its [KoResult].
 *
 * When inside a binding [block], the [bind][CoroutineBindingScope.bind] function is accessible on
 * any [KoResult]. Calling the [bind][CoroutineBindingScope.bind] function will attempt to unwrap the
 * [KoResult] and locally return its [value][KoResult.value].
 *
 * Unlike [binding], this function is designed for _concurrent decomposition_ of work. When any
 * [bind][CoroutineBindingScope.bind] returns an error, the [CoroutineScope] will be
 * [cancelled][Job.cancel], cancelling all the other children.
 *
 * This function returns as soon as the given [block] and all its child coroutines are completed.
 *
 * Example:
 * ```
 * suspend fun provideX(): Result<Int, ExampleErr> { ... }
 * suspend fun provideY(): Result<Int, ExampleErr> { ... }
 *
 * val result: Result<Int, ExampleErr> = coroutineBinding {
 *   val x = async { provideX().bind() }
 *   val y = async { provideY().bind() }
 *   x.await() + y.await()
 * }
 */
public suspend inline fun <V, E> coroutineBinding(crossinline block: suspend CoroutineBindingScope<E>.() -> V): KoResult<V, E> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    lateinit var receiver: CoroutineBindingScopeImpl<E>

    return try {
        coroutineScope {
            receiver = CoroutineBindingScopeImpl(this)

            with(receiver) {
                Ok(block())
            }
        }
    } catch (ex: BindCancellationException) {
        receiver.result!!
    }
}

internal object BindCancellationException : CancellationException(null as String?)

public interface CoroutineBindingScope<E> : CoroutineScope {
    public suspend fun <V> KoResult<V, E>.bind(): V
}

@PublishedApi
internal class CoroutineBindingScopeImpl<E>(
    delegate: CoroutineScope,
) : CoroutineBindingScope<E>, CoroutineScope by delegate {

    private val mutex = Mutex()
    var result: KoErr<E>? = null

    override suspend fun <V> KoResult<V, E>.bind(): V {
        return if (isOk) {
            value
        } else {
            mutex.withLock {
                if (result == null) {
                    result = this.coerceValueType()
                    coroutineContext.cancel(BindCancellationException)
                }

                throw BindCancellationException
            }
        }
    }
}
