package book.manning.javapersistence.ch19.querydsl.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
public class Address {

    @Getter
    private String street;

    @Getter
    private String city;

    @Getter
    private String state;

    @Getter
    private String zipCode;

    protected Address() {}

    public Address(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
