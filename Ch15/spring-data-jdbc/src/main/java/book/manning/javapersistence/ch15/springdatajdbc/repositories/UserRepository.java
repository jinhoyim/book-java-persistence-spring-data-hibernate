package book.manning.javapersistence.ch15.springdatajdbc.repositories;

import book.manning.javapersistence.ch15.springdatajdbc.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll();
}
