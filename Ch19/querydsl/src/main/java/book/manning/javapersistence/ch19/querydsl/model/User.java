package book.manning.javapersistence.ch19.querydsl.model;

import book.manning.javapersistence.ch19.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    @Getter
    private Long id;

    @Getter
    private String username;

    @Getter
    private String firstName;

    @Getter
    private String lastName;

    @Getter
    @Setter
    private LocalDate registrationDate;

    @Embedded
    @Getter
    @Setter
    private Address address;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private int level;

    @Getter
    @Setter
    private boolean active;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Bid> bids = new HashSet<>();

    protected User() {}

    public User(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addBid(Bid bid) {
        bids.add(bid);
    }

    public Set<Bid> getBids() {
        return Collections.unmodifiableSet(bids);
    }
}
