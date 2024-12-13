package book.manning.javapersistence.ch15.springdatajdbc3.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("ADDRESSES")
public class Address {

    @Id
    private Long userId;

    private String street;

    private String city;

    public Address(String city, String street) {
        this.city = city;
        this.street = street;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }
}
