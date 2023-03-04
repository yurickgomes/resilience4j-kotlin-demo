package com.example.resilience4jdemo

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    private val brokenService: BrokenService,
    private val fallbackService: FallbackService,
    circuitBreakerRegistry: CircuitBreakerRegistry,
) {
    private val circuitBreaker: CircuitBreaker = circuitBreakerRegistry.circuitBreaker("demo")

    @GetMapping("/execute")
    suspend fun execute(): String {
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

    @ExceptionHandler(CallNotPermittedException::class)
    fun handleCallNotPermittedException(e: CallNotPermittedException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(e.message)
    }
}
