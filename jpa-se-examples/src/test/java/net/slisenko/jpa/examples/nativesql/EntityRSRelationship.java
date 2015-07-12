package net.slisenko.jpa.examples.nativesql;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

@SqlResultSetMapping(
    name = "bothEntities",
    entities = {
        @EntityResult(entityClass = EntityRSMapping.class),
        @EntityResult(entityClass = EntityRSRelationship.class)
    }
)
@Entity
@Table(name = "entity_rs_rel")
public class EntityRSRelationship extends Identity {

    private String relParam;

    public EntityRSRelationship() {
    }

    public EntityRSRelationship(String relParam) {
        this.relParam = relParam;
    }

    public String getRelParam() {
        return relParam;
    }

    public void setRelParam(String relParam) {
        this.relParam = relParam;
    }

    @Override
    public String toString() {
        return "EntityRSRelationship{" +
                "relParam='" + relParam + '\'' +
                "} " + super.toString();
    }
}
