package setta;

public class RuntimeError extends RuntimeException {
  final SettaToken token;

  RuntimeError(SettaToken token, String message) {
    super(message);
    this.token = token;
  }
}
