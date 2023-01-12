package com.javarush;

import com.javarush.redis.CityCountry;
import com.javarush.service.Controller;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Controller controller = Controller.getController();

        List<CityCountry> frequentlyRequestedData = controller.prepareFrequentlyRequestedData();

        controller.pushDataToRedis(frequentlyRequestedData);

        // TESTING
        controller.shutDownCurrentMySqlSession();
        Map<String, Long> resultTest = controller.runTestingDB();
        resultTest.forEach((k, v) -> System.out.println(k + " " + v));
        // END OF TESTING

        controller.shutDownAllSessions();
    }
}