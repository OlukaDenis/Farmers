package com.mcdenny.farmerapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcdenny.farmerapp.Interface.ItemClickListener;
import com.mcdenny.farmerapp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productItemName, productItemPrice;
    public ImageView productItemImage;

    //admin
    public TextView adminProductItemName, adminProductItemPrice;
    public ImageView adminProductItemImage;
    public Button deleteProduct;
    private ItemClickListener itemClickListener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        productItemName = (TextView) itemView.findViewById(R.id.item_name);
        productItemPrice = (TextView) itemView.findViewById(R.id.item_price);
        productItemImage = (ImageView) itemView.findViewById(R.id.item_image);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);

    }
}
