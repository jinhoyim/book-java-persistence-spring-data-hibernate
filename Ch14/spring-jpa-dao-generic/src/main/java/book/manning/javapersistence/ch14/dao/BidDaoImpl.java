package book.manning.javapersistence.ch14.dao;

import book.manning.javapersistence.ch14.Bid;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BidDaoImpl extends AbstractGenericDao<Bid> {

    public BidDaoImpl() {
        setClazz(Bid.class);
    }
}
