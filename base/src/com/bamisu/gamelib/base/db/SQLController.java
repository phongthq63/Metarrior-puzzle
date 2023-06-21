package com.bamisu.gamelib.base.db;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.internal.ManagedSessionContext;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class SQLController {
    public static final Logger logger = Logger.getLogger(SQLController.class);
    private SessionFactory sessionFactory;
    private Configuration configuration;
    private String name;

    /*----------------------------------------------------------------------------------------------------------------*/

    public SQLController(String name) {
        this.name = name;
        buildSessionFactory();
    }

    public void reset() {
        sessionFactory = null;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public SessionFactory buildSessionFactory() {
        return sessionFactory = buildSessionFactory(System.getProperty("user.dir") + "/conf/hibernate-" + name + ".cfg.xml", "");
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    public SessionFactory buildSessionFactory(String hibernateFilename, String configFilename) {
        try {
            File file = new File(hibernateFilename);
            logger.info("buildSessionFactory - path = " + hibernateFilename);
            configuration = new Configuration().configure(file);
            if (configFilename != null && !configFilename.equals("")) {
                try {
                    Properties prop = new Properties();
                    prop.load(new FileReader(new File(configFilename)));
                    configuration.setProperty("hibernate.connection.username", prop.get("username").toString());
                    configuration.setProperty("hibernate.connection.password", prop.get("password").toString());
                    logger.info("URL: " + prop.get("url").toString());
                    logger.info("Username: " + prop.get("username").toString());
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            StandardServiceRegistryBuilder serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            // Create the SessionFactory from hibernate.cfg.xml
            return configuration.buildSessionFactory(serviceRegistry.build());
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            logger.error("Initial SessionFactory creation failed.", ex);
            ex.printStackTrace();
            // throw new ExceptionInInitializerError(ex);
            return null;
        }
    }

    /**
     * Callback interface for Hibernate code. To be used with
     * 's execution methods, often as anonymous classes
     * within a method implementation. A typical implementation will call
     * {@code SessionModel.load/find/update} to perform some operations on persistent
     * objects.
     *
     * @author Juergen Hoeller
     * @since 4.0.1
     */
    public interface HibernateCallback<T> {

        /**
         * Gets called by {@code HibernateTemplate.execute} with an active
         * Hibernate {@code SessionModel} . Does not need to care about activating or
         * closing the {@code SessionModel}, or handling transactions.
         * <p>
         * <p>
         * Allows for returning a result object created within the callback,
         * i.e. a domain object or a collection of domain objects. A thrown
         * custom RuntimeException is treated as an application exception: It
         * gets propagated to the caller of the template.
         *
         * @param session active Hibernate session
         * @return a result object, or {@code null} if none
         * @throws HibernateException if thrown by the Hibernate API
         */
        T doInHibernate(Session session) throws HibernateException;

    }

    protected <T> T doExecute(HibernateCallback<T> action, boolean enforceNativeSession) throws Exception {
        Session session = null;
        boolean isNew = false;
        try {
            session = getSessionFactory().getCurrentSession();
        } catch (HibernateException ex) {
            logger.debug("Could not retrieve pre-bound Hibernate session", ex);
        }
        if (session == null) {
            session = getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            isNew = true;
        }

        try {
            T result = action.doInHibernate(session);
            if (isNew)
                session.getTransaction().commit();
            return result;
        } catch (HibernateException ex) {
            if (isNew)
                session.getTransaction().rollback();
            throw ex;
        } catch (RuntimeException ex) {
            // Callback code threw application exception...
            if (isNew)
                session.getTransaction().rollback();
            throw ex;
        } finally {
            if (isNew) {
                session.close();
            }
        }
    }

}
