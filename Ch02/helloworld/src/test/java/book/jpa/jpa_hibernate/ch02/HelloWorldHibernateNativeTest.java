package book.jpa.jpa_hibernate.ch02;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloWorldHibernateNativeTest {

    private static SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.getProperties().put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        configuration.configure().addAnnotatedClass(Message.class);
        ServiceRegistry serviceRegistry = new
                StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Test
    void storeLoadMessage() {
        try (SessionFactory sessionFactory = createSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();
            Message message = new Message();
            message.setText("Hello World from Hibernate!");
            session.persist(message);
            session.getTransaction().commit();

            session.beginTransaction();
            CriteriaQuery<Message> query = session.getCriteriaBuilder().createQuery(Message.class);
            query.from(Message.class);
            List<Message> messages = session.createQuery(query).getResultList();
            session.getTransaction().commit();

            assertAll(
                    () -> assertEquals(1, messages.size()),
                    () -> assertEquals("Hello World from Hibernate!", messages.get(0).getText())
            );
        }
    }
}
