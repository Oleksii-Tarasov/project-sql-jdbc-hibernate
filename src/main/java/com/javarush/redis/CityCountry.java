package com.javarush.redis;

import com.javarush.domain.Continent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class CityCountry {
    private Integer id;

    private String name;

    private String district;

    private Integer population;

    private String code;

    private String code2;

    private String countryName;

    private Continent continent;

    private String countryRegion;

    private BigDecimal countrySurfaceArea;

    private Integer countryPopulation;

    private Set<Language> countryLanguages;
}
