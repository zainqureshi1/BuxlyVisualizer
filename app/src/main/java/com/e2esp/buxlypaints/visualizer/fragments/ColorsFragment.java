package com.e2esp.buxlypaints.visualizer.fragments;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.e2esp.buxlypaints.visualizer.R;
import com.e2esp.buxlypaints.visualizer.adapters.ColorsTrayRecyclerAdapter;
import com.e2esp.buxlypaints.visualizer.adapters.ProductsTrayRecyclerAdapter;
import com.e2esp.buxlypaints.visualizer.adapters.WallsTrayRecyclerAdapter;
import com.e2esp.buxlypaints.visualizer.interfaces.OnFragmentInteractionListener;
import com.e2esp.buxlypaints.visualizer.interfaces.OnTraysColorClickListener;
import com.e2esp.buxlypaints.visualizer.interfaces.OnTraysProductClickListener;
import com.e2esp.buxlypaints.visualizer.interfaces.OnTraysWallClickListener;
import com.e2esp.buxlypaints.visualizer.interfaces.OnWallImageTouchListener;
import com.e2esp.buxlypaints.visualizer.models.PrimaryColor;
import com.e2esp.buxlypaints.visualizer.models.ProductColor;
import com.e2esp.buxlypaints.visualizer.models.Room;
import com.e2esp.buxlypaints.visualizer.models.SecondaryColor;
import com.e2esp.buxlypaints.visualizer.models.Wall;
import com.e2esp.buxlypaints.visualizer.utils.PermissionManager;
import com.e2esp.buxlypaints.visualizer.utils.Utility;

import java.io.File;
import java.util.ArrayList;

public class ColorsFragment extends Fragment {

    private static ColorsFragment latestInstance;

    private AppCompatTextView textViewSymphonyColor;
    private AppCompatTextView textViewProductColor;
    private AppCompatTextView textViewShadesOfColor;
    private AppCompatTextView textViewColorsOfProduct;

    private RelativeLayout relativeLayoutPictureContainer;
    private RelativeLayout relativeLayoutColorsTrayContainer;
    private RelativeLayout relativeLayoutProductsTrayContainer;
    private LinearLayout linearLayoutSecondaryColorsTrayContainer;
    private LinearLayout linearLayoutProductColorsTrayContainer;

    private RecyclerView recyclerViewWallsTray;
    private ArrayList<Wall> wallsList;
    private WallsTrayRecyclerAdapter wallsRecyclerAdapter;

    private RecyclerView recyclerViewColorsTray;
    private ArrayList<PrimaryColor> allColorsList;
    private ArrayList<PrimaryColor> activeColorsList;
    private ColorsTrayRecyclerAdapter colorsRecyclerAdapter;

    private RecyclerView recyclerViewProductsTray;
    private ArrayList<ProductColor> allProductColorsList;
    private ArrayList<ProductColor> activeProductColorsList;
    private ProductsTrayRecyclerAdapter productsRecyclerAdapter;

    private Room room;

    private Wall selectedWall;

    private OnFragmentInteractionListener onFragmentInteractionListener;

    private boolean colorsTrayVisible;
    private boolean productsTrayVisible;

    public ColorsFragment() {
        // Required empty public constructor
    }

    public static ColorsFragment newInstance() {
        latestInstance = new ColorsFragment();
        return latestInstance;
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
        View view = inflater.inflate(R.layout.fragment_colors, container, false);

        setupViews(view);
        setupColorsTray();
        setupProductsTray();

        return view;
    }

    private void setupViews(View view) {
        textViewSymphonyColor = view.findViewById(R.id.textViewSymphonyColors);
        textViewSymphonyColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colorsTrayVisible) {
                    hideColorsTray();
                } else {
                    showColorsTray();
                }
            }
        });
        textViewProductColor = view.findViewById(R.id.textViewProductColors);
        textViewProductColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productsTrayVisible) {
                    hideProductsTray();
                } else {
                    showProductsTray();
                }
            }
        });

        textViewShadesOfColor = view.findViewById(R.id.textViewShadesOfColor);
        textViewColorsOfProduct = view.findViewById(R.id.textViewColorsOfProduct);

        relativeLayoutPictureContainer = view.findViewById(R.id.relativeLayoutPictureContainer);
        relativeLayoutColorsTrayContainer = view.findViewById(R.id.relativeLayoutColorsTrayContainer);
        relativeLayoutProductsTrayContainer = view.findViewById(R.id.relativeLayoutProductsTrayContainer);
        linearLayoutSecondaryColorsTrayContainer = view.findViewById(R.id.linearLayoutSecondaryColorsTrayContainer);
        linearLayoutProductColorsTrayContainer = view.findViewById(R.id.linearLayoutProductColorsTrayContainer);

        recyclerViewWallsTray = view.findViewById(R.id.recyclerViewWallsTray);
        wallsList = new ArrayList<>();
        wallsRecyclerAdapter = new WallsTrayRecyclerAdapter(getContext(), wallsList, new OnTraysWallClickListener() {
            @Override
            public void onWallClick(Wall wall) {
                wallClicked(wall);
            }
        });
        recyclerViewWallsTray.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewWallsTray.setAdapter(wallsRecyclerAdapter);

        recyclerViewColorsTray = view.findViewById(R.id.recyclerViewColorsTray);
        allColorsList = new ArrayList<>();
        activeColorsList = new ArrayList<>();
        colorsRecyclerAdapter = new ColorsTrayRecyclerAdapter(getContext(), activeColorsList, new OnTraysColorClickListener() {
            @Override
            public void onPrimaryColorClick(PrimaryColor color) {
                primaryColorClicked(color);
            }
        });
        recyclerViewColorsTray.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewColorsTray.setAdapter(colorsRecyclerAdapter);

        recyclerViewProductsTray = view.findViewById(R.id.recyclerViewProductsTray);
        allProductColorsList = new ArrayList<>();
        activeProductColorsList = new ArrayList<>();
        productsRecyclerAdapter = new ProductsTrayRecyclerAdapter(getContext(), activeProductColorsList, new OnTraysProductClickListener() {
            @Override
            public void onProductColorClick(ProductColor color) {
                productColorClicked(color);
            }
        });
        recyclerViewProductsTray.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewProductsTray.setAdapter(productsRecyclerAdapter);

        ImageView imageViewSave = view.findViewById(R.id.imageViewSave);
        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndSaveScreenshot();
            }
        });

        ImageView imageViewNext = view.findViewById(R.id.imageViewNext);
        imageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextClicked();
            }
        });
    }

    private void nextClicked() {
        onFragmentInteractionListener.onInteraction(OnFragmentInteractionListener.COLORS_NEXT_CLICK, null);
    }

    private void wallClicked(Wall wall) {
        selectedWall = wall;
        hideAllWallsBorders();
        selectedWall.showBorder(true);
        deselectAllWalls();
        selectedWall.setSelected(true);
        wallsRecyclerAdapter.notifyDataSetChanged();
    }

    private void hideAllWallsBorders() {
        for (int i = 0; i < wallsList.size(); i++) {
            wallsList.get(i).showBorder(false);
        }
    }

    private void deselectAllWalls() {
        for (int i = 0; i < wallsList.size(); i++) {
            wallsList.get(i).setSelected(false);
        }
    }

    private void setupColorsTray() {
        ArrayList<SecondaryColor> secondaryColors;

        // Shades of White
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f8e6d5"), "Peach Shadow", "3-6-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f7f1e1"), "Almond Torte", "3-8-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f2ead7"), "Timid Yellow", "2-9-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f1e4e1"), "Romance", "3-1-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f6e3e1"), "Hush Rose", "3-1-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#eee4e4"), "Alpine Pink", "1-47-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f4eae9"), "Pink Champagne", "1-48-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f6e7e9"), "Cherry Blossom Pink", "1-47-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#efe7d3"), "Downy Fluff", "3-11-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f0eadd"), "Vanilla Slices", "3-9-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f2eed6"), "Harvest Moon", "3-13-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f9ecde"), "Apricot Ice", "2-4-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f9ece5"), "Sweet Rose", "2-2-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#eceeda"), "Scotch Butter", "1-15-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e4eae7"), "Empress Teal", "1-28-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e1e9db"), "Spindrift", "1-24-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#edf0d4"), "Daydream", "1-16-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#eff0db"), "Pale Star", "3-16-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#edf0cc"), "Ambrosia", "3-16-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ebebd5"), "Ivory Hue", "3-15-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#efeed3"), "Pale Daffodil", "3-14-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e7f1cd"), "Sonata", "1-16-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e5efd5"), "Opalescence", "2-18-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dcedeb"), "Impressionist Sky", "2-29-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e9e8e9"), "Calm Spirit", "2-39-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e3e9e9"), "Gossamer Down", "3-35-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#deede9"), "Wispy", "3-30-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e1efdf"), "Mint Frost", "3-25-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e0eecb"), "Green Myth", "2-18-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d9edde"), "Fruity Flavour", "2-25-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d9ecd8"), "Drifting Spirit", "2-24-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dcead6"), "White Asparagus", "2-24-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d8ecd7"), "Green Chiffon", "2-23-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d1ecdd"), "Softly Chiming", "2-25-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e2eedd"), "Spring Splash", "2-23-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e9f2cd"), "Absinthe Mist", "2-20-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e0edd1"), "Enchanted Mist", "2-21-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dce8d8"), "Winter Garden", "3-22-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e7f1df"), "Papaya Cream", "3-20-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e9eddb"), "Sublime", "3-18-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e9efdf"), "Sweet Surrender", "3-22-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dae5c4"), "Willowbrook", "3-18-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e0ead7"), "Eternal Bliss", "3-20-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d6eacf"), "Cool Comfort", "2-21-2"));

        allColorsList.add(new PrimaryColor(Color.parseColor("#e9e8e9"), "Calm Spirit", "White", secondaryColors));

        // Shades of Yellow
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fceeb3"), "Sassy Yellow", "1-12-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f6f1ae"), "Yellow Duchess", "1-15-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f1eb9b"), "Sensation", "1-15-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fce18e"), "Banana Peel", "1-11-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ece86f"), "Fluorite", "1-15-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f3db47"), "The Louvre", "1-15-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f7d867"), "Bright Marigold", "1-12-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e5cd42"), "Electric Yellow", "1-15-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ffce61"), "Lemon", "1-10-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#febf39"), "Banana Alley", "1-10-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f6ab00"), "Duckling", "1-10-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#caa800"), "Yangtze", "2-15-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fabd00"), "Gold Finger", "1-12-7"));

        allColorsList.add(new PrimaryColor(Color.parseColor("#fabd00"), "Gold Finger", "Yellow", secondaryColors));

        // Shades of Red
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f5e5d8"), "Southern Peach", "3-1-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f9e6e2"), "Silk Knot", "1-1-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f7ead5"), "Star Flower", "1-8-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f8ebda"), "Embracing Peach", "1-7-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f8e5c5"), "Acapulco Sun", "1-8-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ead0bd"), "Dusty Apricot", "3-6-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e9d9c2"), "Desert Isle", "3-8-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f9d8c6"), "Apricot Blush", "1-5-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f3dbc1"), "Demure Dreams", "1-7-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f8ddcf"), "Misty Peach", "2-4-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fcccb5"), "Perfectly Peach", "1-5-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ecd2c4"), "Peach Melba", "3-4-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fecea9"), "Sunburst", "1-7-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f7d0cc"), "Fetching Pink", "1-1-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ebcdcb"), "Caress", "3-1-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ebcfc5"), "Subtle Orange", "2-2-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f0c9b9"), "Little Angel", "2-4-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#eecbbc"), "Sheer Scarf", "2-2-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fdba9e"), "Fire Princess", "1-5-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ffb8a6"), "Peach Bloom", "1-3-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fbb5ac"), "Fallen Petal", "1-1-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f0b4ae"), "Jericho Rose", "2-2-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e9bfae"), "Prairie Peach", "3-4-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ddaa99"), "Orient Blush", "3-4-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f3c6a7"), "Carotine", "2-6-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e1b19a"), "Peaches'n Cream", "3-6-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d8a286"), "Frosty Melon", "3-6-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cd8d70"), "Spiced Nectarine", "3-6-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f4ae96"), "Rockmelon", "2-4-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e3886e"), "Terrazzo", "2-4-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f1b18f"), "Nutmeg Swirl", "2-6-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e69970"), "Tropical Coral", "2-6-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e38657"), "Coral Blossom", "2-6-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fc9d86"), "Crepe", "1-3-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f68c6f"), "Mandras", "1-3-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fc8456"), "Siesta", "1-5-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ef7c8c"), "Whoopee", "1-50-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e66579"), "Frosted Rose", "1-50-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d25569"), "Cerise Delight", "1-50-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d55e86"), "Romantic", "1-48-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#df799a"), "Ceramic Pink", "1-48-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d79f9d"), "Pink Dusk", "3-1-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f19ca2"), "Bare Essence", "1-50-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c47875"), "Rose Memory", "3-1-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f87f76"), "Coral Reef", "1-1-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e75b5b"), "Chintz", "1-1-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cf3b40"), "Delectable", "1-1-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c35447"), "Shell Coral", "2-4-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b7452c"), "Indian Summer", "2-4-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#da4736"), "Signorina", "1-3-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e7847e"), "Mango", "2-2-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e26866"), "Garden Fragrance", "2-2-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bf5050"), "Brandy Flame", "2-2-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c4446c"), "Czarina", "1-48-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9d2e30"), "Blaze", "3-1-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a84c4b"), "Bravado", "3-1-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#aa5a47"), "Coral Bead", "3-4-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9a452a"), "Apache", "3-4-7"));

        allColorsList.add(new PrimaryColor(Color.parseColor("#9d2e30"), "Blaze", "Red", secondaryColors));

        // Shades of Blue
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e4f1e9"), "Sea Crystal", "1-26-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d9eaec"), "Nil Blue", "1-33-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dceded"), "Cloud Nine", "1-32-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e0e7eb"), "Lakeside Mist", "1-36-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d2e7e6"), "Lightning Ridge", "2-29-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e0efeb"), "Blue Lagoon", "1-30-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d7e7ea"), "Dreaming Blue", "1-34-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e2ede2"), "Clair De Lune", "3-26-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d8e8db"), "Seaflower", "3-26-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bfd8e4"), "Star of Bethlehem", "1-34-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c4e1e6"), "Icy Fjord", "1-32-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d3dadd"), "Sensitive Blue", "3-36-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dadfe9"), "Nice Day", "2-37-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d2e3eb"), "Cool Hint", "3-35-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c2e1e1"), "Hydro", "1-30-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d5dfdf"), "Sirrus Sky", "2-32-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d4e6e5"), "Spring Tide", "2-27-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d9e7d8"), "Spun Green", "3-25-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c1ddce"), "Jade Isle", "3-26-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d6e5e5"), "Shy Sky", "3-33-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c0dfe0"), "Hidden Mirage", "3-30-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a7d0d2"), "Rain Song", "3-30-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#5794b3"), "Sea Breeze", "3-33-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c7e7e7"), "Buenos Aires", "1-28-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bbdde6"), "Ocean Mist", "1-33-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c5d5e2"), "Pyrenees Morn", "1-36-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c9e9e4"), "Glacier Wall", "2-27-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9ed4ca"), "Cielo", "2-27-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bfd6e0"), "Fiji Waters", "3-33-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a7c8d8"), "Baby Blue", "3-33-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#8bb4cb"), "Skyline", "3-33-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c5d5e0"), "Kind Sky", "3-36-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ced3e0"), "Mystic Blue", "2-37-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d6d7e4"), "Lavender Dream", "2-39-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a9c5d4"), "Icy Morn", "3-35-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b9d9e3"), "Fragile Blue", "2-32-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cdedd8"), "Crisp'N Clear", "1-24-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c7eadd"), "Sea Isle", "1-26-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d5dae7"), "Violet Veil", "1-38-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ccd3e8"), "Violet Note", "1-38-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9cdae2"), "Babbling Brook", "1-30-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a3e3cf"), "Spring Stream", "1-26-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9ce0dc"), "Scandinavian Sky", "1-28-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#6ccdb7"), "Pacific Palisade", "1-26-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9bd4e5"), "Sky Delight", "1-32-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a1cbe4"), "Bluebird", "1-34-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#85c6e3"), "Watercolor Blue", "1-33-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#72d0ce"), "Title River", "1-28-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#6ec2de"), "Blue Heaven", "1-32-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9fd3eb"), "Heavenly Day", "1-33-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9dd6d9"), "Bogong Ranges", "2-29-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#96cbdb"), "Serene Blue", "2-32-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a3bee1"), "Queenscliff", "1-36-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#6fb1da"), "Dresden Blue", "1-34-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#67b6d1"), "Blue Opal", "2-32-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a7b8d7"), "Northern Sky", "2-37-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#aeb8dd"), "Panache", "1-38-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#77a1bb"), "Crushed Blue Stone", "3-35-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#84a0d0"), "Riverside Blue", "1-36-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#939ad0"), "Paradise", "1-38-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#5c84c1"), "Azure", "1-36-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#4a6db5"), "American Blue", "1-36-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#007bb0"), "Stargazer", "1-33-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#68c7d4"), "Seaspray", "1-30-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#66bfc5"), "Great Divide", "2-29-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#43afd6"), "Clear Sailing", "1-33-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#49bcce"), "Ocean Wave", "1-30-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#009faf"), "Caribbean", "1-30-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#0094bf"), "Airway", "1-32-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#3494b7"), "Crispy Blue", "2-32-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#0090c8"), "Sky High", "1-33-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#3e96cc"), "Blue Madonna", "1-34-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#0084c3"), "Mirage Lake", "1-34-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#427a9e"), "Gentle Rain", "3-35-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#008fa8"), "Peacock", "1-30-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#189da7"), "Cavalier", "2-29-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#0080a6"), "North Shore", "1-32-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#74b6bc"), "Still Tide Water", "3-30-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#399097"), "Tropical Sea", "3-30-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#00a5a6"), "Peacock Plume", "1-28-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#009193"), "Deep Reflection", "1-28-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#008a97"), "Voyager Sky", "2-29-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#007984"), "Paradise Lost", "3-30-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#007782"), "Cape Morten", "2-29-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#8396c5"), "Blue Thoughts", "2-37-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#5b6da6"), "Daydream Island", "2-37-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#445699"), "Blue Spell", "2-37-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#2e579e"), "Electric Blue", "1-36-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#166a93"), "Upstream", "3-33-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#00779b"), "Schooner", "2-32-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#00668d"), "Azurean", "2-32-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#005b66"), "Mosaic", "3-30-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#006792"), "Rainy Night", "3-35-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#004a75"), "Blue Vision", "3-35-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#004f75"), "Victor Harbour", "3-33-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#273a7c"), "Lobelia", "2-37-7"));

        allColorsList.add(new PrimaryColor(Color.parseColor("#004a75"), "Blue Vision", "Blue", secondaryColors));

        // Shades of Orange
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f6eddc"), "Aspiration", "2-6-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f9e6ae"), "Parmesan", "1-10-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fee094"), "Sunny", "1-10-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ffd77f"), "Bumble Bee", "1-10-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fed762"), "Flicker", "1-11-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fede6f"), "Sassy Daisy", "1-12-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fed362"), "Sunspray", "1-11-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#edc47f"), "Mandolin Gold", "2-9-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fed762"), "Flicker", "1-11-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fede6f"), "Sassy Daisy", "1-12-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fed362"), "Sunspray", "1-11-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#edc47f"), "Mandolin Gold", "2-9-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fecea9"), "Sunburst", "1-7-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fdd9a8"), "Golden Girl", "1-8-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f3cfa3"), "Phoenix Sun", "2-8-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fcc98e"), "Fairy Gold", "1-8-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ffc092"), "Corallina", "1-7-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#feb86b"), "Mandarin", "1-8-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ffad66"), "Zinnia Scent", "1-7-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fca77c"), "Jaffa Orange", "1-5-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ffc136"), "Hidden Sun", "1-11-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f7ae00"), "Fairy Lights", "1-11-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fea534"), "Orange Peel", "1-8-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f9902b"), "Bird of Paradise", "1-7-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ef7d11"), "Sunblest Poppy", "1-7-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cd8626"), "Sunshine Coast", "2-9-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ed7242"), "Jamaica", "1-5-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e85d3a"), "Orange Harvest", "1-3-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#da4736"), "Signorina", "1-3-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f4e1ce"), "Peach Pinch", "2-6-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ffea96"), "Buttercup Bouquet", "1-12-3"));

        allColorsList.add(new PrimaryColor(Color.parseColor("#e85d3a"), "Orange Harvest", "Orange", secondaryColors));

        // Shades of Green
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#edf0be"), "Tiffany", "1-15-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e6efb7"), "Silent Bliss", "1-16-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ecf1d0"), "Reflecting Green", "1-17-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dfefbc"), "Leaf Green", "1-17-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d2e5bf"), "Green Tease", "1-19-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dcedcf"), "Elfin", "1-19-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d6eccf"), "Forever", "1-21-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cee6c3"), "Cool Sherbet", "1-21-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d8ecd3"), "Melon Ice", "3-24-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d6e8d2"), "Fleecy Green", "3-24-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c7e8b0"), "Summer House", "1-19-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d1e89c"), "Fluorescent", "1-17-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e8eec2"), "Green Gleam", "3-17-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dde1b0"), "Green Whisper", "3-16-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e8eb98"), "Forbidden City", "1-16-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c3e7b4"), "Ariel", "1-21-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bbe094"), "Artesian Well", "1-19-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c1db71"), "East Of Eden", "1-17-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dbe069"), "Shangri-La", "1-16-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ccd01e"), "Mystical", "1-16-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bbc200"), "Oz", "1-16-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a0ccac"), "Alpine Haze", "3-25-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b0e3b8"), "Meadowsweet", "1-24-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#86d099"), "Green Haze", "1-24-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d2dfb2"), "Fern Lane", "3-17-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#becc8c"), "Nottingham", "3-17-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ced9b0"), "Windsor Meadow", "3-18-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#93cc62"), "Jewels", "1-19-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bed6b1"), "Spearmint Miss", "3-20-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b6ca8f"), "Paris Green", "3-18-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#99af65"), "Light Everglade", "3-18-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9ea951"), "Olive Branch", "3-17-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#78832b"), "Chameleon", "3-17-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#918925"), "Olive Meadow", "3-16-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b2d1a4"), "Secret Haven", "3-20-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#90b77b"), "Ripple Green", "3-20-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bfd8b9"), "Dewmist Delight", "3-22-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#aecda6"), "Jasmine Scent", "3-22-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#8bb47c"), "Breath Of Spring", "3-22-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#6c9546"), "Celtic", "3-20-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d4e8c0"), "Key Lime", "2-20-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c6e1ba"), "Azuro", "2-21-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dde5c3"), "Timid Green", "3-17-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d6e6c1"), "Willow Green", "2-18-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b4deb6"), "Wild Mint", "2-23-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c0e0b3"), "Maureen", "2-20-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bbdca2"), "Melon Green", "2-18-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b0dd97"), "Mint Tone", "1-21-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d0e4d1"), "Sea Meadow", "3-25-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bad9bd"), "Wild Grasses", "3-24-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a2cca7"), "Eden Prairie", "3-24-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#94c7ae"), "Melbourne", "3-26-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9ece8f"), "Cool Vista", "2-21-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a5ca6c"), "Inviting Green", "2-18-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#7bc4a0"), "Meditation", "2-25-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#66c3b8"), "Wave", "2-27-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#acdbbe"), "Green Acre", "2-25-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#acddbb"), "Gentility", "2-24-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#97cc93"), "Placid Green", "2-23-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#80c897"), "Grazing Field", "2-24-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#56bf7f"), "Twig Green", "1-24-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#36b06b"), "Green Summit", "1-24-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#3da581"), "Mint Extract", "2-25-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#5ea988"), "Alpine", "3-26-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#86cb69"), "Limerick", "1-21-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#6bb843"), "Debonair", "1-21-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#5aa731"), "Pixieland", "1-21-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#88af40"), "Spring Kiss", "2-18-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#84a046"), "Island Palm", "3-18-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#6e842b"), "Crispy Lettuce", "3-18-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#567e29"), "Promise Land", "3-20-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#00aa8e"), "Ocean Gardens", "1-26-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#009c80"), "Aquarelle", "1-26-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#36bc9f"), "Sea Fascination", "1-26-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#00a393"), "Capri Grotto", "2-27-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#008c7f"), "Scuba", "2-27-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#00736c"), "Deep Sea Green", "2-27-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#009b52"), "Everglade", "1-24-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#92c772"), "Merry Green", "2-20-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#6faa48"), "Festival", "2-20-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#82b965"), "Wild Apple", "2-20-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#5d8c44"), "Willow Herb", "3-22-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#42752b"), "Shamrock Isle", "3-2-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#abce3c"), "Spring Eve", "1-17-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#97ba19"), "Green Sulphur", "1-17-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#769d1d"), "Leprechaun", "2-18-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#5aa731"), "Pixieland", "1-21-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#84c141"), "Green Island", "1-19-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#71b328"), "Spring Burst", "1-19-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#509426"), "Sea Holly", "2-20-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#64b26a"), "Blarney Stone", "2-23-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#77b564"), "Green Terrace", "2-21-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#63a34f"), "Grace Green", "2-21-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#429033"), "Lime Peel", "2-21-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#209555"), "Exquisite", "2-24-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#54a873"), "Aspen Meadow", "2-24-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#54a873"), "Aspen Meadow", "2-24-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#77ac82"), "Green Pastures", "3-24-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#68ab80"), "Mistral Breeze", "3-25-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#2f8551"), "Verdigris", "3-25-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#3f8852"), "Arboretum", "3-24-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#28852c"), "Eden", "2-23-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#007837"), "Tropic Green", "2-24-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#0c5e30"), "Echuca Green", "3-25-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#216d32"), "Brogue Green", "3-24-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#187e5a"), "Sea Silence", "3-26-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#008e63"), "Willawong", "2-25-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#006240"), "Noonday Tide", "3-26-7"));

        allColorsList.add(new PrimaryColor(Color.parseColor("#007837"), "Tropic Green", "Green", secondaryColors));

        // Shades of Violet
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f5efee"), "Sleepy Pink", "1-45-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f4e7ea"), "Rock Candy", "1-50-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ede6e8"), "Swan Lake", "1-43-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e6dbe3"), "Geisha Pink", "1-42-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f0e2e1"), "Pink Lemonade", "1-48-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f5e2e5"), "English Rose", "2-46-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f1e0e8"), "Sugar Glider", "1-45-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e0e0eb"), "Ophelia Violet", "1-40-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e3dce4"), "Shooting Star", "2-41-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d9d1de"), "Mystic Iris", "2-41-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dcd2e2"), "Chaste", "1-42-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cbc8e3"), "Fascination", "1-40-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e1d3e4"), "Slipper Pink", "1-43-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f3e0ea"), "Pink Chill", "2-44-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#eddae5"), "Lady Pink", "2-43-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f8d5d7"), "Shell's Illusion", "1-50-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#edd4dc"), "Candle Light Pink", "2-46-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f2c7d8"), "Pink Elephant", "1-47-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e7d3de"), "Bridal Pink", "2-44-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#beb3d3"), "Easter Egg", "2-41-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e1c9da"), "Rose Quartz", "2-43-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e8c6de"), "Timeless Pink", "1-45-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d5b6d6"), "Azalea", "1-43-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b5b5d5"), "Light Grape", "2-39-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bdb7da"), "Windflower", "1-40-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d5bade"), "Arabesque", "1-42-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f2b5c4"), "Bubble Gum", "1-48-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f9babd"), "Gaponica", "1-50-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ea9eb5"), "Flowering Cherry", "1-48-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d976a1"), "Amore", "1-47-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e18bbd"), "Pink Pirouette", "1-45-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cd65a3"), "Sweet Bouquet", "1-45-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#eda7c7"), "Bikini", "1-47-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e9add2"), "Angel Food", "1-45-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d295ae"), "Snowbush Rose", "2-46-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e1bad5"), "Orlean's Rose", "2-44-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d8b2d0"), "Calamine", "2-43-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c79dcd"), "Cupid", "1-43-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c499c1"), "Ethereal Rose", "2-43-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#be80af"), "Chiffon Rose", "2-43-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ba99cc"), "Velvet Touch", "1-42-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a99bd0"), "Mystic Orchid", "1-40-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ab90c1"), "Pale Pansy", "2-41-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a37ab7"), "Swirl", "1-42-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cb97bb"), "Opera Pink", "2-44-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b46f9f"), "Pink Velvet", "2-44-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ac6aaa"), "Floral Jardin", "1-43-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9c5799"), "Pink Parasol", "1-43-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#896ba3"), "Violet Harmony", "2-41-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#777abe"), "Lupine", "1-38-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#7b5fa9"), "Lavender Lace", "1-40-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#8d79bc"), "Shadow Lilac", "1-40-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#7156a0"), "Purple Empire", "1-40-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9792bc"), "Maytime Blossom", "2-39-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#6b6bb0"), "Magical", "1-38-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#4f4f97"), "Masquerade", "1-38-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#746ba6"), "Easter Bonnet", "2-39-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#845190"), "Sweet Sachet", "1-42-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9968a7"), "Frosted Tulip", "1-42-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#635594"), "Grape sherbet", "2-39-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#4c3e7b"), "Purple Gumdrop", "2-39-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#775690"), "Afternoon Delight", "2-41-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#68437e"), "Purple Tryst", "2-41-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a14f83"), "Ashley Pink", "2-44-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ca6290"), "Untamed", "1-47-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b86a8e"), "Columbine", "2-46-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#863671"), "Cyclamen", "2-43-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9e518a"), "Raspberry Ice", "2-43-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bf4386"), "Lipstick", "1-45-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b64074"), "Rosella", "1-47-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ab436f"), "Pink Interlude", "2-46-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#981c53"), "Scarlet O'Hara", "2-46-7A"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#913168"), "Razzle Dazzle", "2-44-7"));

        allColorsList.add(new PrimaryColor(Color.parseColor("#ab436f"), "Pink Interlude", "Violet", secondaryColors));

        // Shades of Brown
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f6f3d8"), "Wonderment", "2-15-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#eee9d1"), "Shantung", "3-12-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#eeedc8"), "Yellow Leaf", "2-15-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f7ebd3"), "Sunstone", "2-8-1"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#efe0c5"), "Silica Sand", "3-9-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f2e9af"), "All Gold", "2-15-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ebe1b9"), "Clear Corona", "3-13-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e9e0bd"), "Haystack", "3-12-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ebdebc"), "Wheat Husk", "3-11-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e8e0ae"), "Golden Nectar", "3-13-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e4d49c"), "Golden Wonder", "3-12-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e3dda5"), "Wheatmeal", "3-14-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dedea8"), "Isinglass", "3-15-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e3e3bb"), "Egg Custard", "3-14-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ece69e"), "Marsh Gold", "2-15-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e5d697"), "Sweet Goldilocks", "3-13-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e3d89c"), "Daisy Hill", "3-14-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dad48c"), "Golden Delicious", "3-15-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d0c87b"), "Document", "3-15-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dec980"), "Honeynut", "3-12-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d6c376"), "Honeysweet", "3-13-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d5c778"), "Daintree Road", "3-14-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#eed5a2"), "Gosling", "2-9-2"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e7d8ac"), "Amber Light", "3-11-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e0cb94"), "Vanilla Wafer", "3-11-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d2c14d"), "Honey Bar", "2-15-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ded273"), "Honey Dew", "2-15-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ccbe63"), "Batten", "3-15-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cfd18b"), "Sprout", "3-16-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c6c477"), "Weeping Willow", "3-16-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#aca54e"), "Fancy Free", "3-16-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c7ab4b"), "Filigree Gold", "3-13-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d2b570"), "Fool's Gold", "3-11-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d8c197"), "French Mustard", "3-9-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#debf99"), "Sunstraw", "3-8-3"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cdb364"), "Mountain Lion", "3-12-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b9c276"), "Snowpea", "3-17-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b5a750"), "Olympia", "3-15-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bcaa4e"), "Bake Gold", "3-14-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c4a044"), "English Mustard", "3-12-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ba963c"), "Golden Straw Hat", "3-11-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d0ac6e"), "Midas Touch", "3-9-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c99f6d"), "Organza", "3-8-4"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c79b57"), "Honey Butter", "3-9-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#be8e46"), "Lion's Den", "3-9-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a98437"), "Granary Gold", "3-12-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c08949"), "Mystic Maize", "3-9-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c28b55"), "Copper Canyon", "3-8-5"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b97e41"), "Scorcher", "3-8-6"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ad7426"), "Morocco", "3-8-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ae8f2a"), "Golden Glimmer", "3-13-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ad7426"), "Morocco", "3-8-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ad8024"), "Canary Wing", "3-11-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a85f26"), "Wyoming", "3-6-7"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ba6f44"), "Arizona Sunset", "3-6-6"));

        allColorsList.add(new PrimaryColor(Color.parseColor("#a85f26"), "Wyoming", "Brown", secondaryColors));
    }

    private void showColorsTray() {
        hideProductsTray();
        colorsTrayVisible = true;
        relativeLayoutColorsTrayContainer.setVisibility(View.VISIBLE);
        relativeLayoutColorsTrayContainer.bringToFront();
        showColorsTray(0);
    }

    private void showColorsTray(final int iteration) {
        if (iteration >= allColorsList.size() || !colorsTrayVisible) {
            return;
        }
        activeColorsList.add(allColorsList.get(iteration).clone());
        colorsRecyclerAdapter.notifyDataSetChanged();
        recyclerViewColorsTray.post(new Runnable() {
            @Override
            public void run() {
                showColorsTray(iteration + 1);
            }
        });
    }

    private void hideColorsTray() {
        colorsTrayVisible = false;
        relativeLayoutColorsTrayContainer.setVisibility(View.GONE);
        activeColorsList.clear();
        colorsRecyclerAdapter.notifyDataSetChanged();
        textViewShadesOfColor.setText("");
        linearLayoutSecondaryColorsTrayContainer.removeAllViews();
        relativeLayoutPictureContainer.bringToFront();
    }

    private void setupProductsTray() {
        ArrayList<SecondaryColor> secondaryColors;

        // Imperial Matt Emulsion
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#7f4243"), "Tile Red", "3122"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#83804d"), "Leaf Green", "6212"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#99675f"), "Sandal Wood", "3084"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#68554e"), "Dark Choclate", "3090"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a68551"), "Gold Mist", "1162"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#3d6098"), "Morocco Blue", "5124"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dba07f"), "Badami Delight", "3082"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#b5d3e9"), "Aquamarine", "5129"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f4f7f6"), "White", "9207"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f7f3ea"), "Off White", "9215"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e6e5e3"), "Lavender White", "9493"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#9db9dc"), "Blue Passion", "5136"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#ecb7a5"), "Spring Song", "8087"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e0dcd3"), "Ash White", "9279"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#deb8af"), "Kasuri Burfi", "8073"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#cdcadf"), "Grey Dawn", "4046"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#8877a6"), "Royal Purple", "8065"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e8a3a9"), "Pink Bliss", "8074"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#f6eace"), "Off White Plus", "9286"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e5ccca"), "Summer Pink", "4068"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dbd5e3"), "Classic Violet", "8051"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e0c8d8"), "Blossom Pink", "4059"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dac9b2"), "Cockle Shell", "1343"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e9cfbc"), "Peach Delight", "1157"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dfb9cf"), "Swiss Miss", "4060"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d9dadc"), "April Moon", "4062"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#a2c8cf"), "Saif Ul Maluk", "5135"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#e98e53"), "Orange Fizz", "3113"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dac5af"), "Desert Safari", "1160"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#d2d67e"), "Marine Green", "6152"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#bb70a4"), "Carnival Pink", "4069"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#fac166"), "Mango Tango", "1161"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c1745a"), "Fiery Volcano", "3088"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#dbe4ad"), "Heaven Grass", "6135"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#c5dec0"), "Mint Leaf", "6136"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#A0A19C"), "Rock Grey", "7136"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#D3D1CB"), "Rain Cloud", "9288"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F3E2D7"), "Rose Petal", "3127"));

        allProductColorsList.add(new ProductColor("Imperial Matt Emulsion", R.drawable.buxly_imperial_matt_emulsion, secondaryColors));

        // Weather Fighter
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#3a5968"), "Cobalt Blue", "5079"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E9E1D5"), "Badami", "8078"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#DDDAD0"), "Ash White", "9279"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C8AF8F"), "Beige", "1318"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#646361"), "Coal Mine", "7372"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#D8C6B3"), "Fawn", "1376"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#A0A19D"), "Goose Wing", "7204"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F1E7D1"), "Off White", "9215"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#59665C"), "Olive Green", "6289"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#754842"), "Red Oxide", "3231"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#A9675E"), "Roof Tile", "3184"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#CDBBA6"), "Sand Stone", "1429"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C5C7C4"), "Smoke Grey", "7302"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E0CFB3"), "Sugar Cane", "1112"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E4BEAB"), "Sweet Jewel", "8055"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C5B2A9"), "Tea Rose", "8182"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#B9846E"), "Terracotta", "7428"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#AD615C"), "Tile Red", "3051"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EFF1EF"), "White", "9207"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#AE6144"), "Multani Tile Brown", "3165"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#D56E78"), "Pindi Pink", "8112"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#DC884F"), "Bright orange", "3113"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EED9C4"), "Peach", "3048"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F0E2A5"), "Banana Yellow", "1189"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EFDEDF"), "Rose Petal", "8110"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#91D2D4"), "Arabian Sea", "5153"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C4D4E0"), "Pool Blue", "5149"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C6C3BB"), "Light Grey", "7313"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#D8B794"), "Cameo", "1447"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E0CFB5"), "Magnolia", "1241"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C5C1A9"), "Dove Grey", "7136"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#BAAF9E"), "Clay", "6395"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#D5CAB4"), "Dust", "7082"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#DFCE91"), "Mango Feast", "13115"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E0BB93"), "Honey Bee", "10275"));

        allProductColorsList.add(new ProductColor("Weather Fighter", R.drawable.buxly_weather_fighter, secondaryColors));

        // Stylekoat Semi Plastic Emulsion
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E7E2DB"), "Dove White", "9495"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E3D8C9"), "Cockle Shell", "1343"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E7BCB6"), "Apricot", "2007"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E1DFDC"), "Lavender White.", "9493"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EFEBD8"), "Off White.", "9215"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#DE6D8B"), "PERKY PEACH ", "10364"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EFD5CD"), "ROSE WHITE", "10242"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EAC2C4"), "KASHMIRI CHAI ", "10323"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E8CEB7"), "PEACH GODDESS ", "10162"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C4BFD7"), "LAVENDAR ", "13402"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#8D8BB9"), "ELEGANCE ", "12394"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#CDD6D7"), "ICE FROST ", "15322"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C9DDDD"), "LAKE VIEW ", "12282"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#DEE4CD"), "LEMON ZEST ", "14172"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#7C8A5D"), "SPRING LEAF ", "14196"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#B14148"), "TILE RED ", "3031"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#89675D"), "OLD MAHOGANY ", "10535"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#FBC16D"), "MANGO SLICE ", "11116"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F6E19A"), "VANILLA KULFI", "12144"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F5DAC3"), "CHEEKY PINK ", "10172"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#BCBBB3"), "GREY CASTLE ", "15203"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F1F4E5"), "COTTON BALL ", "11171"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E9F1BB"), "ANARKALI ", "11163"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#2BA4B3"), "GWADAR ", "11306"));

        allProductColorsList.add(new ProductColor("Styklekoat Semi Plastic Emulsion", R.drawable.buxly_stylekoat_semi_plastic_emulsion, secondaryColors));

        // Decora Matt Enamel
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#FFF3E7"), "Off White", "9346"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EFE3D9"), "Satin Magnolia", "1240"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F3E7DE"), "Morning Light", "9404"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EBE4D4"), "Kitten White", "9339"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E7E3E8"), "Lavender white", "9493"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#DFE0D2"), "Ash White", "9279"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E4E0D2"), "Halo", "7360"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E3CDBD"), "Beige", "1318"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#FAEBD7"), "Rose White", "9355"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E9C49C"), "Peach Silk", "1152"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F5C7A9"), "Peach Delight", "1049"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#FABEAF"), "Light Terracota", "7123"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F5EADB"), "Chandni", "9295"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#FFF0E1"), "Country Rose", "9320"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#DBC4D9"), "Lilac Sky", "4048"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#CDE7F4"), "Crushed ice", "5066"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#CCDBF1"), "Serene Blue", "5051"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#FCDFEB"), "Kashmiri Tea", "3173"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E0F1EA"), "Potohar Mist", "6095"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E4EEBD"), "Lime Fizz", "6117"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#BDDFB7"), "Mint Leaf", "6055"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#97A4D3"), "Dusk", "5052"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#BA9DC5"), "Violet Charm", "4024"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#D95E80"), "Carnival Pink", "4069"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F9A56F"), "Mystic Orange", "3066"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#D53B46"), "Chilli Red", "3076"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E1D1C3"), "Pale Mushroom", "1354"));

        allProductColorsList.add(new ProductColor("Decora Matt Enamel", R.drawable.buxly_decora_matt_enamel, secondaryColors));

        // PBD Luxury Emulsion
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#93C681"), "APPLE GREEN  ", "6267"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#DBDAD0"), "ASH WHITE  ", "9279"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E8E1D7"), "BADAMI", "7345"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C8D3DB"), "MOUNTAIN MIST", "5184"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#BADBD7"), "COOL BLUE  ", "5267"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#CED3CF"), "DOVE GREY ", "7136"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F6F0E7"), "SILKY MILKY", "9413"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EFDAAF"), "IVORY", "1061"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#DFE8D4"), "MEADOW FRESH", "6391"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#E1DFDA"), "LAVENDER WHITE", "9493"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C5A986"), "PUNJABI GOLD  ", "1359"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F3EAD2"), "OFF WHITE", "9215"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EED39E"), "PALE CREAM", "1088"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#D5E8CD"), "PASTEL GREEN ", "6362"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F3E2D8"), "ROSE PETAL ", "3127"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F1CDB9"), "ROSE PINK  ", "8179"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#C7B6A1"), "SANDSTONE", "1429"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#B9BCB6"), "SHELL GREY  ", "7376"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#F0C4C0"), "SUMMER PINK  ", "8176"));

        allProductColorsList.add(new ProductColor("PBD Luxury Emulsion", R.drawable.buxly_pbd_luxury_emulsion, secondaryColors));

        // Buxlac Emulsion
        secondaryColors = new ArrayList<>();
        secondaryColors.add(new SecondaryColor(Color.parseColor("#EFF1EF"), "WHITE ", "9207"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#FFF3E7"), "OFF WHITE ", "9215"));
        secondaryColors.add(new SecondaryColor(Color.parseColor("#DBDAD0"), "ASH WHITE ", "9279"));

        allProductColorsList.add(new ProductColor("Buxlac Emulsion", R.drawable.buxlac_emulsion, secondaryColors));
    }

    private void showProductsTray() {
        hideColorsTray();
        productsTrayVisible = true;
        relativeLayoutProductsTrayContainer.setVisibility(View.VISIBLE);
        relativeLayoutProductsTrayContainer.bringToFront();
        showProductsTray(0);
    }

    private void showProductsTray(final int iteration) {
        if (iteration >= allProductColorsList.size() || !productsTrayVisible) {
            return;
        }
        activeProductColorsList.add(allProductColorsList.get(iteration).clone());
        productsRecyclerAdapter.notifyDataSetChanged();
        recyclerViewProductsTray.post(new Runnable() {
            @Override
            public void run() {
                showProductsTray(iteration + 1);
            }
        });
    }

    private void hideProductsTray() {
        productsTrayVisible = false;
        relativeLayoutProductsTrayContainer.setVisibility(View.GONE);
        activeProductColorsList.clear();
        productsRecyclerAdapter.notifyDataSetChanged();
        textViewColorsOfProduct.setText("");
        linearLayoutProductColorsTrayContainer.removeAllViews();
        relativeLayoutPictureContainer.bringToFront();
    }

    private void showSecondaryColorsTray(PrimaryColor color) {
        textViewShadesOfColor.setText(getString(R.string.shades_of_color, color.getShade()));
        textViewShadesOfColor.setTextColor(color.getColor());
        linearLayoutSecondaryColorsTrayContainer.removeAllViews();

        int containerWidth = ((View) linearLayoutSecondaryColorsTrayContainer.getParent()).getWidth();
        int colorBoxSize = getResources().getDimensionPixelSize(R.dimen.color_box_size);
        int colorBoxMargin = getResources().getDimensionPixelSize(R.dimen.margin_small);
        int boxLimit = (int)((float)(containerWidth + colorBoxMargin) / (float)(colorBoxMargin + colorBoxSize + colorBoxMargin));

        ArrayList<SecondaryColor> secondaryColors = color.getSecondaryColors();
        LinearLayout linearLayoutColorsTray = null;
        int totalColors = secondaryColors.size();
        for (int i = 0; i < totalColors; i++) {
            final SecondaryColor secondaryColor = secondaryColors.get(totalColors - 1 - i);

            if (i % boxLimit == 0) {
                linearLayoutColorsTray = new LinearLayout(getContext());
                linearLayoutColorsTray.setVisibility(View.GONE);
                linearLayoutColorsTray.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout.LayoutParams layoutParamsColorsTray = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayoutSecondaryColorsTrayContainer.addView(linearLayoutColorsTray, layoutParamsColorsTray);
            }

            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.color_box);
            imageView.setColorFilter(secondaryColor.getColor());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    secondaryColorClicked(secondaryColor);
                }
            });

            LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(colorBoxSize, colorBoxSize);
            imageViewParams.setMargins(colorBoxMargin, colorBoxMargin, colorBoxMargin, colorBoxMargin);
            linearLayoutColorsTray.addView(imageView, imageViewParams);
        }

        showSecondaryColors(0);
    }

    private void showSecondaryColors(final int iteration) {
        if (iteration >= linearLayoutSecondaryColorsTrayContainer.getChildCount()) {
            return;
        }
        linearLayoutSecondaryColorsTrayContainer.getChildAt(iteration).setVisibility(View.VISIBLE);
        linearLayoutSecondaryColorsTrayContainer.post(new Runnable() {
            @Override
            public void run() {
                showSecondaryColors(iteration + 1);
            }
        });
    }

    private void showProductColorsTray(ProductColor product) {
        textViewColorsOfProduct.setText(getString(R.string.colors_of_product, product.getName()));
        linearLayoutProductColorsTrayContainer.removeAllViews();

        int containerWidth = ((View) linearLayoutProductColorsTrayContainer.getParent().getParent()).getWidth();
        int colorBoxSize = getResources().getDimensionPixelSize(R.dimen.color_box_size);
        int colorBoxMargin = getResources().getDimensionPixelSize(R.dimen.margin_small);
        int boxLimit = (int)((float)(containerWidth + colorBoxMargin) / (float)(colorBoxMargin + colorBoxSize + colorBoxMargin));

        ArrayList<SecondaryColor> secondaryColors = product.getSecondaryColors();
        LinearLayout linearLayoutColorsTray = null;
        int totalColors = secondaryColors.size();
        for (int i = 0; i < totalColors; i++) {
            final SecondaryColor secondaryColor = secondaryColors.get(totalColors - 1 - i);

            if (i % boxLimit == 0) {
                linearLayoutColorsTray = new LinearLayout(getContext());
                linearLayoutColorsTray.setVisibility(View.GONE);
                linearLayoutColorsTray.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout.LayoutParams layoutParamsColorsTray = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayoutProductColorsTrayContainer.addView(linearLayoutColorsTray, layoutParamsColorsTray);
            }

            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.color_box);
            imageView.setColorFilter(secondaryColor.getColor());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    secondaryColorClicked(secondaryColor);
                }
            });

            LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(colorBoxSize, colorBoxSize);
            imageViewParams.setMargins(colorBoxMargin, colorBoxMargin, colorBoxMargin, colorBoxMargin);
            linearLayoutColorsTray.addView(imageView, imageViewParams);
        }

        showProductColors(0);
    }

    private void showProductColors(final int iteration) {
        if (iteration >= linearLayoutProductColorsTrayContainer.getChildCount()) {
            return;
        }
        linearLayoutProductColorsTrayContainer.getChildAt(iteration).setVisibility(View.VISIBLE);
        linearLayoutProductColorsTrayContainer.post(new Runnable() {
            @Override
            public void run() {
                showProductColors(iteration + 1);
            }
        });
    }

    private void primaryColorClicked(PrimaryColor color) {
        if (color.isTrayOpen()) {
            secondaryColorClicked(color);
            return;
        }
        for (int i = 0; i < activeColorsList.size(); i++) {
            activeColorsList.get(i).setTrayOpen(false);
        }
        color.setTrayOpen(true);
        showSecondaryColorsTray(color);
    }

    private void productColorClicked(ProductColor color) {
        if (color.isTrayOpen()) {
            return;
        }
        for (int i = 0; i < activeProductColorsList.size(); i++) {
            activeProductColorsList.get(i).setTrayOpen(false);
        }
        color.setTrayOpen(true);
        showProductColorsTray(color);
    }

    private void secondaryColorClicked(SecondaryColor color) {
        hideColorsTray();
        hideProductsTray();
        updateWallColor(color);

        onFragmentInteractionListener.onInteraction(OnFragmentInteractionListener.COLOR_SELECTED, color);
    }

    private void updateWallColor(SecondaryColor color) {
        if (selectedWall != null && color != null) {
            selectedWall.setSelectedColor(color);
            wallsRecyclerAdapter.notifyDataSetChanged();
            hideAllWallsBorders();
        }
    }

    private void cleanPreviousRoom() {
        if (wallsList != null) {
            for (Wall wall : wallsList) {
                try {
                    wall.recycle();
                } catch (Exception ex) {
                }
            }
        }
    }

    private void setRoom(Room room) {
        if (relativeLayoutPictureContainer == null) {
            return;
        }
        if (room == this.room) {
            return;
        }
        cleanPreviousRoom();
        this.room = room;

        relativeLayoutPictureContainer.removeAllViews();
        wallsList.clear();

        ArrayList<Wall> walls = room.getWallsList();
        if (walls != null && walls.size() > 0) {
            for (int i = 0; i < walls.size(); i++) {
                Wall wall = walls.get(i).clone();
                ImageView wallImage = addRoomImage(wall.getImageRes(), wall.getName());
                wallImage.setColorFilter(wall.getDefaultColor());
                ImageView borderImage = addRoomImage(wall.getBorderImageRes(), wall.getName()+" Border");
                borderImage.setColorFilter(Color.BLUE);
                borderImage.setVisibility(View.INVISIBLE);
                wall.setWallImage(wallImage, borderImage, onWallTouchListener);
                if (i == 0) {
                    selectedWall = wall;
                    selectedWall.setSelected(true);
                }
                wallsList.add(wall);
            }
        }

        addRoomImage(room.getTransparentImageRes(), room.getName());

        wallsRecyclerAdapter.notifyDataSetChanged();
        textViewShadesOfColor.setText("");
        textViewColorsOfProduct.setText("");
    }

    private ImageView addRoomImage(int resId, String tag) {
        ImageView imageViewRoom = new ImageView(getContext());
        imageViewRoom.setImageResource(resId);
        imageViewRoom.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageViewRoom.setTag(tag);

        RelativeLayout.LayoutParams layoutParamsRoom = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        layoutParamsRoom.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayoutPictureContainer.addView(imageViewRoom, layoutParamsRoom);

        return imageViewRoom;
    }

    private void openColorsOfProduct(String productName) {
        for (int i = 0; i < allProductColorsList.size(); i++) {
            if (productName.contains(allProductColorsList.get(i).getName())) {
                openColorsOfProduct(allProductColorsList.get(i));
                break;
            }
        }
    }

    private void openColorsOfProduct(final ProductColor product) {
        hideColorsTray();
        hideProductsTray();
        productsTrayVisible = true;
        relativeLayoutProductsTrayContainer.setVisibility(View.VISIBLE);
        relativeLayoutProductsTrayContainer.bringToFront();
        product.setTrayOpen(true);
        relativeLayoutColorsTrayContainer.post(new Runnable() {
            @Override
            public void run() {
                showProductColorsTray(product);
            }
        });
    }

    private void createAndSaveScreenshot() {
        PermissionManager.getInstance().checkPermissionRequest(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, 121, getString(R.string.app_name) + " requires permission to save room image in storage.", new PermissionManager.Callback() {
            @Override
            public void onGranted() {
                Bitmap roomBitmap = Utility.drawBitmapOfView(relativeLayoutPictureContainer);
                File file = Utility.saveToStorage(getContext(), roomBitmap, room.getName()+"_"+System.currentTimeMillis());
                if (file != null) {
                    Utility.showToast(getContext(), "Image of this room saved successfully in storage.");
                    Utility.broadcastGalleryUpdate(getContext(), file);
                } else {
                    Utility.showToast(getContext(), "Unable to save image. Try again.");
                }
                relativeLayoutPictureContainer.requestLayout();
            }
            @Override
            public void onDenied() {
                Utility.showToast(getContext(), "Permission denied to save image in storage.");
            }
        });
    }

    private OnWallImageTouchListener onWallTouchListener = new OnWallImageTouchListener() {
        @Override
        public void onWallTouch(Wall wall) {
            Log.i("Touch", "Touched wall: "+wall.getName());
            wallClicked(wall);
        }
    };

    public static void setSelectedRoom(Room room, boolean def) {
        if (latestInstance != null) {
            if (!def || latestInstance.room == null) {
                latestInstance.setRoom(room);
            }
        }
    }

    public static void showColorsOfProduct(String productName) {
        if (latestInstance != null) {
            latestInstance.openColorsOfProduct(productName);
        }
    }

    public static boolean backPressed() {
        if (latestInstance != null) {
            if (latestInstance.colorsTrayVisible) {
                latestInstance.hideColorsTray();
                return true;
            }
            if (latestInstance.productsTrayVisible) {
                latestInstance.hideProductsTray();
                return true;
            }
        }
        return false;
    }

}
