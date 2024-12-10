package book.manning.javapersistence.ch12.proxy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUtil;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LazyProxyCollections {

    static EntityManagerFactory emf;

    static {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        emf = Persistence.createEntityManagerFactory("ch12", properties);
    }

    private FetchTestData storeTestData() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Long[] categoryIds = new Long[3];
            Long[] itemIds = new Long[3];
            Long[] userIds = new Long[3];

            User johndoe = new User("johndoe");
            em.persist(johndoe);
            userIds[0] = johndoe.getId();

            User janeroe = new User("janeroe");
            em.persist(janeroe);
            userIds[1] = janeroe.getId();

            User roberdoe = new User("roberdoe");
            em.persist(roberdoe);
            userIds[2] = roberdoe.getId();


            Category category = new Category("Category One");
            em.persist(category);
            categoryIds[0] = category.getId();

            Item item = new Item("Item One", LocalDate.now().plusDays(1), johndoe);
            em.persist(item);
            itemIds[0] = item.getId();
            category.addItem(item);
            item.addCategory(category);
            for (int i = 1; i <= 3; i++) {
                Bid bid = new Bid(item, roberdoe, new BigDecimal(9 + i));
                item.addBid(bid);
                em.persist(bid);
            }

            category = new Category("Category Two");
            em.persist(category);
            categoryIds[1] = category.getId();

            item = new Item("Item Two", LocalDate.now().plusDays(1), johndoe);
            em.persist(item);
            itemIds[1] = item.getId();
            category.addItem(item);
            for (int i = 1; i <= 3; i++) {
                Bid bid = new Bid(item, janeroe, new BigDecimal(9 + i));
                item.addBid(bid);
                em.persist(bid);
            }

            item = new Item("Item Three", LocalDate.now().plusDays(2), janeroe);
            em.persist(item);
            itemIds[2] = item.getId();
            category.addItem(item);
            item.addCategory(category);

            category = new Category("Category Three");
            em.persist(category);
            categoryIds[2] = category.getId();

            em.getTransaction().commit();
            em.close();

            FetchTestData testData = new FetchTestData();
            testData.items = new TestData(itemIds);
            testData.users = new TestData(userIds);
            return testData;
        }
    }

    @Test
    void lazyEntityProxies() {
        FetchTestData testData = storeTestData();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Long ITEM_ID = testData.items.getFirstId();
            Long USER_ID = testData.users.getFirstId();

            {
                Item item = em.getReference(Item.class, ITEM_ID);

                // @Id getter는 프록시를 초기화하지 않음
                assertThat(item.getId()).isEqualTo(ITEM_ID);

                // Item$HibernateProxy$NDqV3awC
                assertThat(item.getClass()).isNotEqualTo(Item.class);

                // deprecated HibernateProxyHelper.getClassWithoutInitializingProxy(item)
                // use Hibernate.getClassLazy(item)
                assertThat(Hibernate.getClassLazy(item)).isEqualTo(Item.class);

                // JPA API
                PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
                assertThat(persistenceUtil.isLoaded(item)).isFalse();
                assertThat(persistenceUtil.isLoaded(item, "seller")).isFalse();

                assertThat(Hibernate.isInitialized(item)).isFalse();
                assertThat(Hibernate.isPropertyInitialized(item, "seller")).isFalse();

                Hibernate.initialize(item);
                // select * from ITEM where ID = ?

                assertThat(Hibernate.isInitialized(item.getSeller())).isFalse();

                Hibernate.initialize(item.getSeller());
            }
            em.clear();
            {
                Item item = em.find(Item.class, ITEM_ID);

                em.detach(item);
                em.detach(item.getSeller());

                PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
                assertThat(persistenceUtil.isLoaded(item)).isTrue();
                assertThat(persistenceUtil.isLoaded(item, "seller")).isFalse();

                assertThat(item.getSeller().getId()).isEqualTo(USER_ID);
                // Throws exception

                Executable executable = () -> item.getSeller().getUsername();
                assertThrows(LazyInitializationException.class, executable);
            }
            em.clear();
            {
                Item item = em.getReference(Item.class, ITEM_ID);
                User user = em.find(User.class, USER_ID);

                Bid bid = new Bid(new BigDecimal("99.00"));
                bid.setItem(item);
                bid.setBidder(user);

                em.persist(bid);
                // insert into BID values (...)

                em.flush();
                em.clear();
                Bid actual = em.find(Bid.class, bid.getId());
                assertThat(actual.getAmount())
                        .usingComparator(BigDecimal::compareTo)
                        .isEqualTo(new BigDecimal("99"));
            }

            em.getTransaction().commit();
        }
    }
}
