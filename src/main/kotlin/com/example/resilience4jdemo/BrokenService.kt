package com.example.resilience4jdemo

import kotlinx.coroutines.delay
import org.springframework.stereotype.Service

@Service
class BrokenService {
    suspend fun doSomething(): String {
        delay(500L)

        throw RuntimeException("foo")
    }
}
