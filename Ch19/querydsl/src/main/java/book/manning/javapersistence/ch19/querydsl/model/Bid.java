package book.manning.javapersistence.ch19.querydsl.model;

import book.manning.javapersistence.ch19.Constants;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
public class Bid {

    @Id
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private Long id;

    @Getter
    private BigDecimal amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Getter
    private User user;

    protected Bid() {}

    public Bid(BigDecimal amount) {
        this.amount = amount;
    }

    public void setUser(User user) {
        this.user = user;
        user.addBid(this);
    }
}
