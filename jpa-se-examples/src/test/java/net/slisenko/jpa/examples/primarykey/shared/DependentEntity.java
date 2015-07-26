package net.slisenko.jpa.examples.primarykey.shared;

import javax.persistence.*;

@Entity
@IdClass(EmbeddedIdClass.class)
public class DependentEntity {

    /**
     * Identified includes not only relationship, but it's state
     */
    @Id
    @OneToOne
    private MainEntity main;

    @Id
    private String ownName;

    public MainEntity getMain() {
        return main;
    }

    public void setMain(MainEntity main) {
        this.main = main;
    }

    public String getOwnName() {
        return ownName;
    }

    public void setOwnName(String ownName) {
        this.ownName = ownName;
    }

    @Override
    public String toString() {
        return "DependentEntity{" +
                "main=" + main +
                ", ownName='" + ownName + '\'' +
                '}';
    }
}