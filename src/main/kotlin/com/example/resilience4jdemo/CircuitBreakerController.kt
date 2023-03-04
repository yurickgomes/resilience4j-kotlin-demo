package com.example.resilience4jdemo

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.kotlin.circuitbreaker.circuitBreaker
import io.github.resilience4j.kotlin.circuitbreaker.decorateSuspendFunction
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CircuitBreakerController(
    private val brokenService: BrokenService,
    private val fallbackService: FallbackService,
    circuitBreakerRegistry: CircuitBreakerRegistry,
) {
    private val circuitBreaker: CircuitBreaker = circuitBreakerRegistry.circuitBreaker("demo")

    @GetMapping("/execute")
    suspend fun execute(): String {
        return circuitBreaker.executeSuspendFunction {
            brokenService.doSomething()
        }
    }

    @GetMapping("/execute-with-fallback")
    suspend fun executeWithFallback(): String {
        // using recently created custom decorator
        return circuitBreaker.executeSuspendFunctionWithFallback(
            block = {
                brokenService.doSomething()
            },
            fallback = {
                fallbackService.doSomething()
            }
        )
    }

    @GetMapping("/decorate-and-execute")
    suspend fun decorateAndExecute(): String {
        val decorated = circuitBreaker.decorateSuspendFunction {
            brokenService.doSomething()
        }

        return decorated()
    }

    @GetMapping("/decorate-and-execute-with-fallback")
    suspend fun decorateAndExecuteWithFallback(): String {
        // using recently created custom decorator
        val fallbackDecorated = circuitBreaker.decorateSuspendFunctionWithFallback(
            block = {
                brokenService.doSomething()
            },
            fallback = {
                fallbackService.doSomething()
            }
        )

        return fallbackDecorated()
    }

    @GetMapping("/execute-flow-with-fallback")
    fun executeFlowWithFallback(): Flow<String> {
        return flow { emit(brokenService.doSomething()) }
            .circuitBreaker(circuitBreaker)
            .catch { e ->
                if (e !is CallNotPermittedException) {
                    throw e
                }
                emit(fallbackService.doSomething())
            }
    }

    @ExceptionHandler(CallNotPermittedException::class)
    fun handleCallNotPermittedException(e: CallNotPermittedException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(e.message)
    }
}
