package net.slisenko.jpa.examples.lifecycle.locking.exception;

/**
 * Исключение хранит информацию, какая из транзакций упала.
 * Используется для ассертов в тестах.
 */
public class TransactionFailException extends RuntimeException {

    private String transactionId;

    public TransactionFailException(String transactionId, Throwable cause) {
        super(cause);
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}