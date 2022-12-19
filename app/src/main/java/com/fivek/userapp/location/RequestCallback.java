package com.fivek.userapp.location;

import android.location.Address;
import android.location.Location;

import java.util.List;


public class RequestCallback {

    public interface LocationRequestCallback {
        void onLocationResult(Location location);

        void onFailedRequest(String result);
    }

    public interface PermissionRequestCallback {
        void onRationaleDialogOkPressed(int requestCode);
    }

    public interface AddressRequestCallback {
        void onAddressSuccessfulResult(List<Address> addressList);

        void onAddressFailedResult(String result);
    }


}
