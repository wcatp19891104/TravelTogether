package com.traveltogether.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.inject.*;
import com.traveltogether.calculation.BasicCalculator;
import com.traveltogether.calculation.Calculator;
import com.traveltogether.model.common.PayEntry;
import com.traveltogether.model.request.AddBillRequest;
import com.traveltogether.util.Jsonizer;
import com.traveltogether.util.MoneyDeserializer;
import com.traveltogether.util.MoneySerializer;
import org.joda.money.Money;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.vertx.core.json.Json.mapper;

public class CalculationService {

    private final Calculator calculator;
    private final Jsonizer jsonizer;

    @Inject
    public CalculationService(Calculator calculator, Jsonizer jsonizer) {
        this.calculator = calculator;
        this.jsonizer = jsonizer;
    }

    public String calculate(String input) {
        List<AddBillRequest> billRequests = jsonizer.toObjectList(input, AddBillRequest.class);
        List<PayEntry> payEntries = calculator.calculate(billRequests);
        String ret = jsonizer.toJsonList(payEntries);
        System.out.println(ret);
        return ret;
    }

    private static class CalculationServiceModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(Calculator.class).to(BasicCalculator.class);
        }

        @Provides
        private ObjectMapper getJsonObjectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Money.class, new MoneyDeserializer());
            module.addSerializer(Money.class, new MoneySerializer());
            mapper.registerModule(module);
            return mapper;
        }

    }


//    public static void main(String[] args) {
//        Injector injector = Guice.createInjector(new CalculationServiceModule());
//        CalculationService calculationService = injector.getInstance(CalculationService.class);
//
//        File file = new File("src/main/resources/temp.json");
//        try {
//            String inputString = Files.toString(file, Charsets.UTF_8);
//            calculationService.calculate(inputString);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }


}
