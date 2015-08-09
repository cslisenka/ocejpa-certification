package net.slisenko.jpa.examples.relationship.collections.map;

import net.slisenko.Identity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Person extends Identity {

    /**
     * Key: BASIC, Value: BASIC
     */
    @ElementCollection
    @CollectionTable(name = "person_contacts")
    @MapKeyColumn(name = "contact_type") // Override collection table key column
    @Column(name = "contact_value") // Override collection table value column
    private Map<String, String> contacts = new HashMap<>();

    /**
     * Key: ENUM, Value: BASIC
     * Actually this is basic type
     */
    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    private Map<PersonLikeType, String> likes = new HashMap<>();

    // Join table used
    @OneToMany
    @MapKeyColumn(name = "relation_type")
    private Map<String, Person> family = new HashMap<>();

    @OneToMany(mappedBy = "person")
    @MapKey(name = "name")
    private Map<String, CreditCard> creditCards = new HashMap<>();

    // TODO key = Entity

    // TODO key = embedded type, hashCode/equals are mandatory, test attribute overrides

    // TODO key, value = embedded types, apply attributes for key and value

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public Map<String, String> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, String> contacts) {
        this.contacts = contacts;
    }

    public Map<PersonLikeType, String> getLikes() {
        return likes;
    }

    public void setLikes(Map<PersonLikeType, String> likes) {
        this.likes = likes;
    }

    public Map<String, Person> getFamily() {
        return family;
    }

    public void setFamily(Map<String, Person> family) {
        this.family = family;
    }

    public Map<String, CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(Map<String, CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", contacts=" + contacts +
                ", likes=" + likes +
                ", relatives=" + family +
                "} " + super.toString();
    }
}