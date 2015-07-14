package net.slisenko.jpa.examples.criteriaApi;

import net.slisenko.jpa.examples.criteriaApi.model.CrEmployee;
import org.junit.Test;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

public class TestCriteriaAPIBulkUpdateDelete extends BaseCriteriaAPITest {

    @Test
    public void testBulkUpdate() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<CrEmployee> qUpd = cb.createCriteriaUpdate(CrEmployee.class);
        Root<CrEmployee> empRoot = qUpd.from(CrEmployee.class);
        qUpd.set(empRoot.<Integer>get("salary"), cb.sum(empRoot.<Integer>get("salary"), 1000));

        em.getTransaction().begin();
        em.createQuery(qUpd).executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    public void testBulkDelete() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<CrEmployee> qd = cb.createCriteriaDelete(CrEmployee.class);
        Root<CrEmployee> empRoot = qd.from(CrEmployee.class);
        qd.where(cb.equal(empRoot.get("age"), 25));

        em.getTransaction().begin();
        em.createQuery(qd).executeUpdate();
        em.getTransaction().commit();
    }
}
