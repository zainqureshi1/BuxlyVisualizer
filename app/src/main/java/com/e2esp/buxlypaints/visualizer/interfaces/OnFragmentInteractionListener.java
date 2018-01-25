package com.e2esp.buxlypaints.visualizer.interfaces;

/**
 * Created by Zain on 1/31/2017.
 */

public interface OnFragmentInteractionListener {

    int DEFAULT_ROOM_SELECTED = 1000;
    int ROOM_SELECTED = 1001;
    int ROOMS_NEXT_CLICK = 1002;
    int COLOR_SELECTED = 1003;
    int COLORS_NEXT_CLICK = 1004;
    int PRODUCT_VIEW_COLORS_CLICK = 1005;

    void onInteraction(int type, Object obj);

}
