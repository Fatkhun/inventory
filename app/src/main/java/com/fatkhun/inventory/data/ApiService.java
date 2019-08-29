package com.fatkhun.inventory.data;

import com.fatkhun.inventory.data.model.CityResponse;
import com.fatkhun.inventory.data.model.DataProvince;
import com.fatkhun.inventory.data.model.ProvinceResponse;
import com.fatkhun.inventory.data.model.ZipCodeResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface ApiService {

    @GET("kota_kab/k69.json")
    Single<List<ZipCodeResponse>> getZipCode();

    @GET("list_propinsi.json")
    Single<DataProvince> getProvince();

    @GET("list_kotakab/p9.json")
    Single<List<CityResponse>> getCity();
}
