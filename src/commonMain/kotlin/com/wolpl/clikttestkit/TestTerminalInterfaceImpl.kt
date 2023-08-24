package com.wolpl.clikttestkit

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.PrintRequest
import com.github.ajalt.mordant.terminal.TerminalInfo
import com.github.ajalt.mordant.terminal.TerminalInterface
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class TestTerminalInterfaceImpl : TerminalInterface {

    override val info = TerminalInfo(
            width = 0,
            height = 0,
            ansiLevel = AnsiLevel.NONE,
            ansiHyperLinks = false,
            outputInteractive = false,
            inputInteractive = false,
            crClearsLine = false
    )

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

    override fun completePrintRequest(request: PrintRequest) {
        runBlocking {
            if (request.stderr) {
                try {
                    eventsChannel.send(CliEvent.ErrorOutput(request.text.trimEnd('\n')))
                } catch (e: CancellationException) {
                    throw CliktTestCancellation(AssertionError("Test was finished, but CliktCommand tried to create error output: ${request.text}"))
                }
            } else {
                try {
                    eventsChannel.send(CliEvent.StandardOutput(request.text.trimEnd('\n')))
                } catch (e: CancellationException) {
                    throw CliktTestCancellation(AssertionError("Test was finished, but CliktCommand tried to create standard output: ${request.text}"))
                }
            }
        }
    }

    override fun readLineOrNull(hideInput: Boolean): String? {
        return runBlocking {
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