package book.manning.javapersistence.ch15.springdatajdbc5;

import book.manning.javapersistence.ch15.springdatajdbc5.model.Address;
import book.manning.javapersistence.ch15.springdatajdbc5.model.User;
import book.manning.javapersistence.ch15.springdatajdbc5.repositories.AddressOneToManyRepository;
import book.manning.javapersistence.ch15.springdatajdbc5.repositories.UserOneToManyRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAddressOneToManyTest {

	@Autowired
	private UserOneToManyRepository userOneToManyRepository;

	@Autowired
	private AddressOneToManyRepository addressOneToManyRepository;

	private static List<User> users = new ArrayList<>();

	@BeforeAll
	void beforeAll() {
		userOneToManyRepository.saveAll(generateUsers());
	}

	@AfterAll
	void afterAll() {
		userOneToManyRepository.deleteAll();
	}

	@Test
	void userAddressOneToMany() {
		Long userId = users.get(0).getId();
		assertAll(
				() -> assertThat(addressOneToManyRepository.countByUserId(userId))
						.isEqualTo(2),
				() -> assertThat(addressOneToManyRepository.findByUserId(userId))
						.anyMatch(address -> "21, 5th Avenue".equals(address.getStreet())),
				() -> assertThat(userOneToManyRepository.findById(userId).get()
						.getAddresses()
						.size())
						.isEqualTo(2),
				() -> assertThat(userOneToManyRepository.findById(userId).get()
						.getAddresses()
						.iterator()
						.next()
						.getCity()).isEqualTo("New York")
		);
	}

	private static Address generateAddress(String number) {
		return new Address("New York", number + ", 5th Avenue");
	}

	private static List<User> generateUsers() {
		User john = new User("john", LocalDate.of(2020, Month.APRIL, 13));
		john.setEmail("john@somedomain.com");
		john.setLevel(1);
		john.setActive(true);
		john.addAddress(generateAddress("11"));
		john.addAddress(generateAddress("21"));

		User mike = new User("mike", LocalDate.of(2020, Month.JANUARY, 18));
		mike.setEmail("mike@somedomain.com");
		mike.setLevel(3);
		mike.setActive(true);
		mike.addAddress(generateAddress("12"));
		mike.addAddress(generateAddress("22"));

		User james = new User("james", LocalDate.of(2020, Month.MARCH, 11));
		james.setEmail("james@someotherdomain.com");
		james.setLevel(3);
		james.setActive(false);
		james.addAddress(generateAddress("13"));
		james.addAddress(generateAddress("23"));

		User katie = new User("katie", LocalDate.of(2021, Month.JANUARY, 5));
		katie.setEmail("katie@somedomain.com");
		katie.setLevel(5);
		katie.setActive(true);
		katie.addAddress(generateAddress("14"));
		katie.addAddress(generateAddress("24"));

		User beth = new User("beth", LocalDate.of(2020, Month.AUGUST, 3));
		beth.setEmail("beth@somedomain.com");
		beth.setLevel(2);
		beth.setActive(true);
		beth.addAddress(generateAddress("15"));
		beth.addAddress(generateAddress("25"));

		User julius = new User("julius", LocalDate.of(2021, Month.FEBRUARY, 9));
		julius.setEmail("julius@someotherdomain.com");
		julius.setLevel(4);
		julius.setActive(true);
		julius.addAddress(generateAddress("16"));
		julius.addAddress(generateAddress("26"));

		User darren = new User("darren", LocalDate.of(2020, Month.DECEMBER, 11));
		darren.setEmail("darren@somedomain.com");
		darren.setLevel(2);
		darren.setActive(true);
		darren.addAddress(generateAddress("17"));
		darren.addAddress(generateAddress("27"));

		User marion = new User("marion", LocalDate.of(2020, Month.SEPTEMBER, 23));
		marion.setEmail("marion@someotherdomain.com");
		marion.setLevel(2);
		marion.setActive(false);
		marion.addAddress(generateAddress("18"));
		marion.addAddress(generateAddress("28"));

		User stephanie = new User("stephanie", LocalDate.of(2020, Month.JANUARY, 18));
		stephanie.setEmail("stephanie@someotherdomain.com");
		stephanie.setLevel(4);
		stephanie.setActive(true);
		stephanie.addAddress(generateAddress("19"));
		stephanie.addAddress(generateAddress("29"));

		User burk = new User("burk", LocalDate.of(2020, Month.NOVEMBER, 28));
		burk.setEmail("burk@somedomain.com");
		burk.setLevel(1);
		burk.setActive(true);
		burk.addAddress(generateAddress("10"));
		burk.addAddress(generateAddress("20"));

		users.add(john);
		users.add(mike);
		users.add(james);
		users.add(katie);
		users.add(beth);
		users.add(julius);
		users.add(darren);
		users.add(marion);
		users.add(stephanie);
		users.add(burk);

		return users;
	}
}
