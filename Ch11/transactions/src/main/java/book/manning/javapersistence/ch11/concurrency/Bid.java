package book.manning.javapersistence.ch11.concurrency;

import book.manning.javapersistence.ch11.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class Bid {

    @Id
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private Long id;

    @NotNull
    private BigDecimal amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Item item;

    protected Bid() {}

    public Bid(BigDecimal amount, Item item) {
        this.amount = amount;
        this.item = item;
    }

    public Bid(BigDecimal amount, Item item, Bid lastBid) {
        if (lastBid != null &&
        amount.compareTo(lastBid.getAmount()) < 1) {
            throw new InvalidBidException(
                    "Bid amount '" +
                            amount +
                            " too low, last bid was: " +
                            lastBid.getAmount()
            );
        }
        this.amount = amount;
        this.item = item;
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
}
