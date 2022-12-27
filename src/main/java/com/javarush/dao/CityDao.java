package com.javarush.dao;

import com.javarush.domain.City;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CityDao {
    private final SessionFactory sessionFactory;

    public CityDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<City> getItems(int offset, int limit) {
        Query<City> cityQuery = sessionFactory.getCurrentSession().createQuery("select c from City c", City.class);
        cityQuery.setFirstResult(offset);
        cityQuery.setMaxResults(limit);

        return cityQuery.list();
    }

    public int getTotalCount() {
        Query<Long> longQuery = sessionFactory.getCurrentSession().createQuery("select count(c) from City c", Long.class);

        return Math.toIntExact(longQuery.uniqueResult());
    }
}
