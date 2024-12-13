package book.manning.javapersistence.ch15.springdatajdbc6.repositories;

import book.manning.javapersistence.ch15.springdatajdbc6.model.UserAddress;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserAddressManyToManyRepository extends CrudRepository<UserAddress, Long> {

    @Query("SELECT COUNT(*) FROM USERS_ADDRESSES WHERE USER_ID = :userId")
    int countByUserId(@Param("userId") Long userId);
}
