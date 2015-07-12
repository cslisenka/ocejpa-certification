package net.slisenko.jpa.examples.validation;

import net.slisenko.AbstractJpaTest;
import org.junit.Ignore;
import org.junit.Test;

import javax.validation.*;
import java.util.Calendar;
import java.util.Set;

/**
 * Validation in JPA uses JSR 303 Bean Validation specification
 * We can use separate bean validation and integrate bean validation with JPA
 */
public class ValidationExampleTest extends AbstractJpaTest {

    @Test
    public void testValid() {
        em.getTransaction().begin();
        // Correct entity
        ValidationEntity ve = new ValidationEntity();
        ve.setNotNull("not null");
        ve.setNumber(1005);
//        ve.setRegexp("1f6"); // Doesn't work in this example
        ve.setShouldBeFalse(false);
        ve.setShouldBeTrue(true);
        ve.setSizeLimitation("1234567890aaaaa");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        ve.setDateInTheFuture(c.getTime());

        c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        ve.setDateInThePast(c.getTime());

        em.persist(ve);

        em.getTransaction().commit();
        em.clear();

        System.out.println(em.find(ValidationEntity.class, ve.getId()));
    }

    // TODO try @Valid annotation with Embeddables
    // TODO try postgreSQL and see if constraints are generated on DB level or not? (I think that not)
    @Test(expected = ConstraintViolationException.class)
    public void testNotValid() {
        em.getTransaction().begin();
        ValidationEntity notValid = new ValidationEntity();

        em.persist(notValid);

        em.getTransaction().commit();
    }

    /**
     * Validate entity without using JPA (use bean validation)
     */
    @Test
    public void testSeparateValidation() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        ValidationEntity notValid = new ValidationEntity();
        notValid.setNotNull(null);
        notValid.setSizeLimitation("small");
        notValid.setShouldBeTrue(false);
        notValid.setShouldBeFalse(true);
//        notValid.setRegexp("wrong"); // Doesn't work in example with Validation + JPA insert
        notValid.setShouldBeNull("not null");
        notValid.setNumber(100);
        notValid.setDigits(1);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        notValid.setDateInTheFuture(c.getTime());

        c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        notValid.setDateInThePast(c.getTime());

        Set<ConstraintViolation<ValidationEntity>> constraintViolationSet = validator.validate(notValid);
        for (ConstraintViolation<ValidationEntity> entity : constraintViolationSet) {
            System.out.println(entity);
        }
    }
}