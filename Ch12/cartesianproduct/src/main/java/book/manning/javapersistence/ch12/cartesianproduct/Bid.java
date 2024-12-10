package book.manning.javapersistence.ch12.cartesianproduct;

import book.manning.javapersistence.ch12.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class Bid {

    @Id
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @NotNull
    private BigDecimal amount;

    protected Bid() {}

    public Bid(Item item, BigDecimal amount) {
        this.item = item;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
