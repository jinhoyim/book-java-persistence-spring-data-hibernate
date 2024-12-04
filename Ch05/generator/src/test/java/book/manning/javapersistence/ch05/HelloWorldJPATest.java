package book.manning.javapersistence.ch05;

import book.manning.javapersistence.ch05.model.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldJPATest {
    private static EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));

        return Persistence.createEntityManagerFactory("ch05.generator", properties);
    }

    @Test
    void storeLoadItem() {
        try (EntityManagerFactory emf = createEntityManagerFactory();
             EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();

            Item item = new Item();
            item.setName("Some Item");
            em.persist(item);

            em.getTransaction().commit();

            em.getTransaction().begin();

            List<Item> items =
                    em.createQuery("select i from Item i", Item.class)
                            .getResultList();

            em.getTransaction().commit();

            assertAll(
                    () -> assertEquals(1, items.size()),
                    () -> assertEquals("Some Item", items.get(0).getName()),
                    () -> assertTrue(item.getId() >= 1000)
            );
        }
    }
}