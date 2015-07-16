package net.slisenko.jpa.examples.caching;

import net.slisenko.Identity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// TODO region="yourEntityCache"
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
// TODO doesn't work for me
//@Cacheable
@Entity
public class CachedEntity extends Identity {

    /**
     * TODO Associations can also be cached by the second level cache, but by default this is not done. In order to enable caching of an association, we need to apply @Cache to the association itself
     */
//    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    protected CachedEntityRelationship relationshipEager;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    protected CachedEntityRelationship relationshipLazy;

    @OneToMany(cascade = CascadeType.ALL)
    protected List<CachedEntityRelationship> list = new ArrayList<>();

    public CachedEntity() {
    }

    public CachedEntity(String name) {
        this.name = name;
    }

    public CachedEntityRelationship getRelationshipEager() {
        return relationshipEager;
    }

    public void setRelationshipEager(CachedEntityRelationship relationshipEager) {
        this.relationshipEager = relationshipEager;
    }

    public CachedEntityRelationship getRelationshipLazy() {
        return relationshipLazy;
    }

    public void setRelationshipLazy(CachedEntityRelationship relationshipLazy) {
        this.relationshipLazy = relationshipLazy;
    }

    public List<CachedEntityRelationship> getList() {
        return list;
    }

    public void setList(List<CachedEntityRelationship> list) {
        this.list = list;
    }
}