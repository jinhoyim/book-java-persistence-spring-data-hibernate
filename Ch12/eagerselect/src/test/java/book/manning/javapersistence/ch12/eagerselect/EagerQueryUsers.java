package book.manning.javapersistence.ch12.eagerselect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EagerQueryUsers {
    static EntityManagerFactory emf;

    static {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        emf = Persistence.createEntityManagerFactory("ch12_1", properties);
    }

    private FetchTestData storeTestData() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Long[] itemIds = new Long[3];
            Long[] userIds = new Long[3];

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
            testData.users = new TestData(userIds);
            return testData;
        }
    }

    @Test
    void fetchUsers() {
        storeTestData();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            List<Item> items = em.createQuery("select i from Item i join fetch i.seller", Item.class)
                    .getResultList();
            // select i.*, u.*
            // from ITEM i
            // inner join USERS u on u.ID = i.SELLER_ID
            // where i.ID = ?

            em.close();

            for (Item item : items) {
                assertThat(item.getSeller().getUsername()).isNotNull();
            }
        }

        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Item> query = cb.createQuery(Item.class);

            Root<Item> root = query.from(Item.class);
            root.fetch("seller");
            query.select(root);

            List<Item> items = em.createQuery(query).getResultList();

            em.close();

            for (Item item : items) {
                assertThat(item.getSeller().getUsername()).isNotNull();
            }
        }
    }
}
