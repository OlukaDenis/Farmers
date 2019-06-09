package com.mcdenny.farmerapp.user;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mcdenny.farmerapp.Common.Common;
import com.mcdenny.farmerapp.Model.GridObject;
import com.mcdenny.farmerapp.R;
import com.mcdenny.farmerapp.StartActivity;
import com.mcdenny.farmerapp.ViewHolder.RecyclerGrid;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int DISTRIBUTORS = 1;
    private static final int ORDERS = 4;
    private static final int PRODUCTS = 5;
    private static final int CART = 6;
    public RecyclerView rView;
    private GridLayoutManager lLayout;
    private RecyclerGrid rcAdapter;
    TextView usrFullName;
    private boolean allowBackButtonExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setTitle("Food Category");
        setSupportActionBar(toolbar);
        toolbar.setElevation(0);


        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);

        //showing the users full name on the header
        View headerView = navigationView.getHeaderView(0);
        usrFullName = headerView.findViewById(R.id.userFullName);
        usrFullName.setText(Common.user_Current.getName());

        final List<GridObject> rowListItem = getAllItemList();
        lLayout = new GridLayoutManager(this, 2);

        rView = findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        rcAdapter = new RecyclerGrid(this, rowListItem);
        rView.setAdapter(rcAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(
                this,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });
        rView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(
                    RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(
                        motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    int itemPosition = rView.getChildPosition(child);
                    final GridObject gridObject = rowListItem.get(itemPosition);
                    gridEventHandler(gridObject.getId());
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView,
                                     MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

    }

    private void gridEventHandler(int id) {
        Intent intent;
        switch (id) {
            case PRODUCTS:
                intent = new Intent(this, FoodCategoryActivity.class);
                //ProgressDialog progressDialog = new ProgressDialog(this);
                //progressDialog.setMessage("Refreshing products...");
                // progressDialog.show();
                startActivity(intent);
                break;
            case DISTRIBUTORS:
                intent = new Intent(this, DistributorList.class);
//                intent.putExtra("itemtype", 0);
                startActivity(intent);
                break;
            case ORDERS:
                startActivity(new Intent(this, OrderStatus.class));
                break;
            case CART:
                startActivity(new Intent(this, Cart.class));
                break;
        }
    }

    private List<GridObject> getAllItemList() {
        List<GridObject> allItems = new ArrayList<>();
        allItems.add(new GridObject(PRODUCTS, "Food Stuffs", "", R.drawable.food));
        allItems.add(new GridObject(DISTRIBUTORS, "Delivery", "", R.drawable.ic_distributors));
        allItems.add(new GridObject(ORDERS, "Order History", "", R.drawable.ic_order_history_));
        allItems.add(new GridObject(CART, "Cart", "", R.drawable.ic_cart_pic));
        return allItems;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.home) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            intent = new Intent(MenuActivity.this, Cart.class);
            startActivity(intent);

        } else if (id == R.id.nav_orders) {
            intent = new Intent(MenuActivity.this, OrderStatus.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_logout) {
            //firebaseAuth.signOut();
            intent = new Intent(MenuActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Toast.makeText(MenuActivity.this, "Thanks for visiting!\nSee you soon.", Toast.LENGTH_SHORT)
                    .show();
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            //super.onBackPressed();
            if (!allowBackButtonExit) {
                allowBackButtonExit = true;
                Toast.makeText(this, "Press again to close app.", Toast.LENGTH_SHORT).show();
                CountDownTimer timer = new CountDownTimer(1000, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        allowBackButtonExit = false;
                    }
                }.start();
            } else {
                System.exit(0);
            }
        }
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
