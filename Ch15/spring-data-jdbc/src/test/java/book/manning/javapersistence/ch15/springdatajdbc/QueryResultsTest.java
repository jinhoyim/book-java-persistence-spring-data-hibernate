package book.manning.javapersistence.ch15.springdatajdbc;

import book.manning.javapersistence.ch15.springdatajdbc.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class QueryResultsTest extends SpringDataJdbcApplicationTests {

    @Test
    void testStreamable() {
        try (Stream<User> result = userRepository.findByEmailContaining("someother")
                .and(userRepository.findByLevel(2))
                .stream().distinct()) {
            assertThat(result).hasSize(6);
        }
    }

    @Test
    void testNumberOfUsersByActivity() {
        int active = userRepository.findNumberOfUsersByActivity(true);
        int inactive = userRepository.findNumberOfUsersByActivity(false);

        assertAll(
                () -> assertThat(active).isEqualTo(8),
                () -> assertThat(inactive).isEqualTo(2)
        );
    }

    @Test
    void testUsersByLevelAndActivity() {
        List<User> userList1 = userRepository.findByLevelAndActive(1, true);
        List<User> userList2 = userRepository.findByLevelAndActive(2, true);
        List<User> userList3 = userRepository.findByLevelAndActive(2, false);

        assertAll(
                () -> assertThat(userList1).hasSize(2),
                () -> assertThat(userList2).hasSize(2),
                () -> assertThat(userList3).hasSize(1)
        );
    }
}
