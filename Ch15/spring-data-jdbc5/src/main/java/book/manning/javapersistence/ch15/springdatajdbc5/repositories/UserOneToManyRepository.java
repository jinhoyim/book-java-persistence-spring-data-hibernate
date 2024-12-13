package book.manning.javapersistence.ch15.springdatajdbc5.repositories;

import book.manning.javapersistence.ch15.springdatajdbc5.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserOneToManyRepository extends CrudRepository<User, Long> {
}
