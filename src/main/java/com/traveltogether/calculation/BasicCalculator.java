package com.traveltogether.calculation;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.io.Files;
import com.traveltogether.model.common.BillEntry;
import com.traveltogether.model.common.BillType;
import com.traveltogether.model.common.PayEntry;
import com.traveltogether.model.common.User;
import com.traveltogether.model.request.AddBillRequest;
import com.traveltogether.model.result.GroupCalculationResult;
import com.traveltogether.model.result.SingleUserResult;
import com.traveltogether.util.Jsonizer;
import org.javatuples.Pair;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;


import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BasicCalculator implements Calculator {

    private static final CurrencyUnit DEFAULT_CURRENCY = CurrencyUnit.USD;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_DOWN;

    @Override
    public List<PayEntry> calculate(List<AddBillRequest> billRequests) {

        Pair<Money, List<CalculationUnit>> pair = getBillSumPair(billRequests);
        Money sum = pair.getValue0();
        List<CalculationUnit> calculationUnits = pair.getValue1();

        Pair<List<CalculationUnit>, List<CalculationUnit>> payOwnPair = getPayOwnCalculationUnitPair(sum, calculationUnits);
        List<CalculationUnit> shouldPayList = payOwnPair.getValue0();
        List<CalculationUnit> shouldOwnList = payOwnPair.getValue1();

        return performCalculation(shouldPayList, shouldOwnList);

    }

    @Override
    public GroupCalculationResult getGroupCalculationResult(List<AddBillRequest> billRequests) {
        return null;
    }

    @Override
    public SingleUserResult getSingleUserResult(List<AddBillRequest> billRequests, User user) {
        return null;
    }

    private Pair<Money, List<CalculationUnit>> getBillSumPair(List<AddBillRequest> billRequests) {

        Money sum = Money.zero(DEFAULT_CURRENCY);
        Map<User, CalculationUnit> userAmountMap = Maps.newHashMap();

        for (AddBillRequest billRequest : billRequests) {
            Money added = billRequest.getBillEntry().getMoney();
            sum = sum.plus(added);

            if (userAmountMap.containsKey(billRequest.getUser())) {

                userAmountMap.get(billRequest.getUser()).
                        add(new CalculationUnit(billRequest));

            } else {
                userAmountMap.put(billRequest.getUser(), new CalculationUnit(billRequest));
            }
        }

        List<CalculationUnit> calculationUnitList = Lists.newArrayList(userAmountMap.values());

        return Pair.with(sum, calculationUnitList);
    }

    private Pair<List<CalculationUnit>, List<CalculationUnit>> getPayOwnCalculationUnitPair
            (Money totalAmount, List<CalculationUnit> calculationUnits) {

        Money average = totalAmount.dividedBy(calculationUnits.size(), DEFAULT_ROUNDING);
        List<CalculationUnit> shouldPay = Lists.newArrayList();
        List<CalculationUnit> shouldOwn = Lists.newArrayList();
        for (CalculationUnit unit : calculationUnits) {
            Money offset = unit.getAmount().minus(average);
            // if has paid is more or equal than should pay, shoud Own
            if (offset.isPositiveOrZero()) {
                unit.setAmount(offset);
                shouldOwn.add(unit);
            } else {
                // if had paid is less, shoud pay
                unit.setAmount(offset.abs());
                shouldPay.add(unit);
            }
        }

        Ordering<CalculationUnit> ordering = new Ordering<CalculationUnit>() {
            @Override
            public int compare(CalculationUnit unit, CalculationUnit t1) {
                return unit.getAmount().compareTo(t1.getAmount());
            }
        };

        Collections.sort(shouldPay, ordering);
        Collections.sort(shouldOwn, ordering);

        return Pair.with(shouldPay, shouldOwn);
    }

    private List<PayEntry> performCalculation(List<CalculationUnit> shouldPay, List<CalculationUnit> shouldOwn) {
        int size = shouldPay.size();
        List<PayEntry> results = Lists.newArrayList();
        int payIndex = 0;
        int ownIndex = 0;

        while (payIndex < size && ownIndex < size) {

            CalculationUnit shouldPayUnit = shouldPay.get(payIndex);
            CalculationUnit shouldOwnUnit = shouldOwn.get(ownIndex);
            User from = shouldPayUnit.getUser();
            User to = shouldOwnUnit.getUser();
            // if should pay is less than should own, the should pay one should pay all amount he has to should own one,
            // and index next should pay is ready, and also need to minus the amount from should own
            if (shouldPayUnit.getAmount().isLessThan(shouldOwnUnit.getAmount())) {

                Money shouldPayAmount = shouldPayUnit.getAmount();
                Money newShouldOwnAmount = shouldOwnUnit.getAmount().minus(shouldPayAmount);
                shouldOwnUnit.setAmount(newShouldOwnAmount);
                results.add(new PayEntry(from, to, shouldPayAmount, false));
                payIndex++;
            } else if (shouldPayUnit.getAmount().isGreaterThan(shouldOwnUnit.getAmount())){
                // if should pay is larger than should own, the should pay will pay the should own amount, and minus this
                // amount from should pay. new should own is ready
                Money shouldPayAmount = shouldOwnUnit.getAmount();
                Money newShouldPayAmount = shouldPayUnit.getAmount().minus(shouldPayAmount);
                shouldPayUnit.setAmount(newShouldPayAmount);
                results.add(new PayEntry(from, to, shouldPayAmount, false));
                ownIndex++;
            } else {
                // if equals, both ++
                Money shouldPayAmount = shouldPayUnit.getAmount();
                results.add(new PayEntry(from, to, shouldPayAmount, false));
                payIndex++;
                ownIndex++;
            }

        }

        return results;

    }

    private class CalculationUnit {

        private User user;
        private Money amount;

        public CalculationUnit(User user, Money amount) {
            this.user = user;
            this.amount = amount;
        }

        public CalculationUnit(AddBillRequest addBillRequest) {
            this.user = addBillRequest.getUser();
            this.amount = addBillRequest.getBillEntry().getMoney();
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Money getAmount() {
            return amount;
        }

        public void setAmount(Money amount) {
            this.amount = amount;
        }

        public void add(CalculationUnit unit) {
            this.amount = this.amount.plus(unit.amount);
        }

    }

//    public static void main(String[] argv) throws JsonProcessingException {
//        BasicCalculator basicCalculator = new BasicCalculator();
//        AddBillRequest a1 = new AddBillRequest(
//                new User(1L, 2L, "laowang"),
//                new BillEntry(Money.of(CurrencyUnit.USD, 50.0), BillType.GAS, "h")
//        );
//        AddBillRequest a2 = new AddBillRequest(
//                new User(1L, 2L, "laowang"),
//                new BillEntry(Money.of(CurrencyUnit.USD, 60.0), BillType.GAS, "h")
//        );
//
//        AddBillRequest a3 = new AddBillRequest(
//                new User(2L, 3L, "zaicheng"),
//                new BillEntry(Money.of(CurrencyUnit.USD, 50.0), BillType.GAS, "h")
//        );
//        AddBillRequest a4 = new AddBillRequest(
//                new User(2L, 3L, "zaicheng"),
//                new BillEntry(Money.of(CurrencyUnit.USD, 70.0), BillType.GAS, "h")
//        );
//
//        AddBillRequest a5 = new AddBillRequest(
//                new User(3L, 4L, "heliu"),
//                new BillEntry(Money.of(CurrencyUnit.USD, 100.0), BillType.GAS, "h")
//        );
//        AddBillRequest a6 = new AddBillRequest(
//                new User(3L, 4L, "heliu"),
//                new BillEntry(Money.of(CurrencyUnit.USD, 200.0), BillType.GAS, "h")
//        );
//
//        AddBillRequest a7 = new AddBillRequest(
//                new User(4L, 5L, "laizong"),
//                new BillEntry(Money.of(CurrencyUnit.USD, 110.0), BillType.GAS, "h")
//        );
//        AddBillRequest a8 = new AddBillRequest(
//                new User(4L, 5L, "laizong"),
//                new BillEntry(Money.of(CurrencyUnit.USD, 110.0), BillType.GAS, "h")
//        );
//
//        List<AddBillRequest> requests = Lists.newArrayList(a1, a2, a3, a4, a5, a6, a7, a8);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        Jsonizer jsonizer = new Jsonizer(objectMapper);
//
//        File file = new File("src/main/resources/temp.json");
//        try {
//            Files.write(jsonizer.toJsonList(requests).getBytes(), file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        List<PayEntry> payEntries = basicCalculator.calculate(requests);
////        System.out.print("");
////        ObjectMapper mapper = new ObjectMapper();
////        for (PayEntry payEntry : payEntries) {
////            System.out.println(mapper.writeValueAsString(payEntry));
////        }
//
//
//    }

}
