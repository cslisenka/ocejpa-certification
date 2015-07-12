package net.slisenko.jpa.examples.schemaGenerator;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Аннотации не влияют на поведение хибернейта. Они применяются только при генерации схемы!
 * Если генерация схемы не предусмотрена, то от них никакого смысла нет.
 */
@Entity
@Table(indexes = {
    @Index(name = "age", columnList = "age"),
    @Index(name = "name_age", columnList = "name, age"),
    @Index(name = "email", columnList = "email", unique = true)
})
public class IndexedEntity extends Identity {

    private int age;
    private String email;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}