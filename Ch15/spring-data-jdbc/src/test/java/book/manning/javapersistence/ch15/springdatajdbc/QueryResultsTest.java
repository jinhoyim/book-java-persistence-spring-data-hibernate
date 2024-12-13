package book.manning.javapersistence.ch15.springdatajdbc;

import book.manning.javapersistence.ch15.springdatajdbc.model.User;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class QueryResultsTest extends SpringDataJdbcApplicationTests {

    @Test
    void testStreamable() {
        try (Stream<User> result = userRepository.findByEmailContaining("someother")
                .and(userRepository.findByLevel(2))
                .stream().distinct()) {
            assertThat(result).hasSize(6);
        }
    }
}
