package book.manning.javapersistence.ch15.springdatajdbc3.repositories;

import book.manning.javapersistence.ch15.springdatajdbc3.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserOneToOneRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
