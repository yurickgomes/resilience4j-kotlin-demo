package com.example.resilience4jdemo

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction

suspend fun <T> CircuitBreaker.executeSuspendFunctionWithFallback(
    block: suspend () -> T,
    fallback: suspend () -> T,
): T {
    return try {
        this.executeSuspendFunction {
            block()
        }
    } catch (e: CallNotPermittedException) {
        println("fallback -> ${e.message}")
        fallback()
    }
}

fun <T> CircuitBreaker.decorateSuspendFunctionWithFallback(
    block: suspend () -> T,
    fallback: suspend () -> T,
): suspend () -> T = {
    this.executeSuspendFunctionWithFallback(block, fallback)
}
