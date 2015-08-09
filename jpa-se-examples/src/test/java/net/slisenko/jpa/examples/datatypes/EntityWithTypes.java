package net.slisenko.jpa.examples.datatypes;

import net.slisenko.Identity;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Entity
@AttributeOverrides({
    @AttributeOverride(name = "embedded.country", column = @Column(name = "strana")),
})
@Table(name = "entity_with_types")
public class EntityWithTypes extends Identity {

    @Lob
    @Column(name = "my_long_string")
    private String longString;

    @Lob
    private byte[] binaryData;

    private MyEnumType numberedEnum;

    @Enumerated(EnumType.STRING)
    private MyEnumType stringEnum;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar timestamp;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Temporal(TemporalType.TIME)
    private Date time;

    private transient String transientString;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "city", column = @Column(name = "gorod"))
    })
    // Works only when we define @JoinColumn in MyEmbeddedObject type
    @AssociationOverrides({
        @AssociationOverride(name = "entity", joinColumns = @JoinColumn(name = "overriden"))
    })
    private MyEmbeddedObject embedded;

    @Transient
    private String transientString2;

    public String getLongString() {
        return longString;
    }

    public void setLongString(String longString) {
        this.longString = longString;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public MyEnumType getNumberedEnum() {
        return numberedEnum;
    }

    public void setNumberedEnum(MyEnumType numberedEnum) {
        this.numberedEnum = numberedEnum;
    }

    public MyEnumType getStringEnum() {
        return stringEnum;
    }

    public void setStringEnum(MyEnumType stringEnum) {
        this.stringEnum = stringEnum;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTransientString() {
        return transientString;
    }

    public void setTransientString(String transientString) {
        this.transientString = transientString;
    }

    public String getTransientString2() {
        return transientString2;
    }

    public void setTransientString2(String transientString2) {
        this.transientString2 = transientString2;
    }

    public MyEmbeddedObject getEmbedded() {
        return embedded;
    }

    public void setEmbedded(MyEmbeddedObject embedded) {
        this.embedded = embedded;
    }

    @Override
    public String toString() {
        return "EntityWithTypes{" +
                "longString='" + longString + '\'' +
                ", binaryData=" + Arrays.toString(binaryData) +
                ", numberedEnum=" + numberedEnum +
                ", stringEnum=" + stringEnum +
                ", timestamp=" + timestamp +
                ", date=" + date +
                ", time=" + time +
                ", transientString='" + transientString + '\'' +
                ", embedded=" + embedded +
                ", transientString2='" + transientString2 + '\'' +
                '}';
    }
}
