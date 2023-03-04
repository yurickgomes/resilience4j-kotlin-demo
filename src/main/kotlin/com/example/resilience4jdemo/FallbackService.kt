package com.example.resilience4jdemo

import kotlinx.coroutines.delay
import org.springframework.stereotype.Service

@Service
class FallbackService {
    suspend fun doSomething(): String {
        delay(500L)
        return "Hello from fallback service"
    }
}
