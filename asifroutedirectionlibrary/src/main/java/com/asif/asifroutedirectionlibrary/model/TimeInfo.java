
package com.asif.asifroutedirectionlibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;



@SuppressWarnings("WeakerAccess")
public class TimeInfo implements Parcelable {
    @SerializedName("text")
    private String text;
    @SerializedName("time_zone")
    private String timeZone;
    @SerializedName("value")
    private String value;

    public TimeInfo() {
    }

    protected TimeInfo(Parcel in) {
        text = in.readString();
        timeZone = in.readString();
        value = in.readString();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(timeZone);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TimeInfo> CREATOR = new Creator<TimeInfo>() {
        @Override
        public TimeInfo createFromParcel(Parcel in) {
            return new TimeInfo(in);
        }

        @Override
        public TimeInfo[] newArray(int size) {
            return new TimeInfo[size];
        }
    };
}
