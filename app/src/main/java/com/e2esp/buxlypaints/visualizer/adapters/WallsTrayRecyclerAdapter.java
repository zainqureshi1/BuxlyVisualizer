package com.e2esp.buxlypaints.visualizer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e2esp.buxlypaints.visualizer.R;
import com.e2esp.buxlypaints.visualizer.interfaces.OnTraysWallClickListener;
import com.e2esp.buxlypaints.visualizer.models.SecondaryColor;
import com.e2esp.buxlypaints.visualizer.models.Wall;

import java.util.ArrayList;

/**
 *
 * Created by Zain on 2/1/2017.
 */

public class WallsTrayRecyclerAdapter extends RecyclerView.Adapter<WallsTrayRecyclerAdapter.WallViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Wall> wallsList;
    private OnTraysWallClickListener onWallClickListener;

    public WallsTrayRecyclerAdapter(Context context, ArrayList<Wall> wallsList, OnTraysWallClickListener onWallClickListener) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.wallsList = wallsList;
        this.onWallClickListener = onWallClickListener;
    }

    @Override
    public WallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tray_item_wall_layout, parent, false);
        return new WallViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return wallsList.size();
    }

    @Override
    public void onBindViewHolder(WallViewHolder holder, int position) {
        holder.bindView(wallsList.get(position));
    }

    class WallViewHolder extends RecyclerView.ViewHolder {

        private View topView;
        private TextView textViewName;
        private TextView textViewColor;
        private TextView textViewCode;

        WallViewHolder(View itemView) {
            super(itemView);
            topView = itemView;
            textViewName = itemView.findViewById(R.id.textViewWallName);
            textViewColor = itemView.findViewById(R.id.textViewColorName);
            textViewCode = itemView.findViewById(R.id.textViewColorCode);
        }

        void bindView(final Wall wall) {
            textViewName.setText(wall.getName());
            SecondaryColor color = wall.getSelectedColor();
            if (color != null) {
                textViewColor.setText(color.getName());
                textViewColor.setTextColor(color.getColor());
                textViewCode.setText(color.getColorCode());
                textViewCode.setTextColor(color.getColor());
            } else {
                textViewColor.setText("");
                textViewCode.setText("");
            }
            if (wall.isSelected()) {
                topView.setBackgroundColor(Color.BLUE);
            } else {
                topView.setBackgroundColor(Color.TRANSPARENT);
            }
            topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWallClickListener.onWallClick(wall);
                }
            });
        }

    }

}
