package com.mcdenny.farmerapp.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mcdenny.farmerapp.Interface.ItemClickListener;
import com.mcdenny.farmerapp.Model.Category;
import com.mcdenny.farmerapp.R;
import com.mcdenny.farmerapp.ViewHolder.MenuViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import mehdi.sakout.fancybuttons.FancyButton;

public class HomeActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseStorage storage;
    StorageReference storageReference;

    RecyclerView recyclerMenu;

    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    //for the alert dialog
    MaterialEditText edtName;
    FancyButton btnSelectImage, btnUploadImage;
    Category newCategory;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    private boolean allowBackButtonExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Crop Category");
       // setSupportActionBar(toolbar);

        //initialising the firebase database
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        //Loading the product details to the menu
        recyclerMenu = findViewById(R.id.recycler_menu);
        recyclerMenu.setHasFixedSize(false);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerMenu.setLayoutManager(layoutManager);

        loadMenu();
    }


    //loading the data from firebase to the menu
    private void loadMenu(){
         adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                 Category.class,
                 R.layout.layout_product_category_list,
                 MenuViewHolder.class,
                 category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, final Category model, int position) {
                viewHolder.productName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getLink()).into(viewHolder.productImage);
                final String titleName = model.getName();

                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClicked) {
                        Intent productList = new Intent(HomeActivity.this, FoodCategoryActivity.class);
                        //Getting the category key id and sending it to the product list activity
                        productList.putExtra("CategoryID", adapter.getRef(position).getKey());
                        Bundle bundle = new Bundle();
                        bundle.putString("Title_key", titleName);
                        productList.putExtras(bundle);
                        startActivity(productList);
                    }
                });
            }
        };
         adapter.notifyDataSetChanged();//Refresh data if it has been changed
        recyclerMenu.setAdapter(adapter);
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

        return super.onOptionsItemSelected(item);
    }

}
