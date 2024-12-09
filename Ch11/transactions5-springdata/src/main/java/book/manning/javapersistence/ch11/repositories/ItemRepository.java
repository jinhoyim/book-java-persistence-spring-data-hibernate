package book.manning.javapersistence.ch11.repositories;

import book.manning.javapersistence.ch11.concurrency.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    Optional<Item> findByName(String name);
}
