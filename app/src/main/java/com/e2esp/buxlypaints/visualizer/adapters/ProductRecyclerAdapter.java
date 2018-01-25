package com.e2esp.buxlypaints.visualizer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e2esp.buxlypaints.visualizer.R;
import com.e2esp.buxlypaints.visualizer.interfaces.OnProductClickListener;
import com.e2esp.buxlypaints.visualizer.models.Product;

import java.util.ArrayList;

/**
 *
 * Created by Zain on 2/1/2017.
 */

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ProductViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Product> productsList;
    private OnProductClickListener onProductClickListener;

    private int selectedColor;

    public ProductRecyclerAdapter(Context context, ArrayList<Product> productsList, int selectedColor, OnProductClickListener onProductClickListener) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.productsList = productsList;
        this.selectedColor = selectedColor;
        this.onProductClickListener = onProductClickListener;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_product_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.bindView(productsList.get(position));
    }

    public void setColor(int color) {
        this.selectedColor = color;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewLearnMore;
        private TextView textViewViewColors;

        ProductViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewProduct);
            textViewTitle = itemView.findViewById(R.id.textViewProductTitle);
            textViewDescription = itemView.findViewById(R.id.textViewProductDescription);
            textViewLearnMore = itemView.findViewById(R.id.textViewProductLearnMore);
            textViewViewColors = itemView.findViewById(R.id.textViewProductViewColors);
        }

        void bindView(final Product product) {
            imageView.setImageResource(product.getImageRes());
            textViewTitle.setText(product.getName());
            textViewDescription.setText(product.getDescription());

            if (!product.getLink().isEmpty()) {
                textViewLearnMore.setVisibility(View.VISIBLE);
                textViewLearnMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onProductClickListener.onLearnMoreClick(product);
                    }
                });
            } else {
                textViewLearnMore.setVisibility(View.INVISIBLE);
            }
            if (product.hasColors()) {
                textViewViewColors.setVisibility(View.VISIBLE);
                textViewViewColors.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onProductClickListener.onViewColorsClick(product);
                    }
                });
            } else {
                textViewViewColors.setVisibility(View.INVISIBLE);
            }
        }

    }

}
