package book.manning.javapersistence.ch11.concurrency;

import book.manning.javapersistence.ch11.configuration.SpringDataConfiguration;
import book.manning.javapersistence.ch11.exceptions.DuplicateItemNameException;
import book.manning.javapersistence.ch11.repositories.ItemRepository;
import book.manning.javapersistence.ch11.repositories.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.IllegalTransactionStateException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
class TransactionPropagationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private LogRepository logRepository;

    @BeforeEach
    void clean() {
        itemRepository.deleteAllInBatch();
        logRepository.deleteAllInBatch();
    }

    @Test
    void notSupported() {
        // executing in transaction:
        // addLogs is starting transaction, but addSeparateLogsNotSupported() suspends it

        assertAll(

                () -> assertThrows(RuntimeException.class, () -> itemRepository.addLogs()),
                () -> assertThat(logRepository.findAll().size()).isOne(),
                () -> assertThat(logRepository.findAll().get(0).getMessage()).isEqualTo("check from not supported 1")
        );

        // no transaction - first record is added in the log even after exception
        logRepository.showLogs();
    }

    @Test
    void supports() {
        // executing without transaction:
        // addSeparateLogsSupports is working with no transaction
        assertAll(
                () -> assertThrows(RuntimeException.class, () -> logRepository.addSeparateLogsSupports()),
                () -> assertThat(logRepository.findAll().size()).isOne(),
                () -> assertThat(logRepository.findAll().get(0).getMessage()).isEqualTo("check from supports 1")
        );

        // no transaction - first record is added in the log even after exception
        logRepository.showLogs();
    }

    @Test
    void mandatory() {
        // get exception because checkNameDuplicate can be executed only in transaction
        Executable executable = () -> itemRepository.checkNameDuplicate("Item1");
        IllegalTransactionStateException ex = assertThrows(IllegalTransactionStateException.class, executable);
        assertThat(ex.getMessage())
                .isEqualTo("No existing transaction found for transaction marked with propagation 'mandatory'");
    }

    @Test
    void never() {
        itemRepository.addItem("Item1", LocalDate.of(2022, 5, 1));
        // it's safe to call showLogs from no transaction
        logRepository.showLogs();

        // but prohibited to execute from transaction
        Executable executable = () -> itemRepository.showLogs();
        IllegalTransactionStateException ex = assertThrows(IllegalTransactionStateException.class, executable);
        assertThat(ex.getMessage())
                .isEqualTo("Existing transaction found for transaction marked with propagation 'never'");
    }

    @Test
    void requiresNew() {
        // requires new - log message is persisted in the logs even after exception
        // because it was added in the separate transaction
        itemRepository.addItem("Item1", LocalDate.of(2022, 5, 1));
        itemRepository.addItem("Item2", LocalDate.of(2022, 3, 1));
        itemRepository.addItem("Item3", LocalDate.of(2022, 1, 1));

        Executable executable = () -> itemRepository.addItem("Item2", LocalDate.of(2016, 3, 1));
        DuplicateItemNameException ex = assertThrows(DuplicateItemNameException.class, executable);
        assertAll(
                () -> assertThat(ex.getMessage()).isEqualTo("Item with name Item2 already exists"),
                () -> assertThat(logRepository.findAll()).hasSize(4),
                () -> assertThat(itemRepository.findAll()).hasSize(3)
        );

        System.out.println("Logs: ");
        logRepository.findAll().forEach(System.out::println);

        System.out.println("List of added items: ");
        itemRepository.findAll().forEach(System.out::println);
    }

    @Test
    void noRollback() {
        // no rollback - log message is persisted in the logs even after exception
        // because transaction was not rolled back
        itemRepository.addItemNoRollback("Item1", LocalDate.of(2022, 5, 1));
        itemRepository.addItemNoRollback("Item2", LocalDate.of(2022, 3, 1));
        itemRepository.addItemNoRollback("Item3", LocalDate.of(2022, 1, 1));

        Executable executable = () -> itemRepository.addItemNoRollback("Item2", LocalDate.of(2016, 3, 1));
        DuplicateItemNameException ex = assertThrows(DuplicateItemNameException.class, executable);
        assertAll(
                () -> assertThat(ex.getMessage()).isEqualTo("Item with name Item2 already exists"),
                () -> assertThat(logRepository.findAll().size()).isEqualTo(4),
                () -> assertThat(itemRepository.findAll().size()).isEqualTo(3)
        );

        System.out.println("Logs: ");
        logRepository.findAll().forEach(System.out::println);

        System.out.println("List of added items: ");
        itemRepository.findAll().forEach(System.out::println);
    }
}
