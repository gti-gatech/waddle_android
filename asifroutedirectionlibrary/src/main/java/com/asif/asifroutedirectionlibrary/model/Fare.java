

package com.asif.asifroutedirectionlibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("WeakerAccess")
public class Fare implements Parcelable {
    @SerializedName("currency")
    private String currency;
    @SerializedName("value")
    private String value;
    @SerializedName("text")
    private String text;

    public Fare() {
    }

    protected Fare(Parcel in) {
        currency = in.readString();
        value = in.readString();
        text = in.readString();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currency);
        dest.writeString(value);
        dest.writeString(text);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Fare> CREATOR = new Creator<Fare>() {
        @Override
        public Fare createFromParcel(Parcel in) {
            return new Fare(in);
        }

        @Override
        public Fare[] newArray(int size) {
            return new Fare[size];
        }
    };
}
