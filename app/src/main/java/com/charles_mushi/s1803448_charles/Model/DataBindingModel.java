//
// Student name: Charles Mushi
//  Student ID: S1803448
//

package com.charles_mushi.s1803448_charles.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;
/**
 * Model for mapping details extracted from the pullparser
 */

public class DataBindingModel implements Serializable {
    private String title ,description, link, category,location;
    private Date pubDate;
    private double lat;
    private double lon;
    private Date originDate;
    private int depth;
    private double magnitude;

    public DataBindingModel(String title, String description, String link, String pubicationDate, String category, double lat, double lon, String originDate, String location, int depth, double magnitude) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = parseDate(pubicationDate);
        this.category = category;
        this.lat = lat;
        this.lon = lon;
        this.originDate = parseDate(originDate);
        this.location = location;
        this.depth = depth;
        this.magnitude = magnitude;
    }
    public DataBindingModel() {

    }

    public Date getOriginDate() {
        return originDate;
    }

    public String getLocation() {
        return location;
    }

    public int getDepth() {
        return depth;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        String[] tokens = description.split(";");
        this.originDate = parseDate(tokens[0].substring(tokens[0].indexOf(":")+2,tokens[0].length()-1));
        this.location = tokens[1].substring(tokens[1].indexOf(":")+2,tokens[1].length()-1);
        this.depth = Integer.parseInt(tokens[3].substring(tokens[3].indexOf(":")+2,tokens[3].length()-4));
        this.magnitude = Double.parseDouble(tokens[4].substring(tokens[4].indexOf(":")+2));
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(String date) {
        this.pubDate = parseDate(date);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataBindingModel item = (DataBindingModel) o;
        return Double.compare(item.lat, lat) == 0 &&
                Double.compare(item.lon, lon) == 0 &&
                depth == item.depth &&
                Double.compare(item.magnitude, magnitude) == 0 &&
                Objects.equals(title, item.title) &&
                Objects.equals(description, item.description) &&
                Objects.equals(link, item.link) &&
                Objects.equals(pubDate, item.pubDate) &&
                Objects.equals(category, item.category) &&
                Objects.equals(originDate, item.originDate) &&
                Objects.equals(location, item.location);
    }

    private Date parseDate(String pubDate) {
        try {
            return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss").parse(pubDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class ImageTitleLink implements Serializable {

        String title, url,link;

        public ImageTitleLink() {

        }
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getURL() {
            return url;
        }

        public void setURL(String url) {
            this.url = url;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public static class ParcebleModel implements Parcelable {

        private LinkedModel channel = new LinkedModel();

        @Override
        public void writeToParcel(Parcel dest, int flags){
            dest.writeValue(channel);
        }

        public ParcebleModel(Parcel p) {

            channel = (LinkedModel) p.readValue(channel.getClass().getClassLoader());
        }


        public ParcebleModel(LinkedModel channel) {
            this.channel = channel;
        }

        public LinkedModel getData() {
            return channel;
        }

        public static final Creator<ParcebleModel> CREATOR = new Creator<ParcebleModel>(){

            @Override
            public ParcebleModel createFromParcel(Parcel parcel) {
                return new ParcebleModel(parcel);
            }

            @Override
            public ParcebleModel[] newArray(int size) {
                return new ParcebleModel[0];
            }
        };

        @Override
        public int describeContents() {
            return hashCode();
        }
    }
}
