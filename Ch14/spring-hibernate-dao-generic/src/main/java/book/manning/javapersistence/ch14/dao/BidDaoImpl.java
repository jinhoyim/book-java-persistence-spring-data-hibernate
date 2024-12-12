package book.manning.javapersistence.ch14.dao;

import book.manning.javapersistence.ch14.Bid;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BidDaoImpl extends AbstractGenericDao<Bid> {

    public BidDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Bid.class);
    }
}
