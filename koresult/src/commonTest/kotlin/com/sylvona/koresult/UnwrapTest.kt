package com.sylvona.koresult

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UnwrapTest {

    class Unwrap {

        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = 5000,
                actual = Ok(5000).unwrap(),
            )
        }

        @Test
        fun throwsExceptionIfErr() {
            assertFailsWith<UnwrapException> {
                Err(5000).unwrap()
            }
        }
    }

    class Expect {

        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = 1994,
                actual = Ok(1994).expect { "the year should be" },
            )
        }

        @Test
        fun throwsExceptionIfErr() {
            val message = object {
                override fun toString() = "the year should be"
            }

            assertFailsWith<UnwrapException> {
                Err(1994).expect { message }
            }
        }
    }

    class UnwrapError {

        @Test
        fun throwsExceptionIfOk() {
            assertFailsWith<UnwrapException> {
                Ok("example").unwrapError()
            }
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = "example",
                actual = Err("example").unwrapError(),
            )
        }
    }

    class ExpectError {

        @Test
        fun throwsExceptionIfOk() {
            val message = object {
                override fun toString() = "the year should be"
            }

            assertFailsWith<UnwrapException> {
                Ok(2010).expectError { message }
            }
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = 2010,
                actual = Err(2010).expectError { "the year should be" },
            )
        }
    }
}
