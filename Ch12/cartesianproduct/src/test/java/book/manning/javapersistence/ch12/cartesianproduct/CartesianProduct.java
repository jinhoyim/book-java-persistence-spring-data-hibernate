package book.manning.javapersistence.ch12.cartesianproduct;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CartesianProduct {
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
            item.addImage("foo.jpg");
            item.addImage("bar.jpg");
            item.addImage("baz.jpg");
            em.persist(item);
            itemIds[0] = item.getId();
            for (int i = 1; i <= 3; i++) {
                Bid bid = new Bid(item, new BigDecimal(9 + i));
                item.addBid(bid);
                em.persist(bid);
            }

            item = new Item("Item Two", LocalDate.now().plusDays(1), johndoe);
            item.addImage("a.jpg");
            item.addImage("b.jpg");
            em.persist(item);
            itemIds[1] = item.getId();
            for (int i = 1; i <= 1; i++) {
                Bid bid = new Bid(item, new BigDecimal(2 + i));
                item.addBid(bid);
                em.persist(bid);
            }

            item = new Item("Item Three", LocalDate.now().plusDays(2), janeroe);
            em.persist(item);
            itemIds[2] = item.getId();

            em.getTransaction().commit();
            em.close();

            FetchTestData testData = new FetchTestData();
            testData.items = new TestData(itemIds);
            testData.users = new TestData(userIds);
            return testData;
        }
    }

    @Test
    void fetchCollections() {
        FetchTestData testData = storeTestData();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Long ITEM_ID = testData.items.getFirstId();

            Item item = em.find(Item.class, ITEM_ID);
            // from Item i
            // left join Bid b on i.id=b.item_id
            // left join IMAGE img on i.id=img.Item_id
            // left join USERS s on s.id=i.seller_id
            // where i.id=?

            em.detach(item);

            assertThat(item.getImages().size()).isEqualTo(3);
            assertThat(item.getBids().size()).isEqualTo(3);

            em.getTransaction().commit();
        }
    }
}
