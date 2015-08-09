package net.slisenko.jpa.examples.ee.ejb;

import net.slisenko.jpa.examples.ee.model.EESimpleEntity;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

@Stateful
public class ExtendedContextBean {

    /**
     * Такой контекст нам нужен если у нас есть persistence-логика в сессии - например покупательская корзина.
     * Мы не хотим чтобы между транзакциями сущности детачились и их приходилось потом мержить.
     */
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    private EESimpleEntity entity;

    public EESimpleEntity addEntity() {
        entity = new EESimpleEntity("entity added in extended persistence context");
        em.persist(entity);
        System.out.println(em);
        return entity;
    }

    /**
     * Будет возвращать true если мы включим extended-контекст = сущность не детачится
     * Будет false если у нас обычный транзакционный контекст (сущность детачится)
     */
    public boolean checkEntityInContext() {
        System.out.println(em);
        return em.contains(entity);
    }
}
