package com.wolpl.clikttestkit

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import io.kotest.assertions.shouldFail
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

class CliktTestDslTest : FreeSpec({
    "The Clikt tester" - {

        "should run the full example" {
            class Greeter : CliktCommand() {
                override fun run() {
                    val name = terminal.prompt("Enter your name")
                    echo("Hello $name!")
                    echo("I failed successfully!", err = true)
                    throw ProgramResult(-42)
                }
            }

            Greeter().test(expectedExitCode = -42) {
                expectOutput("Enter your name: ")
                provideInput("Tester")
                expectOutput("Hello Tester!")
                expectErrorOutput("I failed successfully!")
            }
        }

        "should test for output" {
            class HelloWorld : CliktCommand() {
                override fun run() {
                    echo("Hello World!")
                }
            }

            HelloWorld().test {
                expectOutput("Hello World!")
            }
        }

        "should test error output" {
            val command = object : CliktCommand() {
                override fun run() {
                    echo("Error message!", err = true)
                }
            }

            command.test {
                expectErrorOutput("Error message!")
            }
        }

        "should provide input" {
            val command = object : CliktCommand() {
                override fun run() {
                    val input = terminal.prompt("Input")
                    echo(input)
                }
            }

            command.test {
                expectOutput("Input: ")
                provideInput("Hello123")
                expectOutput("Hello123")
            }
        }

        "should check the exit code" {
            val command = object : CliktCommand() {
                override fun run() {
                    throw ProgramResult(42)
                }
            }
            shouldFail {
                command.test(expectedExitCode = 43) {

                }
            }
        }

        "should provide console arguments" {
            class Greet : CliktCommand() {
                val name by argument()
                val count by option().int().default(1)
                override fun run() {
                    repeat(count) {
                        echo("Hello $name!")
                    }
                }
            }

            Greet().test("--count", "3", "Tester") {
                expectOutput("Hello Tester!")
                expectOutput("Hello Tester!")
                expectOutput("Hello Tester!")
            }
        }

        "should call subcommands" {
            class Cli : CliktCommand() {
                override fun run() {}

                init {
                    subcommands(Greet())
                }

                inner class Greet : CliktCommand() {
                    val name by argument()
                    override fun run() {
                        echo("Hello $name!")
                    }
                }
            }

            Cli().test("greet", "Tester") {
                expectOutput("Hello Tester!")
            }
        }

        "should provide environment variables" {
            val envVarName = "CLIKT_TEST_ENV_VAR_NAME"

            class TestCommand : CliktCommand() {
                val name by option(envvar = envVarName).required()
                override fun run() {
                    echo("Hello $name!")
                }
            }

            TestCommand().test(
                environmentVariables = mapOf(
                    envVarName to "Tester"
                )
            ) {
                expectOutput("Hello Tester!")
            }
        }

        "should ignore additional outputs" {
            class TestCommand : CliktCommand() {
                override fun run() {
                    repeat(5) {
                        echo("Output")
                    }
                }
            }

            withTimeout(1.seconds) {
                TestCommand().test {
                    expectOutput("Output")
                    ignoreOutputs()
                }
            }
        }

        "should report errors" - {
            "when output is missing" {
                val command = object : CliktCommand() {
                    override fun run() {

                    }
                }
                shouldFail {
                    command.test {
                        expectOutput("Output")
                    }
                }.printStackTrace()
            }

            "when the wrong output is given" {
                class TestCommand : CliktCommand() {
                    override fun run() {
                        echo("Wrong output")
                    }
                }

                shouldFail {
                    TestCommand().test {
                        expectOutput("Correct output")
                    }
                }.printStackTrace()
            }

            "when error output is given instead of stdout output" {
                class TestCommand : CliktCommand() {
                    override fun run() {
                        echo("Output", err = true)
                    }
                }

                shouldFail {
                    TestCommand().test {
                        expectOutput("Output")
                    }
                }.printStackTrace()
            }

            "when stdout output is given instead of error output" {
                class TestCommand : CliktCommand() {
                    override fun run() {
                        echo("Output")
                    }
                }

                shouldFail {
                    TestCommand().test {
                        expectErrorOutput("Output")
                    }
                }.printStackTrace()
            }

            "when the command terminates with an unexpected error code" {
                class TestCommand : CliktCommand() {
                    override fun run() {
                        throw ProgramResult(-42)
                    }
                }

                shouldFail {
                    TestCommand().test(expectedExitCode = 1) {

                    }
                }.printStackTrace()
            }

            "when the command requires input, but the test code called ignoreOutputs()" {
                class TestCommand : CliktCommand() {
                    override fun run() {
                        terminal.prompt("Input")
                    }
                }

                shouldFail {
                    TestCommand().test {
                        ignoreOutputs()
                    }
                }.printStackTrace()
            }
        }

        "should cancel the command" - {
            "after the test failed" - {
                "once it produces output" {
                    class TestCommand : CliktCommand() {
                        override fun run() {
                            runBlocking {
                                while (true) {
                                    echo("Wrong output")
                                }
                            }
                        }
                    }

                    withTimeout(3.seconds) {
                        shouldFail {
                            TestCommand().test {
                                expectOutput("Correct output")
                            }
                        }.printStackTrace()
                    }
                }
                "once it expects input" {
                    class TestCommand : CliktCommand() {
                        override fun run() {
                            runBlocking {
                                terminal.prompt("Wrong output")
                            }
                        }
                    }

                    withTimeout(3.seconds) {
                        shouldFail {
                            TestCommand().test {
                                expectOutput("Correct output")
                            }
                        }.printStackTrace()
                    }
                }
                "repeatedly prompting" {
                    class TestCommand : CliktCommand() {
                        override fun run() {
                            runBlocking {
                                while (true) {
                                    terminal.prompt("Wrong output")
                                }
                            }
                        }
                    }

                    withTimeout(3.seconds) {
                        shouldFail {
                            TestCommand().test {
                                expectOutput("Correct output")
                            }
                        }.printStackTrace()
                    }
                }
            }

            "if it tries to produce additional standard output" {
                class TestCommand : CliktCommand() {
                    override fun run() {
                        echo("Output 1")
                        echo("Output 2")
                    }
                }

                withTimeout(3.seconds) {
                    shouldFail {
                        TestCommand().test {
                            expectOutput("Output 1")
                        }
                    }.printStackTrace()
                }
            }

            "if it tries to produce additional error output" {
                class TestCommand : CliktCommand() {
                    override fun run() {
                        echo("Output 1", err = true)
                        echo("Output 2", err = true)
                    }
                }

                withTimeout(3.seconds) {
                    shouldFail {
                        TestCommand().test {
                            expectErrorOutput("Output 1")
                        }
                    }.printStackTrace()
                }
            }

            "if it tries to get additional input" {
                class TestCommand : CliktCommand() {
                    override fun run() {
                        terminal.prompt("Prompt text")
                    }
                }

                withTimeout(3.seconds) {
                    val assertionError = shouldFail {
                        TestCommand().test {
                            expectOutput("Prompt text: ")
                        }
                    }
                    assertionError.printStackTrace()
                    assertionError shouldHaveMessage "CliktCommand expected input, but test code did not provide any!"
                }
            }
        }
    }
})