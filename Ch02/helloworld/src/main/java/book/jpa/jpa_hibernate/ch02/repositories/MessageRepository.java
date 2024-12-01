package book.jpa.jpa_hibernate.ch02.repositories;

import book.jpa.jpa_hibernate.ch02.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
}
