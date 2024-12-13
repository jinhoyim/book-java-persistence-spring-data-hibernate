package book.manning.javapersistence.ch15.springdatajdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("USERS")
public class User {

    @Id
    @Column("ID")
    private Long id;

    @Column("USERNAME")
    private String username;

    @Column("REGISTRATION_DATE")
    private LocalDate registrationDate;

    protected User() {}

    public User(String username, LocalDate registrationDate) {
        this.username = username;
        this.registrationDate = registrationDate;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
