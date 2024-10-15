package com.sylvona.koresult

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MapTest {
    private sealed interface MapErr {
        val reason: String

        data object HelloError : MapErr {
            override val reason = "hello"
        }

        data object WorldError : MapErr {
            override val reason = "world"
        }

        data class CustomError(override val reason: String) : MapErr
    }

    class Map {

        @Test
        fun returnsTransformedValueIfOk() {
            val value: KoResult<Int, MapErr> = Ok(10)

            assertEquals(
                expected = Ok(30),
                actual = value.map { it + 20 },
            )
        }

        @Test
        fun returnsErrorIfErr() {
            val value: KoResult<Int, MapErr> = Err(MapErr.HelloError)

            assertEquals(
                expected = Err(MapErr.HelloError),
                actual = value.map { "hello $it" },
            )
        }
    }

    class MapCatching {

        private object MapException : Throwable()

        @Test
        fun returnsTransformedValueIfOk() {
            val value: KoResult<Int, Throwable> = Ok(10)

            assertEquals(
                expected = Ok(30),
                actual = value.mapCatching { it + 20 },
            )
        }

        @Test
        fun returnsErrIfTransformationThrows() {
            val value: KoResult<Int, Throwable> = Ok(10)

            assertEquals(
                expected = Err(MapException),
                actual = value.mapCatching { throw MapException },
            )
        }

        @Test
        fun returnsErrorIfErr() {
            val value: KoResult<Int, Throwable> = Err(MapException)

            assertEquals(
                expected = Err(MapException),
                actual = value.mapCatching { "hello $it" },
            )
        }
    }

    class Transpose {

        @Test
        fun returnsNullIfValueIsNull() {
            val result = Ok(null)

            assertNull(result.transpose())
        }

        @Test
        fun returnsOkIfValueIsNotNull() {
            val result = Ok("non null")

            assertEquals(
                expected = Ok("non null"),
                actual = result.transpose()
            )
        }

        @Test
        fun returnsErrIfErr() {
            val result = Err("non null error")

            assertEquals(
                expected = Err("non null error"),
                actual = result.transpose()
            )
        }
    }

    class Flatten {

        @Test
        fun returnsFlattenedValueIfOk() {
            val result = Ok(Ok("hello"))

            assertEquals(
                expected = Ok("hello"),
                actual = result.flatten(),
            )
        }

        @Test
        fun returnsFlattenedErrIfErr() {
            val result = Ok(Err(6))

            assertEquals(
                expected = Err(6),
                actual = result.flatten(),
            )
        }

        @Test
        fun returnsErrIfFlatErr() {
            val result = Err(6)

            assertEquals(
                expected = Err(6),
                actual = result.flatten(),
            )
        }

        @Test
        fun returnsFlattenNestedResult() {
            val result = Ok(Ok(Ok("hello")))

            assertEquals(
                expected = Ok(Ok("hello")),
                actual = result.flatten(),
            )

            assertEquals(
                expected = Ok("hello"),
                actual = result.flatten().flatten(),
            )
        }
    }

    class MapError {

        @Test
        fun returnsValueIfOk() {
            val value: KoResult<Int, MapErr> = Ok(70)

            assertEquals(
                expected = Ok(70),
                actual = value.mapError { MapErr.WorldError },
            )
        }

        @Test
        fun returnsErrorIfErr() {
            val value: KoResult<Int, MapErr> = Err(MapErr.HelloError)

            assertEquals(
                expected = Err(MapErr.WorldError),
                actual = value.mapError { MapErr.WorldError },
            )
        }
    }

    class MapOr {

        @Test
        fun returnsTransformedValueIfOk() {
            val value: KoResult<String, String> = Ok("foo")

            assertEquals(
                expected = 3,
                actual = value.mapOr(42, String::length),
            )
        }

        @Test
        fun returnsDefaultValueIfErr() {
            val value: KoResult<String, String> = Err("foo")

            assertEquals(
                expected = 42,
                actual = value.mapOr(42, String::length),
            )
        }
    }

    class MapOrElse {
        private val k = 21

        @Test
        fun returnsTransformedValueIfOk() {
            val value: KoResult<String, String> = Ok("foo")

            assertEquals(
                expected = 3,
                actual = value.mapOrElse({ k * 2 }, String::length),
            )
        }

        @Test
        fun returnsDefaultValueIfErr() {
            val value: KoResult<String, String> = Err("foo")

            assertEquals(
                expected = 42,
                actual = value.mapOrElse({ k * 2 }, String::length),
            )
        }
    }

    class MapBoth {

        @Test
        fun returnsTransformedValueIfOk() {
            val value: KoResult<Int, Long> = Ok(50)

            val result: String = value.mapBoth(
                success = { "good $it" },
                failure = { "bad $it" },
            )

            assertEquals(
                expected = "good 50",
                actual = result,
            )
        }

        @Test
        fun returnsTransformedErrorIfErr() {
            val value: KoResult<Int, Long> = Err(20)

            val result: String = value.mapBoth(
                success = { "good $it" },
                failure = { "bad $it" },
            )

            assertEquals(
                expected = "bad 20",
                actual = result,
            )
        }
    }

    class FlatMapBoth {

        @Test
        fun returnsTransformedValueIfOk() {
            val value: KoResult<Int, Long> = Ok(50)

            val result: KoResult<String, Long> = value.flatMapBoth(
                success = { Ok("good $it") },
                failure = { Err(100L) },
            )

            assertEquals(
                expected = Ok("good 50"),
                actual = result,
            )
        }

        @Test
        fun returnsTransformedErrorIfErr() {
            val result: KoResult<Int, Long> = Err(25L)

            val value: KoResult<String, Long> = result.flatMapBoth(
                success = { Ok("good $it") },
                failure = { Err(100L) },
            )

            assertEquals(
                expected = Err(100L),
                actual = value,
            )
        }
    }

    class MapEither {

        @Test
        fun returnsTransformedValueIfOk() {
            val value: KoResult<Int, MapErr.HelloError> = Ok(500)

            val result: KoResult<Long, MapErr.CustomError> = value.mapEither(
                success = { it + 500L },
                failure = { MapErr.CustomError("$it") },
            )

            assertEquals(
                expected = Ok(1000L),
                actual = result,
            )
        }

        @Test
        fun returnsTransformedErrorIfErr() {
            val value: KoResult<Int, MapErr.HelloError> = Err(MapErr.HelloError)

            val result: KoResult<Long, MapErr.CustomError> = value.mapEither(
                success = { it + 500L },
                failure = { MapErr.CustomError("bad") },
            )

            assertEquals(
                expected = Err(MapErr.CustomError("bad")),
                actual = result,
            )
        }
    }

    class FlatMapEither {

        @Test
        fun returnsTransformedValueIfOk() {
            val value: KoResult<Int, MapErr.HelloError> = Ok(500)

            val result: KoResult<Long, MapErr.CustomError> = value.flatMapEither(
                success = { Ok(it + 500L) },
                failure = { Err(MapErr.CustomError("$it")) },
            )

            assertEquals(
                expected = Ok(1000L),
                actual = result,
            )
        }

        @Test
        fun returnsTransformedErrorIfErr() {
            val value: KoResult<Int, MapErr.HelloError> = Err(MapErr.HelloError)

            val result: KoResult<Long, MapErr.CustomError> = value.flatMapEither(
                success = { Ok(it + 500L) },
                failure = { Err(MapErr.CustomError("bad")) },
            )

            assertEquals(
                expected = Err(MapErr.CustomError("bad")),
                actual = result,
            )
        }
    }

    class ToErrorIfNull {

        @Test
        fun returnsValueIfOk() {
            assertEquals(
                expected = Ok("a"),
                actual = Ok("a").toErrorIfNull { "b" },
            )
        }

        @Test
        fun returnsTransformedErrorIfNull() {
            assertEquals(
                expected = Err("a"),
                actual = Ok(null).toErrorIfNull { "a" },
            )
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = Err("a"),
                actual = Err("a").toErrorIfNull { "b" },
            )
        }
    }

    class ToErrorUnlessNull {

        @Test
        fun returnsTransformedErrorIfNotNull() {
            assertEquals(
                expected = Err("b"),
                actual = Ok("a").toErrorUnlessNull { "b" },
            )
        }

        @Test
        fun returnsValueIfNull() {
            assertEquals(
                expected = Ok(null),
                actual = Ok(null).toErrorUnlessNull { "a" },
            )
        }

        @Test
        fun returnsErrorIfErr() {
            assertEquals(
                expected = Err("b"),
                actual = Err("a").toErrorUnlessNull { "b" },
            )
        }
    }
}
