package com.github.michaelbull.result

import kotlin.test.Test
import kotlin.test.assertEquals

class BindingTest {

    object BindingError

    @Test
    fun returnsOkIfAllBindsSuccessful() {
        fun provideX(): KoResult<Int, BindingError> = Ok(1)
        fun provideY(): KoResult<Int, BindingError> = Ok(2)

        val result = binding {
            val x = provideX().bind()
            val y = provideY().bind()
            x + y
        }

        assertEquals(
            expected = Ok(3),
            actual = result,
        )
    }

    @Test
    fun returnsOkIfAllBindsOfDifferentTypeAreSuccessful() {
        fun provideX(): KoResult<String, BindingError> = Ok("1")
        fun provideY(x: Int): KoResult<Int, BindingError> = Ok(x + 2)

        val result = binding {
            val x = provideX().bind()
            val y = provideY(x.toInt()).bind()
            y
        }

        assertEquals(
            expected = Ok(3),
            actual = result,
        )
    }

    @Test
    fun returnsFirstErrIfBindingFailed() {
        fun provideX(): KoResult<Int, BindingError> = Ok(1)
        fun provideY(): KoResult<Int, BindingError> = Err(BindingError)
        fun provideZ(): KoResult<Int, BindingError> = Ok(2)

        val result = binding {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y + z
        }

        assertEquals(
            expected = Err(BindingError),
            actual = result,
        )
    }

    @Test
    fun returnsFirstErrIfBindingsOfDifferentTypesFailed() {
        fun provideX(): KoResult<Int, BindingError> = Ok(1)
        fun provideY(): KoResult<String, BindingError> = Err(BindingError)
        fun provideZ(): KoResult<Int, BindingError> = Ok(2)

        val result: KoResult<Int, BindingError> = binding {
            val x = provideX().bind()
            val y = provideY().bind()
            val z = provideZ().bind()
            x + y.toInt() + z
        }

        assertEquals(
            expected = Err(BindingError),
            actual = result,
        )
    }

    @Test
    fun runCatchingInsideBindingDoesNotSwallow() {
        fun squareNumber(): KoResult<Int, BindingError> = throw RuntimeException()

        val squaredNumbers = binding<List<Int>, BindingError> {
            val result: KoResult<List<Int>, Throwable> = runCatching {
                (0..<10).map { number ->
                    squareNumber().bind()
                }
            }

            result.mapError { BindingError }.bind()
        }

        assertEquals(
            expected = Err(BindingError),
            actual = squaredNumbers,
        )
    }
}
