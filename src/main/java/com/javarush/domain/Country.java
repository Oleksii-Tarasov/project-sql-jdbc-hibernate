package com.javarush.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;


@Entity
@Table(schema = "world", name = "country")
@Getter
@Setter
public class Country {
    @Id
    @Column(name = "id")
    private Integer id;

    private String code;

    @Column(name = "code_2")
    private String code2;

    private String name;

    @Column(name = "continent")
    @Enumerated(EnumType.ORDINAL)
    private Continent continent;

    private String region;

    @Column(name = "surface_area")
    private BigDecimal surfaceArea;

    @Column(name = "indep_year")
    private Short indepYear;

    private Integer population;

    @Column(name = "life_expectancy")
    private BigDecimal lifeExpectancy;

    private BigDecimal gnp;

    @Column(name = "gnpo_id")
    private BigDecimal gnpoId;

    @Column(name = "local_name")
    private String localName;

    @Column(name = "government_form")
    private String governmentForm;

    @Column(name = "head_of_state")
    private String headOfState;

    @OneToOne
    @JoinColumn(name = "capital")
    private City capital;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Set<CountryLanguage> countryLanguages;
}
