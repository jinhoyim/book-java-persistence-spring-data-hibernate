package book.manning.javapersistence.ch14.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public abstract class AbstractGenericDao<T> implements GenericDao<T> {


    protected final SessionFactory sessionFactory;

    protected final Class<T> clazz;

    protected AbstractGenericDao(SessionFactory sessionFactory, Class<T> clazz) {
        this.sessionFactory = sessionFactory;
        this.clazz = clazz;
    }

    @Override
    public T getById(long id) {
        return sessionFactory.getCurrentSession()
                .createSelectionQuery("SELECT i FROM " + clazz.getName() + " i WHERE i.id=:id", clazz)
                .setParameter("id", id).getSingleResult();
    }

    @Override
    public List<T> getAll() {
        return sessionFactory.getCurrentSession()
                .createSelectionQuery("from " + clazz.getName(), clazz).getResultList();
    }

    @Override
    public void insert(T entity) {
        sessionFactory.getCurrentSession().persist(entity);
    }

    @Override
    public void delete(T entity) {
        sessionFactory.getCurrentSession().remove(entity);
    }

    @Override
    public void update(long id, String propertyName, Object propertyValue) {
        CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(clazz);
        Root<T> root = update.from(clazz);
        update.set(root.get(propertyName), propertyValue);
        update.where(cb.equal(root.get("id"), id));
        sessionFactory.getCurrentSession().createMutationQuery(update).executeUpdate();
    }

    @Override
    public List<T> findByProperty(String propertyName, Object propertyValue) {
        CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.select(root);
        query.where(cb.equal(root.get(propertyName), propertyValue));
        return sessionFactory.getCurrentSession().createSelectionQuery(query).getResultList();
    }
}
