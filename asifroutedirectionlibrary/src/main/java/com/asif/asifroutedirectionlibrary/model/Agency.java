
package com.asif.asifroutedirectionlibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("WeakerAccess")
public class Agency implements Parcelable {
    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String url;

    public Agency() {
    }

    protected Agency(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Agency> CREATOR = new Creator<Agency>() {
        @Override
        public Agency createFromParcel(Parcel in) {
            return new Agency(in);
        }

        @Override
        public Agency[] newArray(int size) {
            return new Agency[size];
        }
    };
}
