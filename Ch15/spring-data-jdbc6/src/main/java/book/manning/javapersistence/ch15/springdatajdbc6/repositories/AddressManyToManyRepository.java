package book.manning.javapersistence.ch15.springdatajdbc6.repositories;

import book.manning.javapersistence.ch15.springdatajdbc6.model.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressManyToManyRepository extends CrudRepository<Address, Long> {
}
