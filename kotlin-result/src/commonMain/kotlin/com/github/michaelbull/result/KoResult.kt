package com.github.michaelbull.result

/**
 * Returns a [KoResult] that [is ok][KoResult.isOk] and contains a [value][KoResult.value].
 */
@Suppress("FunctionName")
public fun <V> Ok(value: V): KoOk<V> {
    return KoResult.ok(value)
}

/**
 * Returns a [KoResult] that [is an error][KoResult.isErr] and contains an [error][KoResult.error].
 */
@Suppress("FunctionName")
public fun <E> Err(error: E): KoErr<E> {
    return KoResult.err(error)
}

/**
 * [KoResult] is a type that represents either success ([Ok]) or failure ([Err]).
 *
 * A [KoResult] that [is ok][KoResult.isOk] will have a [value][KoResult.value] of type [V], whereas a
 * [KoResult] that [is an error][KoResult.isErr] will have an [error][KoResult.error] of type [E].
 *
 * - Elm: [Result](http://package.elm-lang.org/packages/elm-lang/core/5.1.1/Result)
 * - Haskell: [Data.Either](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html)
 * - Rust: [Result](https://doc.rust-lang.org/std/result/enum.Result.html)
 */
public class KoResult<out V, out E> internal constructor(
    private val inlineValue: Any?,
    public val isOk: Boolean
) {
    @Suppress("UNCHECKED_CAST")
    public val value: V
        get() = inlineValue as V

    @Suppress("UNCHECKED_CAST")
    public val error: E
        get() = inlineValue as E

    public val isErr: Boolean = !isOk

    public companion object {
        /**
         * Returns a [KoResult] that [is ok][KoResult.isOk] and contains a [value][KoResult.value].
         */
        public fun <V> ok(value: V): KoOk<V> = KoResult(value, true)

        /**
         * Returns a [KoResult] that [is an error][KoResult.isErr] and contains an [error][KoResult.error].
         */
        public fun <E> err(error: E): KoErr<E> = KoResult(error, false)
    }

    @Suppress("UNCHECKED_CAST")
    public operator fun component1(): V? {
        return when {
            isOk -> inlineValue as V?
            else -> null
        }
    }

    @Suppress("UNCHECKED_CAST")
    public operator fun component2(): E? {
        return when {
            isErr -> inlineValue as E?
            else -> null
        }
    }

    @Suppress("UNCHECKED_CAST")
    public inline fun <C> coerceValueType() : KoResult<C, E> {
        return this as KoResult<C, E>
    }

    @Suppress("UNCHECKED_CAST")
    public inline fun <C> coerceErrorType(): KoResult<V, C> {
        return this as KoResult<V, C>
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is KoResult<*, *> || inlineValue == null && other.inlineValue != null) return false
        return isOk == other.isOk && inlineValue == other.inlineValue
    }

    override fun hashCode(): Int {
        return inlineValue.hashCode()
    }

    override fun toString(): String {
        return when {
            isOk -> "Ok($value)"
            else -> "Err($error)"
        }
    }
}
