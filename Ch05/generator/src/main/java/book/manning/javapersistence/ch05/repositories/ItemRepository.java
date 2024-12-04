package book.manning.javapersistence.ch05.repositories;

import book.manning.javapersistence.ch05.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Long> {
}
