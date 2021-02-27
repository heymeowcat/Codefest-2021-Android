package com.heymeowcat.codefest2021androidproject.Model;

public class News {
    String docid;
    String News_EventTitle;
    String News_EventDescription;
    String type;
    CustomLatLng directions;
    String expectedTime;
    String distance;


    public CustomLatLng getDirections() {
        return directions;
    }

    public void setDirections(CustomLatLng directions) {
        this.directions = directions;
    }

    public News() {
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getNews_EventTitle() {
        return News_EventTitle;
    }

    public void setNews_EventTitle(String news_EventTitle) {
        News_EventTitle = news_EventTitle;
    }

    public String getNews_EventDescription() {
        return News_EventDescription;
    }

    public void setNews_EventDescription(String news_EventDescription) {
        News_EventDescription = news_EventDescription;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
