package com.github.michaelbull.result

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class BindingBenchmark {

    @Benchmark
    fun bindingSuccess(blackhole: Blackhole) {
        val result: KoResult<Int, Error> = binding {
            val x = provideX().bind()
            val y = provideY().bind()
            x + y
        }

        blackhole.consume(result)
    }

    @Benchmark
    fun bindingFailure(blackhole: Blackhole) {
        val result: KoResult<Int, Error> = binding {
            val x = provideX().bind()
            val z = provideZ().bind()
            x + z
        }

        blackhole.consume(result)
    }

    @Benchmark
    fun andThenSuccess(blackhole: Blackhole) {
        val result = provideX().andThen { x ->
            provideY().andThen { y ->
                Ok(x + y)
            }
        }

        blackhole.consume(result)
    }

    @Benchmark
    fun andThenFailure(blackhole: Blackhole) {
        val result = provideX().andThen { x ->
            provideZ().andThen { z ->
                Ok(x + z)
            }
        }

        blackhole.consume(result)
    }

    private object Error

    private fun provideX(): KoResult<Int, Error> = Ok(1)
    private fun provideY(): KoResult<Int, Error> = Ok(2)
    private fun provideZ(): KoResult<Int, Error> = Err(Error)
}
