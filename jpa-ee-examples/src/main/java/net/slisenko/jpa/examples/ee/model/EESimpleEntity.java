package net.slisenko.jpa.examples.ee.model;

/**
 * All mappings related information provided in xml files.
 */
//@Entity
public class EESimpleEntity {

//    @Id
//    @GeneratedValue
    private int id;

    private String name;

    public EESimpleEntity() {
    }

    public EESimpleEntity(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
