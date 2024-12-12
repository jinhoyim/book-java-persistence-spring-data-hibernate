package book.manning.javapersistence.ch14;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class Bid {

    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    private Long id;

    @NotNull
    private BigDecimal amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    protected Bid() {}

    public Bid(BigDecimal amount, Item item) {
        this.amount = amount;
        this.item = item;
        item.addBid(this);
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Item getItem() {
        return item;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
