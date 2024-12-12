package book.manning.javapersistence.ch14.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public abstract class AbstractGenericDao<T> implements GenericDao<T> {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    protected EntityManager em;

    private Class<T> clazz;

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T getById(long id) {
        return em.createQuery("SELECT i FROM " + clazz.getName() + " i WHERE i.id=:id", clazz)
                .setParameter("id", id).getSingleResult();
    }

    @Override
    public List<T> getAll() {
        return em.createQuery("from " + clazz.getName(), clazz).getResultList();
    }

    @Override
    public void insert(T entity) {
        em.persist(entity);
    }

    @Override
    public void delete(T entity) {
        em.remove(entity);
    }

    @Override
    public void update(long id, String propertyName, Object propertyValue) {
        em.createQuery("UPDATE " + clazz.getName() + " e SET e." + propertyName + " = :propertyValue WHERE e.id = :id")
                .setParameter("propertyValue", propertyValue)
                .setParameter("id", id).executeUpdate();
    }

    @Override
    public List<T> findByProperty(String propertyName, Object propertyValue) {
        return em.createQuery("SELECT e FROM " + clazz.getName() + " e WHERE e." + propertyName + " = :propertyValue", clazz)
                .setParameter("propertyValue", propertyValue).getResultList();
    }
}
