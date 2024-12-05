package book.manning.javapersistence.ch11.timestamp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class VersioningTimestamp {

    static EntityManagerFactory emf;

    static {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        emf = Persistence.createEntityManagerFactory("ch11", properties);
    }

    @Test
    void firstCommitWins() throws ExecutionException, InterruptedException {
        Item someItem;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            someItem = new Item("Some Item");
            em.persist(someItem);
            em.getTransaction().commit();
        }
        final Long ITEM_ID = someItem.getId();

        try (EntityManager em1 = emf.createEntityManager()) {
            em1.getTransaction().begin();

            var item = em1.find(Item.class, ITEM_ID);
            item.setName("New Name");

            try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
                executorService.submit(() -> {
                    try (EntityManager em2 = emf.createEntityManager()) {
                        em2.getTransaction().begin();

                        var item1 = em2.find(Item.class, ITEM_ID);

                        item1.setName("Other Name");

                        em2.getTransaction().commit();
                    } catch (Exception ex) {
                        throw new RuntimeException("Concurrent operation failure: " + ex, ex);
                    }
                    return null;
                }).get();

                assertThrows(OptimisticLockException.class, em1::flush);
            }
        }
    }
}