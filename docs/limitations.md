# Limitations

Be aware of the following limitations when using Clikt Testkit:

- The `test()` function can only capture outputs that are printed via `CliktCommand.echo()`. Anything printed with `println()`, `System.out.print()`, ... is invisible to the testkit.
- You can only provide input to the cli, if it asks for it using `CliktCommand.terminal.prompt()` or `CliktCommand.terminal.readLineOrNull()`.
- After one of the provided expectations (assertions) fails, the test may still continue to run.
  This is, because the Clikt Testkit cannot stop the execution of your CliktCommand, unless it calls `echo()` or `prompt()`.
  Once one of those is called, the command will stop executing because of an exception, even though, Clikt itself catches most exceptions that occur inside a call to `prompt()`.