package ru.cachehw.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cachehw.CacheService;
import ru.cachehw.CacheServiceImpl;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    private static final int MAX_SIZE = 1000;
    private static final long TIME_TO_LIVE = 60000;

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration,
                Client.class, Phone.class, Address.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        CacheService<Long, Client> cacheService = new CacheServiceImpl<>(MAX_SIZE, TIME_TO_LIVE, executorService);
        var dbServiceClient = new DbServiceClientImpl(cacheService, transactionManager, clientTemplate);

        List<Client> clients = new ArrayList<>();

        for (int i = 0; i < 1_000; i++) {
            clients.add(dbServiceClient.saveClient(new Client("client" + i)));
        }

        long start = System.currentTimeMillis();
        for (Client client : clients) {
            dbServiceClient.getClient(client.getId())
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + client.getId()));
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("_________________________");
        System.out.println("time of select: " + timeElapsed);

        System.gc();

        start = System.currentTimeMillis();
        for (Client client : clients) {
            dbServiceClient.getClient(client.getId())
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + client.getId()));
        }
        finish = System.currentTimeMillis();
        timeElapsed = finish - start;
        System.out.println("_________________________");
        System.out.println("time of select: " + timeElapsed);
    }
}