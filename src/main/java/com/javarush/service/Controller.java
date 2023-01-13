package com.javarush.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.connectordb.MySqlConnector;
import com.javarush.connectordb.RedisConnector;
import com.javarush.domain.City;
import com.javarush.redis.CityCountry;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

public class Controller {

    private static Controller controller;
    private final DataHandler dataHandler;
    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final TesterDB testerDB;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Controller() {
        this.sessionFactory = MySqlConnector.getSessionFactory();
        this.redisClient = RedisConnector.getRedisClient();
        this.dataHandler = new DataHandler(sessionFactory);
        this.testerDB = new TesterDB(sessionFactory, redisClient, dataHandler);
    }

    public static Controller getController() {
        if (controller == null) {
            controller = new Controller();
        }

        return controller;
    }

    public List<CityCountry> prepareFrequentlyRequestedData() {
        List<City> cityList = dataHandler.getAllCities();

        return dataHandler.transformData(cityList);
    }

    public void pushDataToRedis(List<CityCountry> preparedData) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : preparedData) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), objectMapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, Long> runTestingDB() {
        testerDB.runMysqlTest();
        testerDB.runRedisTest();

        return testerDB.getTestResultMap();
    }

    public void shutDownCurrentMySqlSession() {
        sessionFactory.getCurrentSession().close();
    }

    public void shutDownAllSessions() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }

        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }
}
