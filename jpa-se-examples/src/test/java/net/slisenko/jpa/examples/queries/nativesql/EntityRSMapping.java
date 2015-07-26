package net.slisenko.jpa.examples.queries.nativesql;

import net.slisenko.Identity;

import javax.persistence.*;

// We can not populate relationships from SQL, all relationships are calculated automatically
@SqlResultSetMapping(
    name = "oneEntity",
    entities = @EntityResult(entityClass = EntityRSMapping.class)
)
@Entity
@Table(name = "entity_rs_main")
public class EntityRSMapping extends Identity {

    private String entityParam;

    @ManyToOne(cascade = CascadeType.ALL)
    private EntityRSRelationship relationship;

    public EntityRSMapping() {
    }

    public EntityRSMapping(String entityParam) {
        this.entityParam = entityParam;
    }

    public EntityRSRelationship getRelationship() {
        return relationship;
    }

    public void setRelationship(EntityRSRelationship relationship) {
        this.relationship = relationship;
    }

    public String getEntityParam() {
        return entityParam;
    }

    public void setEntityParam(String entityParam) {
        this.entityParam = entityParam;
    }

    @Override
    public String toString() {
        return "EntityRSMapping{" +
                "entityParam='" + entityParam + '\'' +
                ", relationship=" + relationship +
                "} " + super.toString();
    }
}