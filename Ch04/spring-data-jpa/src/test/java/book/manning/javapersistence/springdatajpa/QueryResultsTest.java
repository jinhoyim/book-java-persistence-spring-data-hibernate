package book.manning.javapersistence.springdatajpa;

import book.manning.javapersistence.springdatajpa.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
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

    @Test
    void testFindByAsArrayAndSort() {
        List<Object[]> userList1 = userRepository.findByAsArrayAndSort("ar", Sort.by("username"));
        List<Object[]> userList2 = userRepository.findByAsArrayAndSort("ar", Sort.by("email_length").descending());
        List<Object[]> userList3 = userRepository.findByAsArrayAndSort("ar", JpaSort.unsafe("LENGTH(u.email)"));

        assertAll(
                () -> assertEquals(2, userList1.size()),
                () -> assertEquals("darren", userList1.get(0)[0]),
                () -> assertEquals(21, userList1.get(0)[1]),

                () -> assertEquals(2, userList2.size()),
                () -> assertEquals("marion", userList2.get(0)[0]),
                () -> assertEquals(26, userList2.get(0)[1]),

                () -> assertEquals(2, userList3.size()),
                () -> assertEquals("darren", userList3.get(0)[0]),
                () -> assertEquals(21, userList3.get(0)[1])
        );
    }
}
