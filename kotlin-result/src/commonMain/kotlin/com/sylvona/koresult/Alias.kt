package com.sylvona.koresult

public typealias KoOk<V> = KoResult<V, Nothing>

public typealias KoErr<E> = KoResult<Nothing, E>
