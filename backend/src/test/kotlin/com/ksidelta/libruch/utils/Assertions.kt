package com.ksidelta.libruch.utils

import io.kotest.assertions.timing.eventually
import kotlin.time.Duration

suspend fun <T> eventuallyConfigured(func: suspend () -> T) = eventually(Duration.parse("5s"), func)

