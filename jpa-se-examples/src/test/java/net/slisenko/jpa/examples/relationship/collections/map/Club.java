package net.slisenko.jpa.examples.relationship.collections.map;

import net.slisenko.Identity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Club extends Identity {

    @ManyToMany
    @JoinTable(name = "club_person_engagement",
        joinColumns = @JoinColumn(name = "club_id"),
        inverseJoinColumns = @JoinColumn(name = "person_id"))
    @MapKeyColumn(name = "membership_type")
    private Map<String, Person> members = new HashMap<>();

//    @ManyToOne
//    private Map<Long, Person> membersByIds = new HashMap<>();

    public Club() {
    }

    public Club(String name) {
        this.name = name;
    }

    public Map<String, Person> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Person> members) {
        this.members = members;
    }

//    public Map<Long, Person> getMembersByIds() {
//        return membersByIds;
//    }
//
//    public void setMembersByIds(Map<Long, Person> membersByIds) {
//        this.membersByIds = membersByIds;
//    }
}