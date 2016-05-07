package com.traveltogether.calculation;

import com.traveltogether.model.common.PayEntry;
import com.traveltogether.model.common.User;
import com.traveltogether.model.request.AddBillRequest;
import com.traveltogether.model.result.GroupCalculationResult;
import com.traveltogether.model.result.SingleUserResult;

import java.util.List;

public interface Calculator {

    public abstract List<PayEntry> calculate(List<AddBillRequest> billRequests);

    public abstract GroupCalculationResult getGroupCalculationResult(List<AddBillRequest> billRequests);

    public abstract SingleUserResult getSingleUserResult(List<AddBillRequest> billRequests, User user);

}
