package book.manning.javapersistence.ch11.repositories;

import book.manning.javapersistence.ch11.concurrency.Log;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class LogRepositoryImpl implements LogRepositoryCustom{

    private final LogRepository logRepository;

    public LogRepositoryImpl(@Lazy LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String message) {
        logRepository.save(new Log(message));
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addSeparateLogsNotSupported() {
        logRepository.save(new Log("check from not supported 1"));
        if (true) throw new RuntimeException();
        logRepository.save(new Log("check from not supported 2"));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addSeparateLogsSupports() {
        logRepository.save(new Log("check from supports 1"));
        if (true) throw new RuntimeException();
        logRepository.save(new Log("check from supports 2"));
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void showLogs() {
        System.out.println("Current log:");
        logRepository.findAll().forEach(System.out::println);
    }
}
