package net.slisenko.jpa.examples.primarykey.generation;

import javax.persistence.*;

@Entity
public class TablePKEntity {

    @Id
//    @TableGenerator(name = "departme_gen",
//            table = "DEPART_ID_GEN",
//            pkColumnName = "GEN_NAME",
//            valueColumnName = "GEN_VALUE",
//            initialValue = 1000) // One table generator can be used by many classes
//    @GeneratedValue(generator = "department_gen")
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String name;

    public TablePKEntity() {
    }

    public TablePKEntity(String name) {
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