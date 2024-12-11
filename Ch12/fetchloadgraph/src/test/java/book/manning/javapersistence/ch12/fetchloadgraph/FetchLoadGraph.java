package book.manning.javapersistence.ch12.fetchloadgraph;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FetchLoadGraph {

    static EntityManagerFactory emf;

    static {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        emf = Persistence.createEntityManagerFactory("ch12", properties);
    }

    private FetchTestData storeTestData() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Long[] itemIds = new Long[3];
            Long[] userIds = new Long[3];
            Long[] bidIds = new Long[3];

            User johndoe = new User("johndoe");
            em.persist(johndoe);
            userIds[0] = johndoe.getId();

            User janeroe = new User("janeroe");
            em.persist(janeroe);
            userIds[1] = janeroe.getId();

            User robertdoe = new User("robertdoe");
            em.persist(robertdoe);
            userIds[2] = robertdoe.getId();

            Item item = new Item("Item One", LocalDate.now().plusDays(1), johndoe);
            em.persist(item);
            itemIds[0] = item.getId();
            for (int i = 1; i <= 3; i++) {
                Bid bid = new Bid(item, robertdoe, new BigDecimal(9 + i));
                item.addBid(bid);
                em.persist(bid);
                bidIds[i - 1] = bid.getId();
            }

            item = new Item("Item Two", LocalDate.now().plusDays(1), johndoe);
            em.persist(item);
            itemIds[1] = item.getId();
            for (int i = 1; i <= 1; i++) {
                Bid bid = new Bid(item, janeroe, new BigDecimal(2 + i));
                item.addBid(bid);
                em.persist(bid);
            }

            item = new Item("Item Three", LocalDate.now().plusDays(2), janeroe);
            em.persist(item);
            itemIds[2] = item.getId();
            for (int i = 1; i <= 1; i++) {
                Bid bid = new Bid(item, johndoe, new BigDecimal(3 + i));
                item.addBid(bid);
                em.persist(bid);
            }

            em.getTransaction().commit();
            em.close();

            FetchTestData testData = new FetchTestData();
            testData.items = new TestData(itemIds);
            testData.bids = new TestData(bidIds);
            testData.users = new TestData(userIds);
            return testData;
        }
    }

    @Test
    void loadItem() {
        FetchTestData testData = storeTestData();
        Long ITEM_ID = testData.items.getFirstId();
        PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Map<String, Object> properties = new HashMap<>();
            properties.put(
                    "jakarta.persistence.loadgraph",
                    em.getEntityGraph(Item.class.getSimpleName()) // default "Item" graph
            );

            Item item = em.find(Item.class, ITEM_ID, properties);
            // select * from ITEM where ID = ?

            assertThat(persistenceUtil.isLoaded(item)).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "name")).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "auctionEnd")).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "seller")).isFalse();
            assertThat(persistenceUtil.isLoaded(item, "bids")).isFalse();

            em.getTransaction().commit();
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            EntityGraph<Item> itemGraph = em.createEntityGraph(Item.class);

            Map<String, Object> properties = new HashMap<>();
            properties.put("jakarta.persistence.loadgraph", itemGraph);

            Item item = em.find(Item.class, ITEM_ID, properties);

            assertThat(persistenceUtil.isLoaded(item)).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "name")).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "auctionEnd")).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "seller")).isFalse();
            assertThat(persistenceUtil.isLoaded(item, "bids")).isFalse();

            em.getTransaction().commit();
        }
    }

    @Test
    void loadItemSeller() {
        FetchTestData testData = storeTestData();
        Long ITEM_ID = testData.items.getFirstId();
        PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Map<String, Object> properties = new HashMap<>();
            properties.put(
                    "jakarta.persistence.loadgraph",
                    em.getEntityGraph("ItemSeller")
            );

            Item item = em.find(Item.class, ITEM_ID, properties);
            // from ITEM i inner join USERS u

            assertThat(persistenceUtil.isLoaded(item)).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "name")).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "auctionEnd")).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "seller")).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "bids")).isFalse();

            em.getTransaction().commit();
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            EntityGraph<Item> itemGraph = em.createEntityGraph(Item.class);
            itemGraph.addAttributeNodes(Item_.seller);

            Map<String, Object> properties = new HashMap<>();
            properties.put("jakarta.persistence.loadgraph", itemGraph);

            Item item = em.find(Item.class, ITEM_ID, properties);
            // from ITEM i inner join USERS u

            assertThat(persistenceUtil.isLoaded(item)).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "name")).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "auctionEnd")).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "seller")).isTrue();
            assertThat(persistenceUtil.isLoaded(item, "bids")).isFalse();

            em.getTransaction().commit();
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            EntityGraph<Item> itemGraph = em.createEntityGraph(Item.class);
            itemGraph.addAttributeNodes("seller");

            List<Item> items = em.createQuery("select i from Item i", Item.class)
                    .setHint("jakarta.persistence.loadgraph", itemGraph)
                    .getResultList();

            assertThat(items.size()).isEqualTo(3);

            for (Item item : items) {
                assertThat(persistenceUtil.isLoaded(item)).isTrue();
                assertThat(persistenceUtil.isLoaded(item, "name")).isTrue();
                assertThat(persistenceUtil.isLoaded(item, "auctionEnd")).isTrue();
                assertThat(persistenceUtil.isLoaded(item, "seller")).isTrue();
                assertThat(persistenceUtil.isLoaded(item, "bids")).isFalse();
            }

            em.getTransaction().commit();
        }
    }

    @Test
    void loadBidBidderItem() {
        FetchTestData testData = storeTestData();
        Long BID_ID = testData.bids.getFirstId();
        PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Map<String, Object> properties = new HashMap<>();
            properties.put(
                    "jakarta.persistence.loadgraph",
                    em.getEntityGraph("BidBidderItem")
            );

            Bid bid = em.find(Bid.class, BID_ID, properties);

            assertThat(persistenceUtil.isLoaded(bid)).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "amount")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "bidder")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "item")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem(), "name")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem(), "seller")).isFalse();

            em.getTransaction().commit();
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            EntityGraph<Bid> bidGraph = em.createEntityGraph(Bid.class);
            bidGraph.addAttributeNodes("bidder", "item");

            Map<String, Object> properties = new HashMap<>();
            properties.put("jakarta.persistence.loadgraph", bidGraph);

            Bid bid = em.find(Bid.class, BID_ID, properties);

            assertThat(persistenceUtil.isLoaded(bid)).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "amount")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "bidder")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "item")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem(), "name")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem(), "seller")).isFalse();

            em.getTransaction().commit();
        }
    }

    @Test
    void loadBidBidderItemSellerBids() {
        FetchTestData testData = storeTestData();
        Long BID_ID = testData.bids.getFirstId();
        PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Map<String, Object> properties = new HashMap<>();
            properties.put(
                    "jakarta.persistence.loadgraph",
                    em.getEntityGraph("BidBidderItemSellerBids")
            );

            Bid bid = em.find(Bid.class, BID_ID, properties);

            assertThat(persistenceUtil.isLoaded(bid)).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "amount")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "bidder")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "item")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem(), "name")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem(), "seller")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem().getSeller(), "username")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem(), "bids")).isTrue();

            em.getTransaction().commit();
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            EntityGraph<Bid> bidGraph = em.createEntityGraph(Bid.class);
            bidGraph.addAttributeNodes(Bid_.BIDDER, Bid_.ITEM);
            Subgraph<Item> itemGraph = bidGraph.addSubgraph(Bid_.ITEM);
            itemGraph.addAttributeNodes(Item_.SELLER, Item_.BIDS);

            Map<String, Object> properties = new HashMap<>();
            properties.put("jakarta.persistence.loadgraph", bidGraph);

            Bid bid = em.find(Bid.class, BID_ID, properties);

            assertThat(persistenceUtil.isLoaded(bid)).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "amount")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "bidder")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid, "item")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem(), "name")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem(), "seller")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem().getSeller(), "username")).isTrue();
            assertThat(persistenceUtil.isLoaded(bid.getItem(), "bids")).isTrue();

            em.getTransaction().commit();
        }
    }
}
