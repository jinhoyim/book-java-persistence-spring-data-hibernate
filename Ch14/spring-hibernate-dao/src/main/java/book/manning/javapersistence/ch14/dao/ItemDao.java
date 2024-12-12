package book.manning.javapersistence.ch14.dao;

import book.manning.javapersistence.ch14.Item;

import java.util.List;

public interface ItemDao {
    Item getById(long id);

    List<Item> getAll();

    void insert(Item item);

    void update(long id, String name);

    void delete(Item item);

    Item findByName(String name);
}
