package ru.otus.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cachehw.CacheService;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final CacheService<Long, Client> cacheService;

    public DbServiceClientImpl(CacheService<Long, Client> cacheService, TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.cacheService = cacheService;
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", clientCloned);
            } else {
                clientDataTemplate.update(session, clientCloned);
                log.info("updated client: {}", clientCloned);
            }
            putClientToCache(clientCloned);
            return clientCloned;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        var cachedClient = getClientFromCache(id);
        if (cachedClient.isPresent()) {
            log.info("cached client: {}", cachedClient.get());
            return cachedClient;
        } else {
            return transactionManager.doInReadOnlyTransaction(session -> {
                var clientOptional = clientDataTemplate.findById(session, id);
                log.info("client from DB: {}", clientOptional);
                clientOptional.ifPresent(this::putClientToCache);
                return clientOptional;
            });
        }
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }

    @Override
    public Optional<Client> getClientFromCache(long id) {
        return Optional.ofNullable(cacheService.get(id));
    }

    @Override
    public void putClientToCache(Client client) {
        cacheService.put(client.getId(), client);
    }

    @Override
    public void removeClientFromCache(long id) {
        cacheService.remove(id);
    }

    @Override
    public void clearCache() {
        cacheService.clear();
    }
}