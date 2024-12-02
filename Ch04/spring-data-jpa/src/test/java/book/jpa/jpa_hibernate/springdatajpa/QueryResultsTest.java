package book.jpa.jpa_hibernate.springdatajpa;

import book.jpa.jpa_hibernate.springdatajpa.model.User;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryResultsTest extends SpringDataJpaApplicationTests {

    @Test
    void testStreamable() {
        try (Stream<User> results = userRepository.findByEmailContaining("someother")
                .and(userRepository.findByLevel(2))
                .stream()
                .distinct()) {
            assertEquals(6, results.count());
        }
    }
}
