package net.slisenko.jpa.examples.relationship.collections.map;

import net.slisenko.AbstractJpaTest;
import org.junit.Test;

/**
 * Map keys and values can be: basic type, entities, embeddables
 *
 * Map key must properly implement equals() and hashCode(). Key objects should not be changed.
 * Basic types and embeddables stored in mapping association (target entity table, join table, collection table)
 * Entities always represented as foreign keys
 */
public class TestMapCollection extends AbstractJpaTest {

    @Test
    public void testType() {
        em.getTransaction().begin();
        Person kostya = new Person("Kostya");
        kostya.getContacts().put("phone", "2223344");
        kostya.getContacts().put("email", "kostya@google.com");
        kostya.getContacts().put("skype", "kostya_skype_example");

        kostya.getLikes().put(PersonLikeType.FOOD, "icecream");
        kostya.getLikes().put(PersonLikeType.HOBBY, "bike riding");
        kostya.getLikes().put(PersonLikeType.MUSIC, "rock");

        em.persist(kostya);
        em.getTransaction().commit();
        em.clear();

        kostya = em.find(Person.class, kostya.getId());
        System.out.println(kostya);
    }

    @Test
    public void testValueRelationship() {
        em.getTransaction().begin();
        Person kostya = new Person("Kostya");
        em.persist(kostya);
        Person yuri = new Person("Yuri");
        em.persist(yuri);
        Person ann = new Person("Ann");
        em.persist(ann);

        Club sports = new Club("sports");
        em.persist(sports);
        Club games = new Club("games");
        em.persist(games);

        // Fill OneToMany map - we can not duplicate map values
        // On database level, there is unique kty for each Person
        kostya.getFamily().put("sister", ann);
        kostya.getFamily().put("father", yuri);
        //        ann.getFamily().put("father", yuri); // Uncomment to see error
        yuri.getFamily().put("son", kostya);

        // Fill ManyToMany membership - we can duplicate map values
        sports.getMembers().put("president", kostya);
        sports.getMembers().put("member", ann);
        games.getMembers().put("president", yuri);
        games.getMembers().put("member", kostya);
        em.getTransaction().commit();
    }

    @Test
    public void testKeyByEntityAttribute() {

    }
}