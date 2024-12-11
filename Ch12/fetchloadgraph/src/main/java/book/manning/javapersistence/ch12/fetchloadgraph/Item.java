package book.manning.javapersistence.ch12.fetchloadgraph;

import book.manning.javapersistence.ch12.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph// default "Item" entity graph
@NamedEntityGraph(
        name = "ItemSeller",
        attributeNodes = {
                @NamedAttributeNode("seller")
        }
)
@Entity
public class Item {

    @Id
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private LocalDate auctionEnd;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;

    @OneToMany(mappedBy = "item")
    private Set<Bid> bids = new HashSet<>();

    @ElementCollection
    private Set<String> images = new HashSet<>();

    protected Item() {}

    public Item(String name, LocalDate auctionEnd, User seller) {
        this.name = name;
        this.auctionEnd = auctionEnd;
        this.seller = seller;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getAuctionEnd() {
        return auctionEnd;
    }

    public User getSeller() {
        return seller;
    }

    public Set<Bid> getBids() {
        return Collections.unmodifiableSet(bids);
    }

    public void addBid(Bid bid) {
        bids.add(bid);
    }
}
