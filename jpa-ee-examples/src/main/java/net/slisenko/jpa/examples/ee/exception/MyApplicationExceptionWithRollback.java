package net.slisenko.jpa.examples.ee.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class MyApplicationExceptionWithRollback extends RuntimeException {
}
