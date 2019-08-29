package com.fatkhun.inventory.data.model;

import com.google.gson.annotations.SerializedName;

public class ZipCodeResponse{

	@SerializedName("kecamatan")
	private String kecamatan;

	@SerializedName("kodepos")
	private String kodepos;

	@SerializedName("kelurahan")
	private String kelurahan;

	public void setKecamatan(String kecamatan){
		this.kecamatan = kecamatan;
	}

	public String getKecamatan(){
		return kecamatan;
	}

	public void setKodepos(String kodepos){
		this.kodepos = kodepos;
	}

	public String getKodepos(){
		return kodepos;
	}

	public void setKelurahan(String kelurahan){
		this.kelurahan = kelurahan;
	}

	public String getKelurahan(){
		return kelurahan;
	}

	@Override
 	public String toString(){
		return 
			"ZipCodeResponse{" + 
			"kecamatan = '" + kecamatan + '\'' + 
			",kodepos = '" + kodepos + '\'' + 
			",kelurahan = '" + kelurahan + '\'' + 
			"}";
		}
}