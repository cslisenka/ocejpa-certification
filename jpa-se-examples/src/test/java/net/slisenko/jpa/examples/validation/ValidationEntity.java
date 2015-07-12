package net.slisenko.jpa.examples.validation;

import net.slisenko.Identity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.*;
import java.util.Date;

@Entity
public class ValidationEntity extends Identity {

    @NotNull
    @Column(nullable = false)
    private String notNull;

    @Size(max = 40, min = 10)
    private String sizeLimitation;

    @Past
    private Date dateInThePast;

    @Future
    private Date dateInTheFuture;

    @Null
    private String shouldBeNull;

    @AssertTrue
    private boolean shouldBeTrue;

    @AssertFalse
    private boolean shouldBeFalse;

    @Min(1000)
    @Max(2000)
    public long number;

    @Digits(integer = 5, fraction = 2)
    private long digits;

//    @Pattern(regexp = "[1-9][a-z][1-9]")
//    private String regexp;

    public String getNotNull() {
        return notNull;
    }

    public void setNotNull(String notNull) {
        this.notNull = notNull;
    }

    public String getSizeLimitation() {
        return sizeLimitation;
    }

    public void setSizeLimitation(String sizeLimitation) {
        this.sizeLimitation = sizeLimitation;
    }

    public Date getDateInThePast() {
        return dateInThePast;
    }

    public void setDateInThePast(Date dateInThePast) {
        this.dateInThePast = dateInThePast;
    }

    public Date getDateInTheFuture() {
        return dateInTheFuture;
    }

    public void setDateInTheFuture(Date dateInTheFuture) {
        this.dateInTheFuture = dateInTheFuture;
    }

    public String getShouldBeNull() {
        return shouldBeNull;
    }

    public void setShouldBeNull(String shouldBeNull) {
        this.shouldBeNull = shouldBeNull;
    }

    public boolean isShouldBeTrue() {
        return shouldBeTrue;
    }

    public void setShouldBeTrue(boolean shouldBeTrue) {
        this.shouldBeTrue = shouldBeTrue;
    }

    public boolean isShouldBeFalse() {
        return shouldBeFalse;
    }

    public void setShouldBeFalse(boolean shouldBeFalse) {
        this.shouldBeFalse = shouldBeFalse;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getDigits() {
        return digits;
    }

    public void setDigits(long digits) {
        this.digits = digits;
    }

//    public String getRegexp() {
//        return regexp;
//    }
//
//    public void setRegexp(String regexp) {
//        this.regexp = regexp;
//    }

    @Override
    public String toString() {
        return "ValidationEntity{" +
                "notNull='" + notNull + '\'' +
                ", sizeLimitation='" + sizeLimitation + '\'' +
                ", dateInThePast=" + dateInThePast +
                ", dateInTheFuture=" + dateInTheFuture +
                ", shouldBeNull='" + shouldBeNull + '\'' +
                ", shouldBeTrue=" + shouldBeTrue +
                ", shouldBeFalse=" + shouldBeFalse +
                ", number=" + number +
                ", digits=" + digits +
//                ", regexp='" + regexp + '\'' +
                "} " + super.toString();
    }
}