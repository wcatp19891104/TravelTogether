package com.traveltogether.model.request;

import com.traveltogether.model.common.BillEntry;
import com.traveltogether.model.common.User;

import java.io.Serializable;

public class AddBillRequest implements Serializable {

    private User user;
    private BillEntry billEntry;

    public AddBillRequest() {}

    public AddBillRequest(User user, BillEntry billEntry) {
        this.user = user;
        this.billEntry = billEntry;
    }

    public User getUser() {
        return user;
    }

    public BillEntry getBillEntry() {
        return billEntry;
    }

    public void setBillEntry(BillEntry billEntry) {
        this.billEntry = billEntry;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
