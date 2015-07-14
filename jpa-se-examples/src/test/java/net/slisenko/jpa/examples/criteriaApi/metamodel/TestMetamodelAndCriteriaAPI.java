package net.slisenko.jpa.examples.criteriaApi.metamodel;

import net.slisenko.jpa.examples.criteriaApi.BaseCriteriaAPITest;
import net.slisenko.jpa.examples.criteriaApi.model.CrEmployee;
import org.junit.Test;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import java.util.List;

public class TestMetamodelAndCriteriaAPI extends BaseCriteriaAPITest {

    @Test
    public void testQueryUsingMetamodel() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<CrEmployee> root = cq.from(CrEmployee.class);
        EntityType<CrEmployee> type = root.getModel();
        cq.select(root.get(type.getSingularAttribute("age", Integer.class)));

        List<Integer> results = em.createQuery(cq).getResultList();
        System.out.println(results);
    }
}
