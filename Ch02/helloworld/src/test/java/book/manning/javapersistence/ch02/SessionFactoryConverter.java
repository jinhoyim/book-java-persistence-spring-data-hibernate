package book.manning.javapersistence.ch02;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;

class SessionFactoryConverter {

    static SessionFactory from(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(SessionFactory.class);
    }
}
