package com.github.michaelbull.result

public inline infix fun <V> KoResult<V, *>.ifOkThen(transform: KoOk<V>.() -> V): V {
    return when {
        isOk -> transform(coerceErrorType())
        else -> value
    }
}

public inline infix fun <V, E> KoResult<V, E>.ifErrorThen(transform: KoErr<E>.() -> V): V {
    return when {
        isErr -> transform(coerceValueType())
        else -> value
    }
}

