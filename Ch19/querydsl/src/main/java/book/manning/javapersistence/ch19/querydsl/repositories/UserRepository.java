package book.manning.javapersistence.ch19.querydsl.repositories;

import book.manning.javapersistence.ch19.querydsl.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
