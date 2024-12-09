package book.manning.javapersistence.ch11.repositories;

import book.manning.javapersistence.ch11.concurrency.Item;
import book.manning.javapersistence.ch11.concurrency.Log;
import book.manning.javapersistence.ch11.exceptions.DuplicateItemNameException;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final ItemRepository itemRepository;
    private LogRepository logRepository;

    public ItemRepositoryImpl(
            @Lazy ItemRepository itemRepository,
            @Lazy LogRepository logRepository
    ) {
        this.itemRepository = itemRepository;
        this.logRepository = logRepository;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void checkNameDuplicate(String name) {
        long count = itemRepository.findAll()
                .stream()
                .map(Item::getName)
                .filter(n -> n.equals(name))
                .count();
        if (count > 0) {
            throw new DuplicateItemNameException("Item with name " + name + " already exists");
        }
    }

    @Override
    @Transactional
    public void addItem(String name, LocalDate creationDate) {
        logRepository.log("adding item with name " + name);
        checkNameDuplicate(name);
        itemRepository.save(new Item(name, creationDate));
    }

    @Override
    @Transactional(noRollbackFor = DuplicateItemNameException.class)
    public void addItemNoRollback(String name, LocalDate creationDate) {
        logRepository.save(new Log("adding log in method with no rollback for item " + name));
        checkNameDuplicate(name);
        itemRepository.save(new Item(name, creationDate));
    }

    @Override
    @Transactional
    public void addLogs() {
        logRepository.addSeparateLogsNotSupported();
    }

    @Override
    @Transactional
    public void showLogs() {
        logRepository.showLogs();
    }
}
