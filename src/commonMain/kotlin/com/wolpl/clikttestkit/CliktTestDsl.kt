package com.wolpl.clikttestkit

import arrow.fx.coroutines.resourceScope
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.core.context
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@DslMarker
annotation class CliktTestDsl

@OptIn(ExperimentalCoroutinesApi::class)
@CliktTestDsl
suspend fun CliktCommand.test(
    argv: List<String>,
    expectedExitCode: Int = 0,
    environmentVariables: Map<String, String> = emptyMap(),
    testCode: suspend CliTestScope.() -> Unit
) {
    coroutineScope {
        resourceScope {

            val testConsole = TestConsole()
            val testScope = MutableCliTestScope(testConsole)

            val cliContext = install(
                { newSingleThreadContext("clikt-testkit-cli-context") },
                { it, _ -> it.close() }
            )

            launch(cliContext) {
                try {
                    this@test
                        .context {
                            console = testConsole
                            envvarReader = environmentVariables::get
                        }
                        .parse(argv)
                    testConsole.terminate(0)
                } catch (programResult: ProgramResult) {
                    testConsole.terminate(programResult.statusCode)
                } catch (e: CliktTestCancellation) {
                    throw e.cause
                }
            }

            try {
                testScope.testCode()
            } catch (_: CancellationException) {
                // Command terminated, so a CancellationException was thrown in TestScope.ignoreOutputs()
            } finally {
                testConsole.cancel()
            }
            testConsole.exitCode.filterNotNull().first() shouldBe expectedExitCode
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