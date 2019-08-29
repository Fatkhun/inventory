package com.fatkhun.inventory.data.model;

import java.util.List;

public class DataProvince {
    private List<ProvinceResponse> listProvince;

    public List<ProvinceResponse> getListProvince() {
        return listProvince;
    }

    public void setListProvince(List<ProvinceResponse> listProvince) {
        this.listProvince = listProvince;
    }

    @Override
    public String toString() {
        return "DataProvince{" +
                "listProvince=" + listProvince +
                '}';
    }
}
