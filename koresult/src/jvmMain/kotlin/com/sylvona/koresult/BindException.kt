package com.sylvona.koresult

internal actual object BindException : Exception() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}
