package main.java.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author ChaosWong
 * @date 2020/1/9 1:08
 * @title
 */
public class TransactionUtils {
    private Configuration configuration = new Configuration().configure();
    private SessionFactory factory;
    private Session session;
    private Transaction transaction;

    public TransactionUtils (){
        factory = configuration.buildSessionFactory();
        session = factory.openSession();
        transaction = session.getTransaction();
        transaction.begin();
    }

    public Transaction getTransaction () {
        return transaction;
    }

    public Session getSession () {
        return session;
    }
    public void endTransaction(){
        transaction.commit();
        session.close();
    }
}
