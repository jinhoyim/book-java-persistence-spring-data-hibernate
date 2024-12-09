package book.manning.javapersistence.ch11.exceptions;

public class DuplicateItemNameException extends RuntimeException {
  public DuplicateItemNameException(String message) {
    super(message);
  }
}
