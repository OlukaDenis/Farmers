package com.mcdenny.farmerapp.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.farmerapp.Model.Order;
import com.mcdenny.farmerapp.Model.Product;
import com.mcdenny.farmerapp.R;
import com.mcdenny.farmerapp.database.Database;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetail extends AppCompatActivity {
    TextView prdName, prdDescription, prdPrice, prdDiscount;
    ImageView prdImage;
    CollapsingToolbarLayout detailCollapsingToolbar;
    FloatingActionButton cartBtn;
    ElegantNumberButton numberButton;
    Button toCart;

    String productID = "";
    Product finalProduct;

    FirebaseDatabase database;
    DatabaseReference productDetail;

    CoordinatorLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setDisplayShowHomeEnabled(true);


        //firebase init
        database = FirebaseDatabase.getInstance();
        productDetail = database.getReference("Products");

        //reference to views
        prdName = (TextView) findViewById(R.id.product_detail_name);
        prdDescription = (TextView) findViewById(R.id.product_description);
        prdPrice = (TextView) findViewById(R.id.product_detail_price);
        prdDiscount = (TextView) findViewById(R.id.product_detail_discount);
        prdImage = (ImageView) findViewById(R.id.product_detail_image);
        toCart = (Button) findViewById(R.id.btn_to_cart);
        rootLayout = (CoordinatorLayout) findViewById(R.id.detail_root_layout);

        cartBtn = (FloatingActionButton) findViewById(R.id.cart_button);
        numberButton = (ElegantNumberButton) findViewById(R.id.add_subtract_button);

        //setting the clicklistener to the cart button, so that it adds the order to the cart
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        productID,
                        finalProduct.getName(),
                        numberButton.getNumber(),
                        finalProduct.getPrice(),
                        finalProduct.getDiscount()
                ));

                Snackbar.make(rootLayout, "Successfully added to the Cart", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        toCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        productID,
                        finalProduct.getName(),
                        numberButton.getNumber(),
                        finalProduct.getPrice(),
                        finalProduct.getDiscount()
                ));

                Snackbar.make(rootLayout, "Successfully added to the Cart", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        detailCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        detailCollapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        detailCollapsingToolbar.setExpandedTitleTextAppearance(R.style.NavigatedAppBar);

        //Getting the product list id from the product list activity
        if (getIntent() != null){
            productID = getIntent().getStringExtra("ProductListID");
        }
        if(!productID.isEmpty() ){
            getProductDetail(productID);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getProductDetail(final String productID) {
        productDetail.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                finalProduct = dataSnapshot.getValue(Product.class);

                Picasso.with(getBaseContext()).load(finalProduct.getImage()).into(prdImage);

                detailCollapsingToolbar.setTitle(finalProduct.getName());//collapsing toolbar title

                prdName.setText(finalProduct.getName());
                prdDescription.setText("Description: \n"+finalProduct.getDescription());

                Locale locale = new Locale("en", "UG");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                int thePrice = (Integer.parseInt(finalProduct.getPrice()));
                prdPrice.setText(numberFormat.format(thePrice));

                prdDiscount.setText("Discount: "+finalProduct.getDiscount()+"%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
