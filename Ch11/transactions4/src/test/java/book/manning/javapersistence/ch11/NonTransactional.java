package book.manning.javapersistence.ch11;

import book.manning.javapersistence.ch11.concurrency.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NonTransactional {
    static EntityManagerFactory emf;

    static {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        emf = Persistence.createEntityManagerFactory("ch11", properties);
    }

    @Test
    void autoCommit() {
        Long ITEM_ID;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Item someItem = new Item("Original Name");
            em.persist(someItem);
            em.getTransaction().commit();
            ITEM_ID = someItem.getId();
        }

        // Reading data in auto-commit mode
        /*
            No transaction is active when we create the `EntityManager`. The
            persistence context is now in a special **unsynchronized** mode, Hibernate
            will not flush automatically at any time.

         */
        try (EntityManager em = emf.createEntityManager()) {
            /*
               You can access the database to read data; this operation will execute a
               `SELECT` statement, sent to the database in auto-commit mode.
             */
            Item item = em.find(Item.class, ITEM_ID);
            item.setName("New Name");

            /*
               Usually Hibernate would flush the persistence context when you execute a
               `Query`. However, because the context is **unsynchronized**,
               flushing will not occur and the query will return the old, original database
               value. Queries with scalar results are not repeatable, you'll see whatever
               values are present in the database and given to Hibernate in the
               `ResultSet`. Note that this isn't a repeatable read either if
               you are in **synchronized** mode.
             */
            String name = em.createQuery("select i.name from Item i where i.id = :id", String.class)
                    .setParameter("id", ITEM_ID).getSingleResult();
            assertThat(name).isEqualTo("Original Name");

            /*
               Retrieving a managed entity instance involves a lookup, during JDBC
               result set marshaling, in the current persistence context. The
               already loaded `Item` instance with the changed name will
               be returned from the persistence context, values from the database
               will be ignored. This is a repeatable read of an entity instance,
               even without a system transaction.
             */
            String newName = em.createQuery("select i from Item i where i.id = :id", Item.class)
                    .setParameter("id", ITEM_ID).getSingleResult().getName();
            assertThat(newName).isEqualTo("New Name");

            /*
               If you try to flush the persistence context manually, to store the new
               `Item#name`, Hibernate will throw a
               `javax.persistence.TransactionRequiredException`. You are
               prevented from executing an `UPDATE` statement in
               **unsynchronized** mode, as you wouldn't be able to roll back the change.
            */
//            em.flush();

            /*
               You can roll back the change you made with the `refresh()`
               method, it loads the current `Item` state from the database
               and overwrites the change you have made in memory.
             */
            em.refresh(item);
            assertThat(item.getName()).isEqualTo("Original Name");
        }

        // Queueing modifications
        try (EntityManager em = emf.createEntityManager()) {
            Item newItem = new Item("New Item");
            /*
               You can call `persist()` to save a transient entity instance with an
               unsynchronized persistence context. Hibernate will only fetch a new identifier
               value, typically by calling a database sequence, and assign it to the instance.
               The instance is now in persistent state in the context but the SQL
               `INSERT` hasn't happened. Note that this is only possible with
               **pre-insert** identifier generators; see <a href="#GeneratorStrategies"/>.
            */
            em.persist(newItem);
            assertThat(newItem.getId()).isNotNull();

            /*
               When you are ready to store the changes, join the persistence context with
               a transaction. Synchronization and flushing will occur as usual, when the
               transaction commits. Hibernate writes all queued operations to the database.
             */
            em.getTransaction().begin();
            if (!em.isJoinedToTransaction()) {
                em.joinTransaction();
            }
            em.getTransaction().commit(); // Flush
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            String name = em.find(Item.class, ITEM_ID).getName();
            assertThat(name).isEqualTo("Original Name");
            Long count = em.createQuery("select count(i) from Item i", Long.class).getSingleResult();
            assertThat(count).isEqualTo(2L);
            em.getTransaction().commit();
        }

        try (EntityManager tempEm = emf.createEntityManager()) {
            Item detachedItem = tempEm.find(Item.class, ITEM_ID);
            tempEm.close();

            detachedItem.setName("New Name");
            try (EntityManager em = emf.createEntityManager()) {
                Item mergedItem = em.merge(detachedItem);
                em.getTransaction().begin();
                em.joinTransaction();
                em.getTransaction().commit(); // Flush
            }
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            String name = em.find(Item.class, ITEM_ID).getName();
            assertThat(name).isEqualTo("New Name");
            em.getTransaction().commit();
        }

        try (EntityManager em = emf.createEntityManager()) {
            Item item = em.find(Item.class, ITEM_ID);
            em.remove(item);

            em.getTransaction().begin();
            em.joinTransaction();
            em.getTransaction().commit();
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Object count = em.createQuery("select count(i) from Item i").getSingleResult();
            assertThat(count).isEqualTo(1L);
        }
    }
}