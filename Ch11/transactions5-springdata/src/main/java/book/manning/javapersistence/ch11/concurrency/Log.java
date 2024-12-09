package book.manning.javapersistence.ch11.concurrency;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Log {

    private long id;
    private String message;
    private LocalDate date;

    protected Log() {}

    public Log(String message) {
        this.message = message;
        this.date = LocalDate.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Log{" +
                "message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
