package book.manning.javapersistence.ch14.dao;

import book.manning.javapersistence.ch14.Bid;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class BidDaoImpl implements BidDao {

    private final SessionFactory sessionFactory;

    public BidDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Bid getById(long id) {
        return sessionFactory.getCurrentSession().get(Bid.class, id);
    }

    @Override
    public List<Bid> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Bid", Bid.class).getResultList();
    }

    @Override
    public void insert(Bid bid) {
        sessionFactory.getCurrentSession().persist(bid);
    }

    @Override
    public void update(long id, String amount) {
        Bid bid = sessionFactory.getCurrentSession().get(Bid.class, id);
        bid.setAmount(new BigDecimal(amount));
        sessionFactory.getCurrentSession().merge(bid);
    }

    @Override
    public void delete(Bid bid) {
        sessionFactory.getCurrentSession().remove(bid);
    }

    @Override
    public List<Bid> findByAmount(String amount) {
        return sessionFactory.getCurrentSession().createQuery("from Bid where amount=:amount", Bid.class)
                .setParameter("amount", new BigDecimal(amount)).getResultList();
    }
}
