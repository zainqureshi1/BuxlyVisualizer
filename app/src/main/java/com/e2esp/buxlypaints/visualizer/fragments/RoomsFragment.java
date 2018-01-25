package com.e2esp.buxlypaints.visualizer.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.e2esp.buxlypaints.visualizer.R;
import com.e2esp.buxlypaints.visualizer.adapters.GridSpacingItemDecoration;
import com.e2esp.buxlypaints.visualizer.adapters.RoomRecyclerAdapter;
import com.e2esp.buxlypaints.visualizer.interfaces.OnFragmentInteractionListener;
import com.e2esp.buxlypaints.visualizer.interfaces.OnRoomClickListener;
import com.e2esp.buxlypaints.visualizer.models.Room;
import com.e2esp.buxlypaints.visualizer.models.Wall;
import com.e2esp.buxlypaints.visualizer.utils.Utility;

import java.util.ArrayList;

/**
 *
 * Created by Zain on 1/31/2017.
 */

public class RoomsFragment extends Fragment {

    private ArrayList<Room> arrayListRooms;
    private RoomRecyclerAdapter roomRecyclerAdapter;

    private Room selectedRoom;
    private OnFragmentInteractionListener onFragmentInteractionListener;

    public RoomsFragment() {
        // Required empty public constructor
    }

    public static RoomsFragment newInstance() {
        return new RoomsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            onFragmentInteractionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentInteractionListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        setupView(view);
        loadRooms();

        return view;
    }

    private void setupView(View view) {
        ImageView imageViewNext = view.findViewById(R.id.imageViewNext);
        imageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextClicked();
            }
        });

        RecyclerView recyclerViewRooms = view.findViewById(R.id.recyclerViewRooms);
        arrayListRooms = new ArrayList<>();
        roomRecyclerAdapter = new RoomRecyclerAdapter(getContext(), arrayListRooms, new OnRoomClickListener() {
            @Override
            public void onRoomClick(Room room) {
                selectRoom(room, false);
            }
        });

        recyclerViewRooms.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerViewRooms.addItemDecoration(new GridSpacingItemDecoration(2, Utility.dpToPx(getContext(), 10), true));
        recyclerViewRooms.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRooms.setAdapter(roomRecyclerAdapter);
    }

    private void loadRooms() {
        arrayListRooms.clear();

        ArrayList<Wall> walls;

        walls = new ArrayList<>();
        walls.add(new Wall("Front Wall", R.drawable.room_01_01, R.drawable.room_01_01_00, Color.parseColor("#57508E")));
        arrayListRooms.add(new Room("Open Bedroom", R.drawable.room_01, R.drawable.room_01_00, walls));

        walls = new ArrayList<>();
        walls.add(new Wall("Front Wall", R.drawable.room_02_01, R.drawable.room_02_01_00, Color.parseColor("#E58950")));
        arrayListRooms.add(new Room("Living Room", R.drawable.room_02, R.drawable.room_02_00, walls));

        walls = new ArrayList<>();
        walls.add(new Wall("Front Wall", R.drawable.room_03_01, R.drawable.room_03_01_00, Color.parseColor("#BF9C7A")));
        arrayListRooms.add(new Room("Living Room", R.drawable.room_03, R.drawable.room_03_00, walls));

        walls = new ArrayList<>();
        walls.add(new Wall("Front Wall", R.drawable.room_04_01, R.drawable.room_04_01_00, Color.parseColor("#B3CBCF")));
        walls.add(new Wall("Right Wall", R.drawable.room_04_02, R.drawable.room_04_02_00, Color.parseColor("#9EBBBB")));
        arrayListRooms.add(new Room("Bedroom", R.drawable.room_04, R.drawable.room_04_00, walls));

        walls = new ArrayList<>();
        walls.add(new Wall("Right Wall", R.drawable.room_05_01, R.drawable.room_05_01_00, Color.parseColor("#A6566A")));
        walls.add(new Wall("Left Wall", R.drawable.room_05_02, R.drawable.room_05_02_00, Color.parseColor("#FBDACB")));
        walls.add(new Wall("Back Wall", R.drawable.room_05_03, R.drawable.room_05_03_00, Color.parseColor("#EFCFC2")));
        arrayListRooms.add(new Room("Living Room", R.drawable.room_05, R.drawable.room_05_00, walls));

        walls = new ArrayList<>();
        walls.add(new Wall("Front Wall", R.drawable.room_06_01, R.drawable.room_06_01_00, Color.parseColor("#A3546D")));
        arrayListRooms.add(new Room("Drawing Room", R.drawable.room_06, R.drawable.room_06_00, walls));

        walls = new ArrayList<>();
        walls.add(new Wall("Front Wall", R.drawable.room_07_01, R.drawable.room_07_01_00, Color.parseColor("#A2A65C")));
        walls.add(new Wall("Inner Wall", R.drawable.room_07_02, R.drawable.room_07_02_00, Color.parseColor("#BEB692")));
        arrayListRooms.add(new Room("Sitting Area", R.drawable.room_07, R.drawable.room_07_00, walls));

        walls = new ArrayList<>();
        walls.add(new Wall("Left Wall", R.drawable.room_08_01, R.drawable.room_08_01_00, Color.parseColor("#ED9C19")));
        walls.add(new Wall("Right Wall", R.drawable.room_08_02, R.drawable.room_08_02_00, Color.parseColor("#CA3E47")));
        walls.add(new Wall("Pillar", R.drawable.room_08_03, R.drawable.room_08_03_00, Color.parseColor("#BDCC87")));
        arrayListRooms.add(new Room("Open Area", R.drawable.room_08, R.drawable.room_08_00, walls));

        walls = new ArrayList<>();
        walls.add(new Wall("Lower Section", R.drawable.room_09_01, R.drawable.room_09_01_00, Color.parseColor("#C4AD8B")));
        walls.add(new Wall("Middle Section", R.drawable.room_09_02, R.drawable.room_09_02_00, Color.parseColor("#D8C09E")));
        walls.add(new Wall("Top Section", R.drawable.room_09_03, R.drawable.room_09_03_00, Color.parseColor("#D1B993")));
        arrayListRooms.add(new Room("House Exterior", R.drawable.room_09, R.drawable.room_09_00, walls));

        walls = new ArrayList<>();
        walls.add(new Wall("Left Section", R.drawable.room_10_01, R.drawable.room_10_01_00, Color.parseColor("#82B0BC")));
        walls.add(new Wall("Front Section", R.drawable.room_10_02, R.drawable.room_10_02_00, Color.parseColor("#A7D6DE")));
        walls.add(new Wall("Right Section", R.drawable.room_10_03, R.drawable.room_10_03_00, Color.parseColor("#8AC6D0")));
        walls.add(new Wall("Garage", R.drawable.room_10_04, R.drawable.room_10_04_00, Color.parseColor("#8DBFCA")));
        arrayListRooms.add(new Room("Flats Exterior", R.drawable.room_10, R.drawable.room_10_00, walls));

        roomRecyclerAdapter.notifyDataSetChanged();

        selectRoom(arrayListRooms.get(0), true);
    }

    private void selectRoom(Room room, boolean def) {
        for (int i = 0; i < arrayListRooms.size(); i++) {
            arrayListRooms.get(i).setSelected(false);
        }
        selectedRoom = room;
        selectedRoom.setSelected(true);
        roomRecyclerAdapter.notifyDataSetChanged();

        onFragmentInteractionListener.onInteraction(def ? OnFragmentInteractionListener.DEFAULT_ROOM_SELECTED : OnFragmentInteractionListener.ROOM_SELECTED, selectedRoom);
    }

    private void nextClicked() {
        onFragmentInteractionListener.onInteraction(OnFragmentInteractionListener.ROOMS_NEXT_CLICK, selectedRoom);
    }

}
