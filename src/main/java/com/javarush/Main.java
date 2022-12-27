package com.javarush;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.dao.CityDao;
import com.javarush.dao.CountryDao;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Objects.nonNull;

public class Main {
    private final SessionFactory sessionFactory;
    private final ObjectMapper objectMapper;
    private final CountryDao countryDao;
    private final CityDao cityDao;

    public Main() {
        this.sessionFactory = prepareRelationalDb();
        this.countryDao = new CountryDao(sessionFactory);
        this.cityDao = new CityDao(sessionFactory);
        this.objectMapper = new ObjectMapper();
    }

    public static void main(String[] args) {
        Main main = new Main();
        List<City> cityList = main.fetchData(main);
        main.shutDown();
    }

    private SessionFactory prepareRelationalDb() {
        final SessionFactory sessionFactory;

        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/world");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "validate");
        properties.put(Environment.STATEMENT_BATCH_SIZE, "100");

        sessionFactory = new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .addProperties(properties)
                .buildSessionFactory();

        return sessionFactory;
    }

    private List<City> fetchData(Main main) {
        try (Session session = main.sessionFactory.getCurrentSession()){
            List<City> cityList = new ArrayList<>();
            session.beginTransaction();

            int totalCount = main.cityDao.getTotalCount();
            int step = 500;

            for (int i = 0; i < totalCount; i += step) {
                cityList.addAll(main.cityDao.getItems(i, step));
            }

            session.getTransaction().commit();

            return cityList;
        }
    }

    private void shutDown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
    }
}