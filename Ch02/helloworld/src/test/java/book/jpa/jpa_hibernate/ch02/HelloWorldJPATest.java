package book.jpa.jpa_hibernate.ch02;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldJPATest {

    private static EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ch02", properties);
        return emf;
    }

    private static EntityManagerFactory createWithHibernateProperties() {
        Configuration configuration = new Configuration();
        configuration.getProperties().put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        configuration.configure().addAnnotatedClass(Message.class);

        Map<String, String> properties = new HashMap<>();
        Enumeration<?> propertyNames = configuration.getProperties().propertyNames();

        while (propertyNames.hasMoreElements()) {
            String propertyName = (String)propertyNames.nextElement();
            properties.put(propertyName, configuration.getProperties().getProperty(propertyName));
        }

        return Persistence.createEntityManagerFactory("ch02", properties);
    }

    @Test
    void storeLoadMessage() {
        EntityManagerFactory emf = createEntityManagerFactory();

        try {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Message message = new Message();
            message.setText("Hello World!");

            em.persist(message);

            em.getTransaction().commit();

            em.getTransaction().begin();

            List<Message> messages =
                    em.createQuery("select m from Message m", Message.class).getResultList();

            messages.get(messages.size() - 1).setText("Hello World from JPA!");

            em.getTransaction().commit();

            assertAll(
                    () -> assertEquals(1, messages.size()),
                    () -> assertEquals("Hello World from JPA!", messages.get(0).getText())
            );

            em.close();
        } finally {
            emf.close();
        }
    }
}