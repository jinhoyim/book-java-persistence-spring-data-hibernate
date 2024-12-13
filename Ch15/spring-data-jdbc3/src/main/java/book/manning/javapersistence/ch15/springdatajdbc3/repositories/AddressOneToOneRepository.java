package book.manning.javapersistence.ch15.springdatajdbc3.repositories;

import book.manning.javapersistence.ch15.springdatajdbc3.model.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressOneToOneRepository extends CrudRepository<Address, Long> {
}
