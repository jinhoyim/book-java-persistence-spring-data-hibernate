package book.manning.javapersistence.ch11.concurrency;

public class InvalidBidException extends RuntimeException {
    public InvalidBidException(String message) {
        super(message);
    }
}
