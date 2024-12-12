package book.manning.javapersistence.ch14.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(clazz);
        Root<T> root = update.from(clazz);
        update.set(root.get(propertyName), propertyValue);
        update.where(cb.equal(root.get("id"), id));
        em.createQuery(update).executeUpdate();
    }

    @Override
    public List<T> findByProperty(String propertyName, Object propertyValue) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.select(root);
        query.where(cb.equal(root.get(propertyName), propertyValue));
        return em.createQuery(query).getResultList();
    }
}
