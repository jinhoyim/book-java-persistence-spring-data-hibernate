package book.manning.javapersistence.ch14.dao;

import java.util.List;

public interface GenericDao<T> {
    T getById(long id);

    List<T> getAll();

    void insert(T entity);

    void delete(T entity);

    void update(long id, String propertyName, Object propertyValue);

    List<T> findByProperty(String propertyName, Object propertyValue);
}
