
package com.asif.asifroutedirectionlibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;



@SuppressWarnings("WeakerAccess")
public class StopPoint implements Parcelable {
    @SerializedName("location")
    private Coordination location;
    @SerializedName("name")
    private String name;

    public StopPoint() {
    }

    protected StopPoint(Parcel in) {
        location = in.readParcelable(Coordination.class.getClassLoader());
        name = in.readString();
    }

    public Coordination getLocation() {
        return location;
    }

    public void setLocation(Coordination location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StopPoint> CREATOR = new Creator<StopPoint>() {
        @Override
        public StopPoint createFromParcel(Parcel in) {
            return new StopPoint(in);
        }

        @Override
        public StopPoint[] newArray(int size) {
            return new StopPoint[size];
        }
    };
}
