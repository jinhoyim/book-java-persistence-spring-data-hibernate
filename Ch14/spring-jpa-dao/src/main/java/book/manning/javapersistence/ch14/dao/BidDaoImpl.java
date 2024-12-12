package book.manning.javapersistence.ch14.dao;

import book.manning.javapersistence.ch14.Bid;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class BidDaoImpl implements BidDao {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Override
    public Bid getById(long id) {
        return em.find(Bid.class, id);
    }

    @Override
    public List<Bid> getAll() {
        return em.createQuery("from Bid", Bid.class).getResultList();
    }

    @Override
    public void insert(Bid bid) {
        em.persist(bid);
    }

    @Override
    public void update(long id, String amount) {
        Bid bid = em.find(Bid.class, id);
        bid.setAmount(new BigDecimal(amount));
        em.merge(bid);
    }

    @Override
    public void delete(Bid bid) {
        em.remove(bid);
    }

    @Override
    public List<Bid> findByAmount(String amount) {
        return em.createQuery("from Bid where amount=:amount", Bid.class)
                .setParameter("amount", new BigDecimal(amount)).getResultList();
    }
}
