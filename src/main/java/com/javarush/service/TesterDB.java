package com.javarush.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.domain.City;
import com.javarush.domain.CountryLanguage;
import com.javarush.redis.CityCountry;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TesterDB {
    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final DataHandler dataHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //10 exactly known city id`s
    private final static List<Integer> CITY_ID_LIST = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);
    @Getter
    private final Map<String, Long> testResultMap = new HashMap<>();

    public TesterDB(SessionFactory sessionFactory, RedisClient redisClient, DataHandler dataHandler) {
        this.sessionFactory = sessionFactory;
        this.redisClient = redisClient;
        this.dataHandler = dataHandler;
    }

    public void runMysqlTest() {
        long startTime = System.currentTimeMillis();

        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : CITY_ID_LIST) {
                City city = dataHandler.getCityDao().getById(id);
                //to make sure to get a full object (without proxy-stubs), explicitly ask the country list of languages
                Set<CountryLanguage> languages = city.getCountry().getCountryLanguages();
            }
            session.getTransaction().commit();
        }

        long endTime = System.currentTimeMillis();

        testResultMap.put("Test execution time for MySql:", endTime - startTime);
    }

    /*
    open a synchronous connection, and for each id we get a JSON String,
    which we convert into the CityCountry type object we need
    */
    public void runRedisTest() {
        long startTime = System.currentTimeMillis();

        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : CITY_ID_LIST) {
                String value = sync.get(String.valueOf(id));
                try {
                    objectMapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        long endTime = System.currentTimeMillis();

        testResultMap.put("Test execution time for Redis:", endTime - startTime);
    }
}
