package com.wolpl.clikttestkit

import com.github.ajalt.clikt.core.*
import com.github.ajalt.mordant.terminal.Terminal
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@DslMarker
annotation class CliktTestDsl

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@CliktTestDsl
suspend fun CliktCommand.test(
    argv: List<String>,
    expectedExitCode: Int = 0,
    environmentVariables: Map<String, String> = emptyMap(),
    testCode: suspend CliTestScope.() -> Unit
) {
    coroutineScope {
        newSingleThreadContext("clikt-testkit-cli-context").use { cliContext ->

            val testTerminalImpl = TestTerminalInterfaceImpl()
            val testScope = MutableCliTestScope(testTerminalImpl)

            launch(cliContext) {
                try {
                    this@test
                        .context {
                            this.terminal = Terminal(terminalInterface = testTerminalImpl)
                            readEnvvar = environmentVariables::get
                        }
                        .parse(argv)
                    testTerminalImpl.terminate(0)
                } catch (programResult: ProgramResult) {
                    testTerminalImpl.terminate(programResult.statusCode)
                } catch (e: CliktTestCancellation) {
                    throw e.cause
                }
            }

            try {
                testScope.testCode()
            } catch (_: CancellationException) {
                // Command terminated, so a CancellationException was thrown in TestScope.ignoreOutputs()
            } finally {
                testTerminalImpl.cancel()
            }
            testTerminalImpl.exitCode.filterNotNull().first() shouldBe expectedExitCode
        }
    }
}

@CliktTestDsl
suspend fun CliktCommand.test(
    vararg argv: String,
    expectedExitCode: Int = 0,
    environmentVariables: Map<String, String> = emptyMap(),
    testCode: suspend CliTestScope.() -> Unit
) = test(argv.toList(), expectedExitCode, environmentVariables, testCode)