package com.github.michaelbull.result

public typealias KoOk<V> = KoResult<V, Nothing>

public typealias KoErr<E> = KoResult<Nothing, E>
