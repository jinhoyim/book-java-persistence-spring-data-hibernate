package book.manning.javapersistence.ch15.springdatajdbc5.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("ADDRESSES")
public class Address {

    @Id
    private Long id;

    private String street;

    private String city;

    public Address(String city, String street) {
        this.city = city;
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }
}
