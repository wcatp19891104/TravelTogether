package com.traveltogether.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.money.Money;

import java.io.Serializable;

public class BillEntry implements Serializable{

    @JsonProperty
    private Money money;
    private BillType billType;
    private String description;

    public BillEntry() {}

    public BillEntry(Money money, BillType billType, String description) {
        this.money = money;
        this.billType = billType;
        this.description = description;
    }

    public BillType getBillType() {
        return billType;
    }

    public String getDescription() {
        return description;
    }

    public Money getMoney() {
        return money;
    }

    public void setBillType(BillType billType) {
        this.billType = billType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
