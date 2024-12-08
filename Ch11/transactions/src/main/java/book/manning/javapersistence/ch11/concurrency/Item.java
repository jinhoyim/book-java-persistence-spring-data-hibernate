package book.manning.javapersistence.ch11.concurrency;

import book.manning.javapersistence.ch11.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class Item {

    @Id
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private Long id;

    @Version
    private long version;

    @NotNull
    private String name;

    private BigDecimal buyNowPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    protected Item() {}

    public Item(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBuyNowPrice() {
        return buyNowPrice;
    }

    public void setBuyNowPrice(BigDecimal buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", version=" + version +
                ", name='" + name + '\'' +
                ", buyNowPrice=" + buyNowPrice +
                ", category=" + category +
                '}';
    }
}
