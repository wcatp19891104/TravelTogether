package com.traveltogether.model.result;

import com.traveltogether.model.common.PayEntry;

import java.io.Serializable;
import java.util.List;

public class SingleUserResult implements Serializable{

    private Boolean shouldOwn;
    private Boolean shouldPay;
    private List<PayEntry> payEntries;

    public Boolean getShouldOwn() {
        return shouldOwn;
    }

    public void setShouldOwn(Boolean shouldOwn) {
        this.shouldOwn = shouldOwn;
    }

    public List<PayEntry> getPayEntries() {
        return payEntries;
    }

    public void setPayEntries(List<PayEntry> payEntries) {
        this.payEntries = payEntries;
    }

    public Boolean getShouldPay() {
        return shouldPay;
    }

    public void setShouldPay(Boolean shouldPay) {
        this.shouldPay = shouldPay;
    }

    public class Builder {

    }
}
