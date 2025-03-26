package br.com.thiagodotjpeg.exceptions;

public class RequiredObjectIsNullException extends RuntimeException {
  public RequiredObjectIsNullException(String message) {
    super(message);
  }
}
