package com.javarush.dao;

import com.javarush.domain.Country;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CountryDao {
    private final SessionFactory sessionFactory;

    public CountryDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Country> getAll() {
        Query<Country> countryQuery = sessionFactory.getCurrentSession().createQuery("select c from Country c", Country.class);

        return countryQuery.list();
    }
}
