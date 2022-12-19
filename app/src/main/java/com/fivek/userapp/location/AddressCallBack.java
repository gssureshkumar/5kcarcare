package com.fivek.userapp.location;

import com.fivek.userapp.viewmodel.response.LocationInfoData;

public interface AddressCallBack {
    void locationData(LocationInfoData locationData);
    void  onError(String message);
}
