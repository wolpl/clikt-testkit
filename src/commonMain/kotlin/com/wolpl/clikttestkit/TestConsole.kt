package com.wolpl.clikttestkit

import com.github.ajalt.clikt.output.CliktConsole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class TestConsole : CliktConsole {
    override val lineSeparator = "\n"

    private val eventsChannel = Channel<CliEvent>()
    private val promptsChannel = Channel<String>()

    val exitCode = MutableStateFlow<Int?>(null)

    suspend fun terminate(code: Int) {
        exitCode.value = code
        try {
            eventsChannel.send(CliEvent.Termination(code))
        } finally {
            cancel()
        }
    }

    suspend fun receiveEvent() = eventsChannel.receive()

    suspend fun answerPrompt(input: String) = promptsChannel.send(input)

    override fun print(text: String, error: Boolean) {
        runBlocking {

            if (error) {
                try {
                    eventsChannel.send(CliEvent.ErrorOutput(text.trimEnd('\n')))
                } catch (e: CancellationException) {
                    throw CliktTestCancellation(AssertionError("Test was finished, but CliktCommand tried to create error output: $text"))
                }
            } else {
                try {
                    eventsChannel.send(CliEvent.StandardOutput(text.trimEnd('\n')))
                } catch (e: CancellationException) {
                    throw CliktTestCancellation(AssertionError("Test was finished, but CliktCommand tried to create standard output: $text"))
                }
            }

        }
    }

    override fun promptForLine(prompt: String, hideInput: Boolean): String {
        return runBlocking {
            this@TestConsole.print(prompt, false)
            try {
                promptsChannel.receive()
            } catch (e: CancellationException) {
                throw CliktTestCancellation(AssertionError("CliktCommand expected input, but test code did not provide any!"))
            }
        }
    }

    fun cancelPromptsChannel() {
        promptsChannel.cancel()
    }

    fun cancel() {
        eventsChannel.cancel()
        promptsChannel.cancel()
    }
}