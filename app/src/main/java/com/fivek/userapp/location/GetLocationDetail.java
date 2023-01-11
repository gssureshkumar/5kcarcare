package com.fivek.userapp.location;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.fivek.userapp.utils.Logger;
import com.fivek.userapp.viewmodel.response.LocationInfoData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class GetLocationDetail {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/";
    private static Retrofit retrofit;
    private AddressCallBack addressCallBack;
    private Context context;
    private final String TAG = "GetLocationDetailExc-->";

    public GetLocationDetail(AddressCallBack addressCallBack, Context context) {
        this.addressCallBack = addressCallBack;
        this.context = context;
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public void getAddressFromApi(Double latitude, Double longitude, String key) {
        StringBuilder tempBuilder = new StringBuilder();
        tempBuilder.append(latitude);
        tempBuilder.append(",");
        tempBuilder.append(longitude);
        DataService dataService = getRetrofitInstance().create(DataService.class);
        Call<String> stringCall = dataService.getData(tempBuilder.toString(), true, key);
        if (stringCall.isExecuted()) {
            stringCall.cancel();
        }
        stringCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.body());
                    Log.e("jsonObject -->", "onResponse: " + jsonObject);
                    JSONArray Results = jsonObject.getJSONArray("results");
                    JSONObject zero = Results.getJSONObject(0);
                    JSONArray address_components = zero.getJSONArray("address_components");
                    String fullAddress = zero.getString("formatted_address");
                    String city = "", state = "", country = "";
                    for (int i = 0; i < address_components.length(); i++) {
                        JSONObject zero2 = address_components.getJSONObject(i);
                        String long_name = zero2.getString("long_name");
                        String short_name = zero2.getString("short_name");
                        JSONArray mtypes = zero2.getJSONArray("types");
                        String Type = mtypes.getString(0);


                        if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                            city = long_name;
                        } else if (Type.equalsIgnoreCase("administrative_area_level_3")) {
                            city = long_name;
                        } else if (Type.equalsIgnoreCase("locality")) {
                            city = long_name;
                        }

                        if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                            state = short_name;
                        }

                        if (Type.equalsIgnoreCase("country")) {
                            country = long_name;
                        }
                    }

                    String[] split = city.split("\\s+");
                    city = split[0];

                    if (!TextUtils.isEmpty(country) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(state)) {
                        addressCallBack.locationData(new LocationInfoData(fullAddress, city, latitude, longitude, city, state, country));
                    } else {
                        addressCallBack.onError("Error in Fetching Location");
                    }
                } catch (JSONException e) {
                    Logger.INSTANCE.logDebug(TAG, "From getAddressFromApi JSONException " + e.getMessage());
                } catch (NullPointerException e) {
                    Logger.INSTANCE.logDebug(TAG, "From getAddressFromApi NullPointerException " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.INSTANCE.logDebug(TAG, "From getAddressFromApi onFailure " + t.toString());
                addressCallBack.onError("Error in Fetching Location");
            }
        });
    }


    private interface DataService {
        @GET("api/geocode/json")
        Call<String> getData(@Query("latlng") String latLong, @Query("sensor") boolean sensor, @Query("key") String key);
    }
}
