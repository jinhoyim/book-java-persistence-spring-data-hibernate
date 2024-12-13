package book.manning.javapersistence.ch15.springdatajdbc6.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("USERS_ADDRESSES")
public class UserAddress {

    @Id
    private Long id;

    private Long addressId;

    public UserAddress(Long addressId) {
        this.addressId = addressId;
    }

    public Long getAddressId() {
        return addressId;
    }
}
