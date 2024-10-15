package com.sylvona.koresult

import com.sylvona.koresult.coroutines.coroutineBinding
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class CoroutineBindingBenchmark {

    @Benchmark
    fun nonSuspendableBinding(blackhole: Blackhole) {
        blackhole.consume(nonSuspend().get())
    }

    @Benchmark
    fun suspendableBinding(blackhole: Blackhole) {
        runBlocking {
            blackhole.consume(withSuspend().get())
        }
    }

    @Benchmark
    fun asyncSuspendableBinding(blackhole: Blackhole) {
        runBlocking {
            blackhole.consume(withAsyncSuspend().get())
        }
    }

    private object Error

    private val time = 100L

    private fun nonSuspend(): KoResult<Int, Error> = binding {
        val x = provideXBlocking().bind()
        val y = provideYBlocking().bind()
        x + y
    }

    private suspend fun withSuspend(): KoResult<Int, Error> = coroutineBinding {
        val x = provideX().bind()
        val y = provideY().bind()
        x + y
    }

    private suspend fun withAsyncSuspend(): KoResult<Int, Error> = coroutineBinding {
        val x = async { provideX().bind() }
        val y = async { provideY().bind() }
        x.await() + y.await()
    }

    private fun provideXBlocking(): KoResult<Int, Error> {
        Thread.sleep(time)
        return Ok(1)
    }

    private fun provideYBlocking(): KoResult<Int, Error> {
        Thread.sleep(time)
        return Ok(2)
    }

    private suspend fun provideX(): KoResult<Int, Error> {
        delay(time)
        return Ok(1)
    }

    private suspend fun provideY(): KoResult<Int, Error> {
        delay(time)
        return Ok(2)
    }
}
