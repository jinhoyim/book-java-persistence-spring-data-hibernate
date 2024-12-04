package book.manning.javapersistence.ch05;

import book.manning.javapersistence.ch05.configuration.SpringDataConfiguration;
import book.manning.javapersistence.ch05.model.Item;
import book.manning.javapersistence.ch05.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringDataConfiguration.class)
class HelloWorldSpringDataJPATest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void storeLoadItem() {

        Item item = new Item();
        item.setName("Some Item");
        itemRepository.save(item);

        List<Item> items = (List<Item>) itemRepository.findAll();

        assertAll(
                () -> assertEquals(1, items.size()),
                () -> assertEquals("Some Item", items.get(0).getName()),
                () -> assertTrue(item.getId() >= 1000)
        );
    }
}
