package ch.valentindibbern.mastermind.domain;

@FunctionalInterface
public interface SecretCodeProvider {
    Color[] createSecretCode();
}
