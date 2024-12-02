package book.jpa.jpa_hibernate.ch02;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StaticMetamodelTest {
    private static EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));

        return Persistence.createEntityManagerFactory("ch02", properties);
    }

    @Test
    void metamodel_test() {
        try (EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
            EntityManager em = entityManagerFactory.createEntityManager()) {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Item> query = cb.createQuery(Item.class);
            Root<Item> fromItem = query.from(Item.class);
            query.select(fromItem);

            List<Item> items = em.createQuery(query).getResultList();
            assertEquals(0, items.size());
        }
    }

    @Test
    void metamodel_test2() {
        try (EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
             EntityManager em = entityManagerFactory.createEntityManager()) {

            em.getTransaction().begin();
            Item item = new Item("Item 1", LocalDate.now().plusDays(1));
            em.persist(item);
            em.getTransaction().commit();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Item> query = cb.createQuery(Item.class);
            Root<Item> fromItem = query.from(Item.class);
            query.select(fromItem);

//            Path<String> namePath = fromItem.get("name");
            Path<String> namePath = fromItem.get(Item_.name);
            query.where(cb.like(namePath, cb.parameter(String.class, "pattern")));
            List<Item> items = em.createQuery(query)
                    .setParameter("pattern", "%Item 1%")
                    .getResultList();

            assertAll(
                    () -> assertEquals(1, items.size()),
                    () -> assertEquals("Item 1", items.iterator().next().getName())
            );
        }
    }
}
