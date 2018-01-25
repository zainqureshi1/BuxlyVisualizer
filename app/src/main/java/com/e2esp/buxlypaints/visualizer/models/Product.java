package com.e2esp.buxlypaints.visualizer.models;

/**
 *
 * Created by Zain on 2/1/2017.
 */

public class Product {
    private String name;
    private String description;
    private String link;
    private int imageRes;
    private boolean hasColors;

    public Product(String name, String description, String link, int imageRes, boolean hasColors) {
        this.name = name;
        this.description = description;
        this.link = link;
        this.imageRes = imageRes;
        this.hasColors = hasColors;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public int getImageRes() {
        return imageRes;
    }

    public boolean hasColors() {
        return hasColors;
    }

}
