# Repository Guidelines

## Project Purpose and Structure

This repository contains a Java command-line implementation of the classic Mastermind game, produced as an Iperka learning assignment. The authoritative assignment scan is `docs/AufgabenScann.pdf`; `docs/Mastermind.md` records the game rules only. Keep Java source in `src/main/java/`, tests in `src/test/java/`, and resources in `src/main/resources/`. Do not edit `.idea/` unless an IDE configuration change is intentional.

The program must generate a secret four-colour code from six available colours; duplicate colours are allowed. A player receives at most seven guesses. Track ongoing, won, and lost states, and show feedback after every guess.

## Build, Test, and Run

No Maven or Gradle build is committed yet. Until one is added, compile and run with IntelliJ IDEA or the JDK:

```powershell
javac -d out src/main/java/<package>/*.java
java -cp out <package>.Main
```

If Maven or Gradle is introduced, commit its wrapper and use it as the canonical build and test command. Never commit generated `out/`, `target/`, `build/`, or `.class` files.

## Java Style and Design

Use four-space indentation, same-line braces, `PascalCase` for classes, `camelCase` for methods and variables, and `UPPER_SNAKE_CASE` for constants. Keep classes focused: separate game flow, input/output, code generation, and feedback evaluation. Use arrays deliberately; the assignment assesses the ability to explain Java arrays, including two-dimensional arrays where used. Add comments for non-obvious logic, especially duplicate-colour feedback handling.

## Testing

Place unit tests under `src/test/java/` with mirrored packages. Name tests by behaviour, for example `returnsBlackMarkForCorrectColourAndPosition`. Cover exact matches, colours in wrong positions, duplicate colours, invalid input, all seven failed guesses, and victory. Make random code generation injectable or seedable so tests stay deterministic.

## Commits and Pull Requests

There is no existing commit history. Use short imperative subjects such as `Add feedback evaluation` or `Handle seventh failed guess`. Keep commits narrow. Pull requests must summarize the behaviour changed, identify the relevant requirement, and list the commands or scenarios tested. Screenshots are unnecessary for the command-line version.
