package com.traveltogether.model.common;

import org.joda.money.Money;

import java.io.Serializable;

public class PayEntry implements Serializable {

    private User from;
    private User to;
    private Money amount;
    private Boolean paid;

    public PayEntry(User from, User to, Money amount, Boolean paid) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.paid = paid;
    }

    public PayEntry() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayEntry)) return false;

        PayEntry payEntry = (PayEntry) o;

        if (!getFrom().equals(payEntry.getFrom())) return false;
        if (!getTo().equals(payEntry.getTo())) return false;
        if (!getAmount().equals(payEntry.getAmount())) return false;
        return getPaid().equals(payEntry.getPaid());

    }

    @Override
    public int hashCode() {
        int result = getFrom().hashCode();
        result = 31 * result + getTo().hashCode();
        result = 31 * result + getAmount().hashCode();
        result = 31 * result + getPaid().hashCode();
        return result;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public Money getAmount() {
        return amount;
    }

    public Boolean getPaid() {
        return paid;
    }
}
