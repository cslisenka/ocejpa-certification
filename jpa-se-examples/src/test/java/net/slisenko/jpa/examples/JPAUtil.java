package net.slisenko.jpa.examples;

import org.hibernate.Session;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.internal.SessionImpl;
import org.hibernate.jpa.internal.EntityManagerImpl;

import javax.persistence.EntityManager;
import java.util.Map;

public class JPAUtil {

    public static void printObjectsState(EntityManager em, Object... objects) {
        System.out.format("===========================================================================\n");
        for (Object entity : objects) {
            System.out.format("%15s => %s\n", entity, em.contains(entity) ? "managed" : "detached");
        }
        System.out.format("===========================================================================\n");
    }

    public static void printContext(EntityManager em) {
        Session hibernateSession = ((EntityManagerImpl) em).getSession();
        PersistenceContext context = ((SessionImpl) hibernateSession).getPersistenceContext();

        // Print entities which are in context
        Map entities =  context.getEntitiesByKey();
        System.out.format("===========================================================================\n");
        for (Object entity : entities.values()) {
            System.out.println(entity);
        }
        System.out.format("===========================================================================\n");
    }
}
