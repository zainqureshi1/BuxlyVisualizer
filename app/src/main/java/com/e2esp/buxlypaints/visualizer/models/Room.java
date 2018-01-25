package com.e2esp.buxlypaints.visualizer.models;

import java.util.ArrayList;

/**
 * Created by Zain on 1/30/2017.
 */

public class Room {
    private String name;
    private int imageRes;
    private int transparentImageRes;

    private ArrayList<Wall> wallsList;

    private boolean selected;

    public Room(String name, int imageRes, int transparentImageRes, ArrayList<Wall> wallsList) {
        this.name = name;
        this.imageRes = imageRes;
        this.transparentImageRes = transparentImageRes;
        this.wallsList = wallsList;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public int getTransparentImageRes() {
        return transparentImageRes;
    }

    public ArrayList<Wall> getWallsList() {
        return wallsList;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
