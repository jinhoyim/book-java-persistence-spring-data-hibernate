package book.manning.javapersistence.ch11.concurrency;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Locking extends Versioning {
    @Test
    void pessimisticReadWrite() throws Exception {
        final ConcurrencyTestData testData = storeCategoriesAndItems();
        Long[] CATEGORIES = testData.categories.identifiers;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (Long categoryId : CATEGORIES) {

                    /*
                       For each `Category`, query all `Item` instances in
                       `PESSIMISTIC_READ` lock mode. Hibernate will lock the rows in
                       the database with the SQL query. If possible, wait for 5 seconds if some
                       other transaction already holds a conflicting lock. If the lock can't
                       be obtained, the query throws an exception.
                     */
                List<Item> items =
                        em.createQuery("select i from Item i where i.category.id = :catId", Item.class)
                                .setLockMode(LockModeType.PESSIMISTIC_READ)
//                                .setHint("jakarta.persistence.lock.timeout", 5000) // Oracle 용이고 동기식 처리라 불필요
                                .setParameter("catId", categoryId)
                                .getResultList();

                    /*
                       If the query returns successfully, you know that you hold an exclusive lock
                       on the data and no other transaction can access it with an exclusive lock or
                       modify it until this transaction commits.
                     */
                for (Item item : items)
                    totalPrice = totalPrice.add(item.getBuyNowPrice());

                // Now a concurrent transaction will try to obtain a write lock, it fails because
                // we hold a read lock on the data already. Note that on H2 there actually are no
                // read or write locks, only exclusive locks.
                if (categoryId.equals(testData.categories.getFirstId())) {
                    try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
                        executorService.submit(() -> {
                            try (EntityManager em1 = emf.createEntityManager()) {
                                em1.getTransaction().begin();

                                // The next query's lock attempt must fail at _some_ point, and
                                // we'd like to wait 5 seconds for the lock to become available:
                                //
                                // - H2 fails with a default global lock timeout of 1 second.
                                //
                                // - Oracle supports dynamic lock timeouts, we set it with
                                //   the 'javax.persistence.lock.timeout' hint on the query:
                                //
                                //      no hint == FOR UPDATE
                                //      javax.persistence.lock.timeout 0ms == FOR UPDATE NOWAIT
                                //      javax.persistence.lock.timeout >0ms == FOR UPDATE WAIT [seconds]
                                //
                                em1.unwrap(Session.class).doWork(connection -> {

                                    switch (connection.getMetaData().getDatabaseProductName()) {
                                        // - MySQL also doesn't support query lock timeouts, but you
                                        //   can set a timeout for the whole connection/session.
                                        case "MySQL":
                                            connection.createStatement().execute("set innodb_lock_wait_timeout = 2;");
                                            break;
                                        // - PostgreSQL doesn't timeout and just hangs indefinitely if
                                        //   NOWAIT isn't specified for the query. One possible way to
                                        //   wait for a lock is to set a statement timeout for the whole
                                        //   connection/session.
                                        case "PostgreSQL":
                                            connection.createStatement().execute("set statement_timeout = 2000");
                                            break;
                                    }
                                });

                                // Moving the first item from the first category into the last category
                                // This query should fail as someone else holds a lock on the rows.
                                List<Item> items1 =
                                        em1.createQuery("select i from Item i where i.category.id = :catId", Item.class)
                                                .setParameter("catId", testData.categories.getFirstId())
                                                .setLockMode(LockModeType.PESSIMISTIC_WRITE) // Prevent concurrent access
                                                .setHint("jakarta.persistence.lock.timeout", 2000) // Only works on Oracle...
                                                .getResultList();

                                Category lastCategory = em1.getReference(
                                        Category.class, testData.categories.getLastId()
                                );

                                items1.iterator().next().setCategory(lastCategory);
                                em1.getTransaction().commit();
                            } catch (Exception ex) {
                                // This should fail, as the data is already locked!
                                Session session = em.unwrap(Session.class);
                                session.doWork(connection -> {
                                    switch (connection.getMetaData().getDatabaseProductName()) {
                                        // On MySQL we get a LockTimeoutException
                                        case "MySQL":
                                            assertTrue(ex instanceof LockTimeoutException);
                                            break;
                                        // A statement timeout on PostgreSQL doesn't produce a specific exception
                                        case "PostgreSQL":
                                            assertTrue(ex instanceof PersistenceException);
                                            break;
                                        // On H2 and Oracle we get a PessimisticLockException
                                        default:
                                            assertTrue(ex instanceof PessimisticLockException);
                                            break;
                                    }
                                });
                            }
                            return null;
                        }).get();
                    }
                }
            }

            /*
               Our locks will be released after commit, when the transaction completes.
             */
            em.getTransaction().commit();
            em.close();
            assertEquals(0, totalPrice.compareTo(new BigDecimal("108")));
        }
    }

    @Test
    void findLock() {
        final ConcurrencyTestData testData = storeCategoriesAndItems();
        Long CATEGORY_ID = testData.categories.getFirstId();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Map<String, Object> hints = new HashMap<>();
            hints.put("jakarta.persistence.lock.timeout", 5000);

            Category category =
                    em.find(
                            Category.class,
                            CATEGORY_ID,
                            LockModeType.PESSIMISTIC_WRITE,
                            hints
                    );

            category.setName("New Name");
            em.getTransaction().commit();
        }
    }
}
