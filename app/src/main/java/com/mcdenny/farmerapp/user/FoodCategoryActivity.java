package com.mcdenny.farmerapp.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mcdenny.farmerapp.Interface.ItemClickListener;
import com.mcdenny.farmerapp.Model.Category;
import com.mcdenny.farmerapp.R;
import com.mcdenny.farmerapp.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

public class FoodCategoryActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    //view
    RecyclerView recycler_view;
    RecyclerView.LayoutManager layoutManager;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_category);

        setTitle("Food Category");

        //init firebase database
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading food category..");

        recycler_view = (RecyclerView) findViewById(R.id.user_recycler_category);
        layoutManager = new GridLayoutManager(this, 2);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);

        loadCategory();
    }

    private void loadCategory() {
        dialog.show();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,
                R.layout.layout_product_category_list,
                MenuViewHolder.class,
                category
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, final Category model, int position) {
                viewHolder.productName.setText(model.getName());
                Picasso.with(FoodCategoryActivity.this).load(model.getLink()).into(viewHolder.productImage);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //send category id and start a new activity for product
                        final String categoryName = model.getName();
                        final String categoryKey = adapter.getRef(position).getKey();
                        Intent itemList = new Intent(FoodCategoryActivity.this, ProductList.class);
                        itemList.putExtra("CategoryId", categoryKey);
                        itemList.putExtra("CategoryName", categoryName);
                        //Toast.makeText(FoodCategoryActivity.this, categoryKey, Toast.LENGTH_SHORT).show();
                        startActivity(itemList);
                    }
                });
                dialog.dismiss();
            }
        };

        adapter.notifyDataSetChanged();//refresh data if it has changed data
        recycler_view.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Connect with me on facebook via: www.facebook.com/denislucaz");
            startActivity(Intent.createChooser(shareIntent, "Send Invite Via"));
            return true;
        }
        if (id == R.id.action_cart) {
            startActivity(new Intent(FoodCategoryActivity.this, Cart.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
