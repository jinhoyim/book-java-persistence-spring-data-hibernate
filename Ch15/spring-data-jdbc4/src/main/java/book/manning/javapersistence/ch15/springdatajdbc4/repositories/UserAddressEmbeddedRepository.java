package book.manning.javapersistence.ch15.springdatajdbc4.repositories;

import book.manning.javapersistence.ch15.springdatajdbc4.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserAddressEmbeddedRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
