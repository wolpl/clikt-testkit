package com.wolpl.clikttestkit

class MutableCliTestScope(
    private val testTerminalInterfaceImpl: TestTerminalInterfaceImpl
) : CliTestScope {

    override suspend fun expectOutput(): String {
        return when (val event = testTerminalInterfaceImpl.receiveEvent()) {
            is CliEvent.ErrorOutput -> throw AssertionError("Expected standard output, but CliktCommand provided error output \"${event.output}\" instead.")
            is CliEvent.StandardOutput -> event.output
            is CliEvent.Termination -> throw AssertionError("Expected standard output, but CliktCommand terminated with code ${event.exitCode} instead.")
        }
    }

    override suspend fun expectErrorOutput(): String {
        return when (val event = testTerminalInterfaceImpl.receiveEvent()) {
            is CliEvent.ErrorOutput -> event.output
            is CliEvent.StandardOutput -> throw AssertionError("Expected error output, but CliktCommand provided standard output \"${event.output}\" instead.")
            is CliEvent.Termination -> throw AssertionError("Expected error output, but CliktCommand terminated with code ${event.exitCode} instead.")
        }
    }

    override suspend fun provideInput(line: String) {
        testTerminalInterfaceImpl.answerPrompt(line)
    }

    override suspend fun ignoreOutputs(): Nothing {
        testTerminalInterfaceImpl.cancelPromptsChannel()
        while (true) {
            testTerminalInterfaceImpl.receiveEvent()
        }
    }
}