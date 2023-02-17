package com.wolpl.clikttestkit

sealed interface CliEvent {
    data class StandardOutput(val output: String) : CliEvent
    data class ErrorOutput(val output: String) : CliEvent
    data class Termination(val exitCode: Int) : CliEvent
}