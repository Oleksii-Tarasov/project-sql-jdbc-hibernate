package com.javarush.service;

import com.javarush.dao.CityDao;
import com.javarush.dao.CountryDao;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.redis.Language;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class DataHandler {

    private final SessionFactory sessionFactory;
    private final CountryDao countryDao;
    private final CityDao cityDao;

    public DataHandler(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.countryDao = new CountryDao(sessionFactory);
        this.cityDao = new CityDao(sessionFactory);
    }

    public List<City> getAllCities() {
        int stepForCycle = 500;
        try (Session session = sessionFactory.getCurrentSession()) {
            List<City> cityList = new ArrayList<>();

            session.beginTransaction();

             List<Country> countryList = countryDao.getAll();

            int totalCount = cityDao.getTotalCount();

            for (int i = 0; i < totalCount; i += stepForCycle) {
                cityList.addAll(cityDao.getItems(i, stepForCycle));
            }

            session.getTransaction().commit();

            return cityList;
        }
    }

    public List<CityCountry> transformData(List<City> cityList) {
        return cityList.stream().map(city -> {
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
}
