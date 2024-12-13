package book.manning.javapersistence.ch15.springdatajdbc6.repositories;

import book.manning.javapersistence.ch15.springdatajdbc6.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserManyToManyRepository extends CrudRepository<User, Long> {
}
