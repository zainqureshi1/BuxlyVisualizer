package com.e2esp.buxlypaints.visualizer.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e2esp.buxlypaints.visualizer.R;
import com.e2esp.buxlypaints.visualizer.adapters.ProductRecyclerAdapter;
import com.e2esp.buxlypaints.visualizer.adapters.VerticalSpacingItemDecoration;
import com.e2esp.buxlypaints.visualizer.interfaces.OnFragmentInteractionListener;
import com.e2esp.buxlypaints.visualizer.interfaces.OnProductClickListener;
import com.e2esp.buxlypaints.visualizer.models.Product;
import com.e2esp.buxlypaints.visualizer.models.SecondaryColor;
import com.e2esp.buxlypaints.visualizer.utils.Utility;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {

    private static ProductsFragment latestInstance;

    private ArrayList<Product> arrayListProducts;
    private ProductRecyclerAdapter productRecyclerAdapter;

    private SecondaryColor selectedColor;

    private OnFragmentInteractionListener onFragmentInteractionListener;

    public ProductsFragment() {
        // Required empty public constructor
    }

    public static ProductsFragment newInstance() {
        latestInstance = new ProductsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        setupView(view);
        loadProducts();

        return view;
    }

    private void setupView(View view) {
        RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        arrayListProducts = new ArrayList<>();
        int color = selectedColor != null ? selectedColor.getColor() : getResources().getColor(R.color.white);
        productRecyclerAdapter = new ProductRecyclerAdapter(getContext(), arrayListProducts, color, new OnProductClickListener() {
            @Override
            public void onLearnMoreClick(Product product) {
                learnMoreClicked(product);
            }
            @Override
            public void onViewColorsClick(Product product) {
                viewColorsClicked(product);
            }
        });
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewProducts.addItemDecoration(new VerticalSpacingItemDecoration(Utility.dpToPx(getContext(), 10)));
        //recyclerViewProducts.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //recyclerViewProducts.addItemDecoration(new GridSpacingItemDecoration(2, Utility.dpToPx(getContext(), 10), true));
        recyclerViewProducts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewProducts.setAdapter(productRecyclerAdapter);
    }

    private void loadProducts() {
        arrayListProducts.clear();

        arrayListProducts.add(new Product("Buxlac Emulsion", "Buxlac Emulsion is top quality emulsion. It covers more wall area than other distempers and is therefore, very economical.", "http://www.buxly.com/bp_products/buxlac-emulsion/", R.drawable.buxlac_emulsion, true));
        arrayListProducts.add(new Product("Imperial Matt Emulsion", "Buxly Imperial Matt Emulsion is top quality Acrylic-Based emulsion paint, ideal for Interior Surfaces. It dries out to a smooth finish which is durable and easily washable.", "http://www.buxly.com/bp_products/imperial-matt-emulsion/", R.drawable.buxly_imperial_matt_emulsion, true));
        arrayListProducts.add(new Product("Styklekoat Semi Plastic Emulsion", "Buxly Stylekoat Semi Plasitc Emulsionis a good Quality washable vinyl emulsion paint which can be wiped clean by damp cloth.", "", R.drawable.buxly_stylekoat_semi_plastic_emulsion, true));
        arrayListProducts.add(new Product("PBD Luxury Emulsion", "PBD is a top quality smooth emulsion. It can be used on walls, ceilings, old and new cement, concrete, plasters, chipboard and hardboard.", "", R.drawable.buxly_pbd_luxury_emulsion, true));
        arrayListProducts.add(new Product("Decora Matt Enamel", "Buxly Decora Matt Enamel is a high quality matt finish enamel for walls, ceilings, old and new cement plasters, woodwork, metals, chipboards and hardboards. It is tough and long-lasting and is specially recommended for kitchens, bathrooms, corridors and staircase walls, etc.", "", R.drawable.buxly_decora_matt_enamel, true));
        arrayListProducts.add(new Product("Weather Fighter", "Weather fighter is a smooth water based masonry exterior paint. It contains tough felxible resin pigmented with titanium diaoxide and light fast pigments. Its smooth finish has the highest degree of durablity and is resistant to all types of weather conditions.", "http://www.buxly.com/bp_products/weather-fighter-exterior-emulsion/", R.drawable.buxly_weather_fighter, true));
        arrayListProducts.add(new Product("413' Synthetic Enamel", "Buxly Synthetic Enamel is first-rate premium oil-based enamel for interior and exterior use. It provides protection and decoration to the surface; it could be used for finishing and attractiveness of the surface under all climate changes.", "", R.drawable.buxly_413_synthetic_enamel, false));
        arrayListProducts.add(new Product("413' Synthetic Enamel Special Edition", "Buxly Synthetic Enamel Special Edition is first-rate premium oil-based enamel for interior and exterior use. It provides protection and decoration to the surface; it could be used for finishing and attractiveness of the surface under all climate changes.", "", R.drawable.buxly_413_synthetic_enamel, false));
        arrayListProducts.add(new Product("Buxlac Emulsion Special Edition", "Buxlac Emulsion Special Edition is top quality emulsion. It covers more wall area than other distempers and is therefore, very economical.", "", R.drawable.buxlac_emulsion_special_edition, false));
        arrayListProducts.add(new Product("Aqua Seal Water Based Wall Primer", "Aqua Seal Water Based Wall Primer is formulated to resist alkali and moisture present in the new plaster surfaces. It forms a tough and flexible firm which prevents passage of moisture from the substrate through the paint film.", "", R.drawable.buxly_aquaseal, false));
        arrayListProducts.add(new Product("Sealon Alkali Resisting Primer Sealer", "Sealon Alkali Resisting Sealer is an oil based wall primer sealer formulated to hold back alkali and moisture present in the new plaster. Sealon Alkali primer sealer has been designed for use on cement interpretation, concrete, lime plaster, asbestos cement sheets and all other surfaces.", "", R.drawable.buxly_sealon, false));
        arrayListProducts.add(new Product("Red Oxide Primer (PQA)", "Red Oxide Primer is a high quality anti-corrosive varnish based on Red Oxide combined with anti-corrosive color and synthetic resin medium. Its utility is to protect metal products from rusting and it maximizes the products reliability.", "", R.drawable.buxly_red_oxide_primer, false));
        arrayListProducts.add(new Product("Synthetic Clear Varnish", "Synthetic Clear Varnish is a finest Alkyd varnish with maximum spread over the interior surfaces. It is recommended to be durable and long lasting Alkyd varnish and gives the interior surfaces shine and glossy finishing.", "", R.drawable.buxly_synthetic_clear_varnish, false));
        arrayListProducts.add(new Product("Wood Shield Clear Lacquer", "Buxly Wood Shield Clear Lacquer is specially designed for furniture industry.", "", R.drawable.buxly_woodshield_clear_lacquer, false));
        arrayListProducts.add(new Product("Wood Shield sanding sealer", "Buxly Wood Shield Sanding Sealer is a semi-gloss transparent type sealer used for sealing the small pores on surfaces.", "", R.drawable.buxly_woodshield_sanding_sealer, false));
        arrayListProducts.add(new Product("Wood Shield Thinner", "Buxly Wood Shield Thinner is recommended to use with Buxly Range of Products and is blended with highly inflammable solvent.", "", R.drawable.buxly_woodshield_thinner, false));
        arrayListProducts.add(new Product("Buxly Wall Putty", "Buxly Wall Putty is highly recommended wall putty designed for the cement surfaces for interior use. It provides a reliable surface for the paint colors to stay and for the fine performance of the distempers.", "", R.drawable.buxly_wall_putty, false));

        productRecyclerAdapter.notifyDataSetChanged();
    }

    private void learnMoreClicked(Product product) {
        openWebPage(product.getLink());
    }

    private void viewColorsClicked(Product product) {
        onFragmentInteractionListener.onInteraction(OnFragmentInteractionListener.PRODUCT_VIEW_COLORS_CLICK, product.getName());
    }

    private void openWebPage (String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    public void setColor(SecondaryColor color) {
        selectedColor = color;
        if (productRecyclerAdapter != null) {
            productRecyclerAdapter.setColor(selectedColor.getColor());
            productRecyclerAdapter.notifyDataSetChanged();
        }
    }

    public static void setSelectedColor(SecondaryColor color) {
        if (latestInstance != null && color != null) {
            latestInstance.setColor(color);
        }
    }

}
