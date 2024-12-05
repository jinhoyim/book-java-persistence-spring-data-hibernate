package book.manning.javapersistence.ch11.concurrency;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Versioning {

    static EntityManagerFactory emf;

    static {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        emf = Persistence.createEntityManagerFactory("ch11", properties);
    }

    @Test
    void firstCommitWins() throws ExecutionException, InterruptedException {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var someItem = new Item("Some Item");
            em.persist(someItem);
            em.getTransaction().commit();
            em.close();

            final Long ITEM_ID = someItem.getId();

            try (EntityManager em1 = emf.createEntityManager()) {
                em1.getTransaction().begin();
                var item = em1.find(Item.class, ITEM_ID);
                assertThat(item.getVersion()).isZero();
                item.setName("New Name");

                try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
                    executorService.submit(() -> {
                        try {
                            try (EntityManager em2 = emf.createEntityManager()) {
                                em2.getTransaction().begin();
                                var item1 = em2.find(Item.class, ITEM_ID);
                                assertThat(item1.getVersion()).isZero();
                                item1.setName("Other Name");
                                em2.getTransaction().commit();
                                // update ITEM set NAME=?, VERION=1 where ID=? and VERSION=0
                            }
                        } catch (Exception ex) {
                            throw new RuntimeException("Concurrent operation failure: " + ex, ex);
                        }
                        return null;
                    }).get();
                }

                assertThrows(OptimisticLockException.class, em1::flush);
                // update ITEM set NAME=?, VERION=1 where ID=? and VERSION=0
            }
        }
    }
}