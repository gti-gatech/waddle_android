
package com.asif.asifroutedirectionlibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;



@SuppressWarnings("WeakerAccess")
public class Waypoint implements Parcelable {
    @SerializedName("location")
    private Coordination location;
    @SerializedName("step_index")
    private int index;
    @SerializedName("step_interpolation")
    private double interpolation;

    public Waypoint() {
    }

    protected Waypoint(Parcel in) {
        location = in.readParcelable(Coordination.class.getClassLoader());
        index = in.readInt();
        interpolation = in.readDouble();
    }

    public Coordination getLocation() {
        return location;
    }

    public void setLocation(Coordination location) {
        this.location = location;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(double interpolation) {
        this.interpolation = interpolation;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeInt(index);
        dest.writeDouble(interpolation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Waypoint> CREATOR = new Creator<Waypoint>() {
        @Override
        public Waypoint createFromParcel(Parcel in) {
            return new Waypoint(in);
        }

        @Override
        public Waypoint[] newArray(int size) {
            return new Waypoint[size];
        }
    };
}
