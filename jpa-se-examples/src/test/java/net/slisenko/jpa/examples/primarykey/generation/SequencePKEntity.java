package net.slisenko.jpa.examples.primarykey.generation;

import javax.persistence.*;

@Entity
public class SequencePKEntity {

    // TODO MySQL не поддерживает сиквенсов  :(
    @Id
//    @SequenceGenerator(name = "task_gen",
//            sequenceName = "task_seq",
//            initialValue = 50,
//            allocationSize = 100)
//    @GeneratedValue(generator = "task_gen")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @GeneratedValue
    private Long id;

    private String name;

    public SequencePKEntity() {
    }

    public SequencePKEntity(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
