package book.manning.javapersistence.ch15.springdatajdbc;

import book.manning.javapersistence.ch15.springdatajdbc.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FindUsersUsingQueriesTest extends SpringDataJdbcApplicationTests {

    @Test
    void testFindAll() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(10);
    }
}
