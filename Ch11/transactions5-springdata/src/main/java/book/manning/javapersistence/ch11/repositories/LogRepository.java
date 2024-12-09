package book.manning.javapersistence.ch11.repositories;

import book.manning.javapersistence.ch11.concurrency.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long>, LogRepositoryCustom {
}
