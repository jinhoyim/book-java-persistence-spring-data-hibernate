package book.manning.javapersistence.ch15.springdatajdbc5.repositories;

import book.manning.javapersistence.ch15.springdatajdbc5.model.Address;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressOneToManyRepository extends CrudRepository<Address, Long> {

    @Query("SELECT COUNT(*) FROM ADDRESSES WHERE USER_ID = :userId")
    int countByUserId(@Param("userId") Long userId);

    @Query("SELECT * FROM ADDRESSES WHERE USER_ID = :userId")
    List<Address> findByUserId(@Param("userId") Long userId);
}
