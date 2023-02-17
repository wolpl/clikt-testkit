package com.wolpl.clikttestkit

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

@CliktTestDsl
interface CliTestScope {

    /**
     * Assert that the CliktCommand writes output to standard out as its next console action.
     *
     * Note, that this captures only output written with a call to CliktCommand.echo().
     */
    @CliktTestDsl
    suspend fun expectOutput(): String

    /**
     * Assert that the CliktCommand writes output to error out as its next console action.
     *
     * Note, that this captures only output written with a call to CliktCommand.echo().
     */
    @CliktTestDsl
    suspend fun expectErrorOutput(): String


    /**
     * Assert that the CliktCommand requires console input and provide that input.
     *
     * Note, that only input requested via CliktCommand.prompt() can be provided by this function.
     */
    @CliktTestDsl
    suspend fun provideInput(line: String)

    /**
     * Assert that the CliktCommand writes the provided output to standard out as its next console action.
     *
     * Note, that this captures only output written with a call to CliktCommand.echo().
     */
    @CliktTestDsl
    suspend fun expectOutput(output: String) {
        val providedOutput = withClue("Expected output \"$output\"") {
            expectOutput()
        }
        withClue("CliktCommand provided unexpected output!") {
            providedOutput shouldBe output
        }
    }

    /**
     * Assert that the CliktCommand writes the provided output to error out as its next console action.
     *
     * Note, that this captures only output written with a call to CliktCommand.echo().
     */
    @CliktTestDsl
    suspend fun expectErrorOutput(output: String) {
        val providedOutput = withClue("Expected error output \"$output\"") {
            expectErrorOutput()
        }
        withClue("CliktCommand provided unexpected error output!") {
            providedOutput shouldBe output
        }
    }

    /**
     * Ignore any outputs that the CliktCommand produces from now on.
     * If the command tries to prompt for input, it will fail.
     */
    @CliktTestDsl
    suspend fun ignoreOutputs(): Nothing
}