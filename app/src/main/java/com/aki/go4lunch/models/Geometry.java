
package com.aki.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Geometry implements Serializable, Parcelable
{

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("viewport")
    @Expose
    private Viewport viewport;
    public final static Creator<Geometry> CREATOR = new Creator<Geometry>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Geometry createFromParcel(Parcel in) {
            return new Geometry(in);
        }

        public Geometry[] newArray(int size) {
            return (new Geometry[size]);
        }

    }
    ;
    private final static long serialVersionUID = -2724964726432730401L;

    protected Geometry(Parcel in) {
        this.location = ((Location) in.readValue((Location.class.getClassLoader())));
        this.viewport = ((Viewport) in.readValue((Viewport.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Geometry() {
    }

    /**
     * 
     * @param viewport
     * @param location
     */
    public Geometry(Location location, Viewport viewport) {
        super();
        this.location = location;
        this.viewport = viewport;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Geometry withLocation(Location location) {
        this.location = location;
        return this;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public Geometry withViewport(Viewport viewport) {
        this.viewport = viewport;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(location);
        dest.writeValue(viewport);
    }

    public int describeContents() {
        return  0;
    }

}
