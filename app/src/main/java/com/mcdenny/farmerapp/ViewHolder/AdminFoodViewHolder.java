package com.mcdenny.farmerapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcdenny.farmerapp.Interface.ItemClickListener;
import com.mcdenny.farmerapp.R;

public class AdminFoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //admin
    public TextView adminProductItemName, adminProductItemPrice;
    public ImageView adminProductItemImage;
    public Button deleteProduct;
    private ItemClickListener itemClickListener;

   public AdminFoodViewHolder(View itemView){
       super(itemView);

       //admin
       adminProductItemName = (TextView) itemView.findViewById(R.id.ad_item_name);
       adminProductItemPrice = (TextView) itemView.findViewById(R.id.admin_item_price);
       adminProductItemImage = (ImageView) itemView.findViewById(R.id.admin_item_image);
       deleteProduct = (Button) itemView.findViewById(R.id.admin_delete_product);
       itemView.setOnClickListener(this);
   }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(itemView, getAdapterPosition(), false);
    }
}
