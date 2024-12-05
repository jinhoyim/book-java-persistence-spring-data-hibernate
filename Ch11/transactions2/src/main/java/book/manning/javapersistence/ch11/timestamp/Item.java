package book.manning.javapersistence.ch11.timestamp;

import book.manning.javapersistence.ch11.Constants;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Item {

    @Id
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private Long id;

    @Version
    private LocalDateTime lastUpdate;

    @NotNull
    private String name;

    protected Item() {}

    public Item(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
