package book.manning.javapersistence.ch15.springdatajdbc6.model;

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

    public Long getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }
}
