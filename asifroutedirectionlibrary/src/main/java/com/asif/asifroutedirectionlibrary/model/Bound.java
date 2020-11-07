
package com.asif.asifroutedirectionlibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akexorcist on 11/29/15 AD.
 */

@SuppressWarnings("WeakerAccess")
public class Bound implements Parcelable {
    @SerializedName("northeast")
    private Coordination northeast;
    @SerializedName("southwest")
    private Coordination southwest;

    public Bound() {
    }

    protected Bound(Parcel in) {
        northeast = in.readParcelable(Coordination.class.getClassLoader());
        southwest = in.readParcelable(Coordination.class.getClassLoader());
    }

    public Coordination getNortheastCoordination() {
        return northeast;
    }

    public void setNortheast(Coordination northeast) {
        this.northeast = northeast;
    }

    public Coordination getSouthwestCoordination() {
        return southwest;
    }

    public void setSouthwest(Coordination southwest) {
        this.southwest = southwest;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(northeast, flags);
        dest.writeParcelable(southwest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Bound> CREATOR = new Creator<Bound>() {
        @Override
        public Bound createFromParcel(Parcel in) {
            return new Bound(in);
        }

        @Override
        public Bound[] newArray(int size) {
            return new Bound[size];
        }
    };
}
