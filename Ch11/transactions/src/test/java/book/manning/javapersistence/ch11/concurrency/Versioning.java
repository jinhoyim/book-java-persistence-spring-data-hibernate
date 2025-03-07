package book.manning.javapersistence.ch11.concurrency;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Versioning {

    static EntityManagerFactory emf;

    static {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        emf = Persistence.createEntityManagerFactory("ch11", properties);
    }

    ConcurrencyTestData storeCategoriesAndItems() {
        ConcurrencyTestData testData = new ConcurrencyTestData();
        testData.categories = new TestData(new Long[3]);
        testData.items = new TestData(new Long[5]);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            for (int i = 1; i <= testData.categories.identifiers.length; i++) {
                Category category = new Category();
                category.setName("Category: " + i);
                em.persist(category);
                testData.categories.identifiers[i - 1] = category.getId();
                for (int j = 1; j <= testData.categories.identifiers.length; j++) {
                    var item = new Item("Item " + j);
                    item.setCategory(category);
                    item.setBuyNowPrice(new BigDecimal(10 + j));
                    em.persist(item);
                    testData.items.identifiers[(i - 1) + (j - 1)] = item.getId();
                }
            }
            em.getTransaction().commit();
        }
        return testData;
    }

    private TestData storeItemAndBids() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Long[] ids = new Long[1];
            Item item = new Item("Some Item");
            em.persist(item);
            ids[0] = item.getId();
            for (int i = 1; i <= 3; i++) {
                Bid bid = new Bid(new BigDecimal(10 + i), item);
                em.persist(bid);
            }
            em.getTransaction().commit();
            return new TestData(ids);
        }
    }

    private Bid queryHighestBid(EntityManager em, Item item) {
        try {
            return (Bid) em.createQuery(
                    "select b from Bid b where b.item = :itm order by b.amount desc")
                    .setParameter("itm", item)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
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

    @Test
    void manualVersionChecking() throws ExecutionException, InterruptedException {
        final ConcurrencyTestData testData = storeCategoriesAndItems();
        Long[] CATEGORIES = testData.categories.identifiers;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            BigDecimal totalPrice = BigDecimal.ZERO;
            for (Long categoryId : CATEGORIES) {
                // .setLockMode(LockModeType.OPTIMISTIC) 를 설정하여 아이템마다 버전을 조회하는 쿼리를 수행한다.
                /*
                   For each <code>Category</code>, query all <code>Item</code> instances with
                   an <code>OPTIMISTIC</code> lock mode. Hibernate now knows it has to
                   check each <code>Item</code> at flush time.
                 */
                List<Item> items =
                        em.createQuery("select i from Item i where i.category.id = :catId", Item.class)
                                .setLockMode(LockModeType.OPTIMISTIC)
                                .setParameter("catId", categoryId)
                                .getResultList();

                for (Item item : items) {
                    System.out.println(item.toString());
                    totalPrice = totalPrice.add(item.getBuyNowPrice());
                }

                if (categoryId.equals(testData.categories.getFirstId())) {
                    try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
                        executorService.submit(() -> {
                            try (EntityManager em1 = emf.createEntityManager()) {
                                em1.getTransaction().begin();

                                List<Item> items1 = em1.createQuery("select i from Item i where i.category.id = :catId", Item.class)
                                        .setParameter("catId", testData.categories.getFirstId())
                                        .getResultList();

                                Category lastCategory = em1.getReference(
                                        Category.class, testData.categories.getLastId()
                                );

                                Item next = items1.iterator().next();
                                System.out.println(next.toString());
                                next.setCategory(lastCategory);

                                em1.getTransaction().commit();
                            } catch (Exception ex) {
                                throw new RuntimeException("Concurrent operation failure: " + ex, ex);
                            }
                            return null;
                        }).get();
                    }
                }
            }
            /*
               For each <code>Item</code> loaded earlier with the locking query, Hibernate will
               now execute a <code>SELECT</code> during flushing. It checks if the database
               version of each <code>ITEM</code> row is still the same as when it was loaded
               earlier. If any <code>ITEM</code> row has a different version, or the row doesn't
               exist anymore, an <code>OptimisticLockException</code> will be thrown.
             */
            em.getTransaction().commit();
            em.close();

            assertThat(totalPrice.toString()).isEqualTo("108.00");
        }
    }

    @Test
    void forceIncrement() throws ExecutionException, InterruptedException {
        TestData testData = storeItemAndBids();
        Long ITEM_ID = testData.getFirstId();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            /*
               The <code>find()</code> method accepts a <code>LockModeType</code>. The
               <code>OPTIMISTIC_FORCE_INCREMENT</code> mode tells Hibernate that the version
               of the retrieved <code>Item</code> should be incremented after loading,
               even if it's never modified in the unit of work.
               Item 버전이 작업 단위(커밋될 때)에서 수정되지 않더라도 로드 후 증가해야 한다고 알려준다.
            */
            Item item = em.find(Item.class, ITEM_ID, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            Bid highestBid = queryHighestBid(em, item);

            // Now a concurrent transaction will place a bid for this item, and
            // succeed because the first commit wins!
            try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
                executorService.submit(() -> {
                    try (EntityManager em1 = emf.createEntityManager()) {
                        em1.getTransaction().begin();

                        Item item1 = em1.find(
                                Item.class,
                                testData.getFirstId(),
                                LockModeType.OPTIMISTIC_FORCE_INCREMENT
                        );
                        Bid highestBid1 = queryHighestBid(em1, item1);

                        Bid newBid = new Bid(
                                new BigDecimal("44.44"),
                                item1,
                                highestBid1
                        );
                        em1.persist(newBid);

                        em1.getTransaction().commit();
                    } catch (Exception ex) {
                        throw new RuntimeException("Concurrent operation failure: " + ex, ex);
                    }
                    return null;
                }).get();
            }

            /*
               The code persists a new <code>Bid</code> instance; this does not affect
               any values of the <code>Item</code> instance. A new row will be inserted
               into the <code>BID</code> table. Hibernate would not detect concurrently
               made bids at all without a forced version increment of the
               <code>Item</code>. We also use a checked exception to validate the
               new bid amount; it must be greater than the currently highest bid.
            */
            Bid newBid = new Bid(
                    new BigDecimal("45.45"),
                    item,
                    highestBid
            );
            em.persist(newBid);

            /*
                When flushing the persistence context, Hibernate will execute an
                <code>INSERT</code> for the new <code>Bid</code> and force an
                <code>UPDATE</code> of the <code>Item</code> with a version check.
                If someone modified the <code>Item</code> concurrently, or placed a
                <code>Bid</code> concurrently with this procedure, Hibernate throws
                an exception.
                입찰 처리 중 다른 트랜잭션에서 새로운 입찰이 등록되면서 Item 버전이 업데이트되어
                처리중인 입찰은 버전 충돌로 등록에 실패한다.
            */
            assertThrows(RollbackException.class, () -> em.getTransaction().commit());
        }
    }
}