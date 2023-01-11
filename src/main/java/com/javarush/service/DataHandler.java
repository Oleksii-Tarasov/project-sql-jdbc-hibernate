package com.javarush.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.dao.CityDao;
import com.javarush.dao.CountryDao;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.redis.Language;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class DataHandler {
    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;
    private final CountryDao countryDao;
    private final CityDao cityDao;

    private final DBConnector dbConnector;

    public DataHandler() {
        this.dbConnector = new DBConnector();
        this.sessionFactory = dbConnector.prepareMySqlRelationalDb();
        this.redisClient = dbConnector.prepareRedisClient();
        this.countryDao = new CountryDao(sessionFactory);
        this.cityDao = new CityDao(sessionFactory);
        this.objectMapper = new ObjectMapper();
    }

    public List<City> getAllCitiesByStep(int step) {
        try (Session session = sessionFactory.getCurrentSession()){
            List<City> cityList = new ArrayList<>();

            session.beginTransaction();

            List<Country> countryList = countryDao.getAll();

            int totalCount = cityDao.getTotalCount();

            for (int i = 0; i < totalCount; i += step) {
                cityList.addAll(cityDao.getItems(i, step));
            }

            session.getTransaction().commit();

            return cityList;
        }
    }

    public List<CityCountry> transformData(List<City> cities) {
        return cities.stream().map(city -> {
            CityCountry cityCountry = new CityCountry();
            cityCountry.setId(city.getId());
            cityCountry.setName(city.getName());
            cityCountry.setDistrict(city.getDistrict());
            cityCountry.setPopulation(city.getPopulation());

            Country country = city.getCountry();
            cityCountry.setContinent(country.getContinent());
            cityCountry.setCountryName(country.getName());
            cityCountry.setCode(country.getCode());
            cityCountry.setCode2(country.getCode2());
            cityCountry.setCountryRegion(country.getRegion());
            cityCountry.setCountryPopulation(country.getPopulation());
            cityCountry.setCountrySurfaceArea(country.getSurfaceArea());

            Set<CountryLanguage> countryLanguages = country.getCountryLanguages();
            Set<Language> languages = countryLanguages.stream().map(cl -> {
                Language language = new Language();
                language.setLanguage(cl.getLanguage());
                language.setIsOfficial(language.getIsOfficial());
                language.setPercentage(language.getPercentage());
                return language;
            }).collect(Collectors.toSet());

            cityCountry.setCountryLanguages(languages);

            return cityCountry;
        }).collect(Collectors.toList());
    }

//    public void pushToRedis(List<CityCountry> preparedData) {
//        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
//            RedisStringCommands<String, String> sync = connection.sync();
//            for (CityCountry cityCountry : preparedData) {
//                try {
//                    sync.set(String.valueOf(cityCountry.getId()), objectMapper.writeValueAsString(cityCountry));
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    public void shutDownMySqlSession() {
//        sessionFactory.getCurrentSession().close();
//    }

//    public void shutDownAllSessions() {
//        if (nonNull(sessionFactory)) {
//            sessionFactory.close();
//        }
//
//        if (nonNull(redisClient)) {
//            redisClient.shutdown();
//        }
//    }
}
