package com.javarush.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.dao.CityDao;
import com.javarush.dao.CountryDao;
import com.javarush.domain.City;
import com.javarush.redis.CityCountry;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.SessionFactory;

import java.util.List;

import static java.util.Objects.nonNull;

public class MainController {

    private static MainController mainController;
    private final SessionFactory sessionFactory;
    private final DBConnector dbConnector;
    private final DataHandler dataHandler;
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;

    private MainController() {
        this.dataHandler = new DataHandler();
        this.dbConnector = new DBConnector();
        this.objectMapper = new ObjectMapper();
        this.sessionFactory = dbConnector.prepareMySqlRelationalDb();
        this.redisClient = dbConnector.prepareRedisClient();
    }

    public static MainController getMainController() {
        if(mainController == null) {
            mainController = new MainController();
        }

        return mainController;
    }

    public void taskExecute() {
        int stepForCityList = 500;
        List<City> cityList = dataHandler.getAllCitiesByStep(stepForCityList);
        List<CityCountry> preparedData = dataHandler.transformData(cityList);

        pushToRedis(preparedData);
        shutDownMySqlSession();

        //choose random 10 id cities
        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        main.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        main.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

        mainController.shutDownAllSessions();
    }

    private void pushToRedis(List<CityCountry> preparedData) {
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

    private void shutDownMySqlSession() {
        sessionFactory.getCurrentSession().close();
    }

    private void shutDownAllSessions() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }

        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }


}
