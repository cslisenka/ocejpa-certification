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
     * @Cache not required here because foreign key is on entity side. If we use join table, we need to set @Cache annotation.
     */
    @Transient
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    protected CachedEntityRelationship relationshipEager;

    @Transient
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    protected CachedEntityRelationship relationshipLazy;

    // TODO Cache doesn't work for this relationship because join table used
//    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
//    @OneToMany(cascade = CascadeType.ALL)
    @Transient
    protected List<CachedEntityRelationship> list = new ArrayList<>();

    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @OneToMany(mappedBy = "entity", fetch = FetchType.LAZY)
    protected List<CachedEntityRelationship> listBack = new ArrayList<>();

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

    public List<CachedEntityRelationship> getListBack() {
        return listBack;
    }

    public void setListBack(List<CachedEntityRelationship> listBack) {
        this.listBack = listBack;
    }
}