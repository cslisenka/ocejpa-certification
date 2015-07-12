package net.slisenko.jpa.examples.secondaryTable;

import net.slisenko.Identity;

import javax.persistence.*;

@Entity
@Table(name = "EntityTable1")
@SecondaryTable(name = "EntityTable2", pkJoinColumns = @PrimaryKeyJoinColumn(name = "entity_1_id"))
public class OneEntityTwoTables extends Identity {

    private String entity1Attr;

    @Column(table = "EntityTable2")
    private String entity2Attr;

    public OneEntityTwoTables() {
    }

    public OneEntityTwoTables(String entity1Attr, String entity2Attr) {
        this.entity1Attr = entity1Attr;
        this.entity2Attr = entity2Attr;
    }

    public String getEntity1Attr() {
        return entity1Attr;
    }

    public void setEntity1Attr(String entity1Attr) {
        this.entity1Attr = entity1Attr;
    }

    public String getEntity2Attr() {
        return entity2Attr;
    }

    public void setEntity2Attr(String entity2Attr) {
        this.entity2Attr = entity2Attr;
    }

    @Override
    public String toString() {
        return "OneEntityTwoTables{" +
                "entity1Attr='" + entity1Attr + '\'' +
                ", entity2Attr='" + entity2Attr + '\'' +
                "} " + super.toString();
    }
}