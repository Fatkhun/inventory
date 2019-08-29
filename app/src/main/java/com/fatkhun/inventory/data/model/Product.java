package com.fatkhun.inventory.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private long mId;
    private String mName;
    private String mSkuCode;
    private String mPhotoPath;
    private int mQuantity;

    public Product() {
    }

    public Product(long id, String name, String code, String photoPath, int quantity) {
        mId = id;
        mName = name;
        mSkuCode = code;
        mPhotoPath = photoPath;
        mQuantity = quantity;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCode() {
        return mSkuCode;
    }

    public void setCode(String code) {
        mSkuCode = code;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mSkuCode='" + mSkuCode + '\'' +
                ", mPhotoPath='" + mPhotoPath + '\'' +
                ", mQuantity=" + mQuantity +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mSkuCode);
        dest.writeString(this.mPhotoPath);
        dest.writeInt(this.mQuantity);
    }

    private Product(Parcel in) {
        this.mId = in.readLong();
        this.mName = in.readString();
        this.mSkuCode = in.readString();
        this.mPhotoPath = in.readString();
        this.mQuantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
