# User Guide

The Clikt Testkit provides an extension function to `CliktCommand`, that executes the command and allows you to test its behavior.
This guide explains how to write [assertions on the outputs and inputs](#inputs-and-outputs) of a CliktCommand, as well as how to configure, [how the command is executed](#command-options).

## Inputs and Outputs
The Clikt Testkit allows you to assert standard and error outputs, as well as to answer prompts from a `CliktCommand`:

```kotlin
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
```

If you want to test only the first few outputs of your test, you can ignore the rest of them by calling `ignoreOutputs()` in your test code:
```kotlin
class TestCommand : CliktCommand() {
    override fun run() {
        echo("Output 1")
        echo("Output 2")
        echo("Output 3")
    }
}

// This test will succeed:
TestCommand().test {
    expectOutput("Output 1")
    ignoreOutputs()
}
```

## Command Options
### Providing command line arguments
To provide command line arguments to the tested command, you can supply them to the `test()` function:

```kotlin

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
```

### Providing environment variables
Clikt allows reading values from environment variables.
You can provide those for testing, by setting the respective parameter of the `test()` function:
````kotlin
class Greet : CliktCommand() {
    val name by option(envvar = "GREETER_NAME").required()
    override fun run() {
        echo("Hello $name!")
    }
}

Greet().test(
    environmentVariables = mapOf(
        "GREETER_NAME" to "Tester"
    )
) {
    expectOutput("Hello Tester!")
}
````

!!! warning

    This feature is implemented by setting the `envvarReader` in the `context` of the CliktCommand you test.
    Consequently, providing test environment variables will not work, if a subcommand of the tested command overwrites this field.