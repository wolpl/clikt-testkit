package com.wolpl.clikttestkit

import com.github.ajalt.clikt.command.CoreSuspendingCliktCommand
import com.github.ajalt.clikt.command.parse
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
suspend fun <T : BaseCliktCommand<T>, U : T> U.test(
    argv: List<String>,
    expectedExitCode: Int = 0,
    environmentVariables: Map<String, String> = emptyMap(),
    runCommand: suspend U.(argv: List<String>) -> Unit,
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
                        .runCommand(argv)
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
suspend fun CoreSuspendingCliktCommand.test(
    argv: List<String>,
    expectedExitCode: Int = 0,
    environmentVariables: Map<String, String> = emptyMap(),
    testCode: suspend CliTestScope.() -> Unit
) = test(
    argv,
    expectedExitCode,
    environmentVariables,
    runCommand = { parse(it) },
    testCode
)


@CliktTestDsl
suspend fun CoreSuspendingCliktCommand.test(
    vararg argv: String,
    expectedExitCode: Int = 0,
    environmentVariables: Map<String, String> = emptyMap(),
    testCode: suspend CliTestScope.() -> Unit
) = test(argv.toList(), expectedExitCode, environmentVariables, testCode)

@CliktTestDsl
suspend fun CoreCliktCommand.test(
    argv: List<String>,
    expectedExitCode: Int = 0,
    environmentVariables: Map<String, String> = emptyMap(),
    testCode: suspend CliTestScope.() -> Unit
) = test(
    argv,
    expectedExitCode,
    environmentVariables,
    runCommand = { parse(it) },
    testCode
)

@CliktTestDsl
suspend fun CoreCliktCommand.test(
    vararg argv: String,
    expectedExitCode: Int = 0,
    environmentVariables: Map<String, String> = emptyMap(),
    testCode: suspend CliTestScope.() -> Unit
) = test(argv.toList(), expectedExitCode, environmentVariables, testCode)