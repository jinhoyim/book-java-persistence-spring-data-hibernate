package book.manning.javapersistence.ch02.repositories;

import book.manning.javapersistence.ch02.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
}
