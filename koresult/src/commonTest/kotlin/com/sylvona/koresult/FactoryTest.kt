package com.sylvona.koresult

import kotlin.test.Test
import kotlin.test.assertEquals

class FactoryTest {

    class RunCatching {

        @Test
        fun returnsOkIfInvocationSuccessful() {
            assertEquals(
                expected = com.sylvona.koresult.Ok("example"),
                actual = runCatching { "example" },
            )
        }

        @Test
        @Suppress("UNREACHABLE_CODE")
        fun returnsErrIfInvocationFails() {
            val exception = IllegalArgumentException("throw me")

            assertEquals(
                expected = com.sylvona.koresult.Err(exception),
                actual = runCatching { throw exception },
            )
        }
    }

    class ToResultOr {

        @Test
        fun returnsOkfIfNonNull() {
            assertEquals(
                expected = "ok",
                actual = "ok".toResultOr { "err" }.get()
            )
        }

        @Test
        fun returnsErrIfNull() {
            assertEquals(
                expected = com.sylvona.koresult.Err("err"),
                actual = "ok".toLongOrNull().toResultOr { "err" }
            )
        }
    }
}
