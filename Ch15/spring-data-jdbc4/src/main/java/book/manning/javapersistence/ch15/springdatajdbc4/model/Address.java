package book.manning.javapersistence.ch15.springdatajdbc4.model;

public class Address {

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
