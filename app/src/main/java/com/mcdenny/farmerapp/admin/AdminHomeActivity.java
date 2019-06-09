package com.mcdenny.farmerapp.admin;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mcdenny.farmerapp.Common.Common;
import com.mcdenny.farmerapp.Interface.ItemClickListener;
import com.mcdenny.farmerapp.Model.Category;
import com.mcdenny.farmerapp.R;
import com.mcdenny.farmerapp.StartActivity;
import com.mcdenny.farmerapp.ViewHolder.MenuViewHolder;
import com.mcdenny.farmerapp.user.OrderStatus;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;

public class AdminHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView currentAdminName;

    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseStorage storage;
    StorageReference storageReference;

    //for the alert dialog
    MaterialEditText edtName;
    FancyButton  btnUploadImage;
    Button btnSelectImage;
    TextView imageSelected;
    Category newCategory;
    Uri saveUri;

    DrawerLayout drawer;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    //view
    RecyclerView recycler_view;
    RecyclerView.LayoutManager layoutManager;

    AlertDialog.Builder alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_admin);
       //setSupportActionBar(toolbar);
        toolbar.setTitle("Produce Category");

        alertDialog = new AlertDialog.Builder(AdminHomeActivity.this);

        //init firebase database
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.admin_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        //Initializing the navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_admin_view);
        navigationView.setNavigationItemSelectedListener(this);

        //showing the users full name on the header
        View headerView = navigationView.getHeaderView(0);
        currentAdminName = headerView.findViewById(R.id.adminFullName);
        currentAdminName.setText(Common.admin_Current.getName());

        recycler_view = (RecyclerView) findViewById(R.id.admin_recycler_menu);
        layoutManager = new GridLayoutManager(this, 2);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);

        loadCategory();
    }

    private void showDialog() {
        alertDialog.setTitle("Add new food category");
        alertDialog.setMessage("Please fill in the information*");
        alertDialog.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_category_layout, null);

        edtName= add_menu_layout.findViewById(R.id.add_category_name);
        btnSelectImage = add_menu_layout.findViewById(R.id.select_image);
        imageSelected = add_menu_layout.findViewById(R.id.img_cat_selected);

        //adding onclick event on the buttons
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtName.getText().toString().isEmpty()) {
                    edtName.setError("You must fill in the category name!");
                    edtName.requestFocus();
                }
                else {
                    chooseImage();//let the user choose the image and save its uri
                }

            }
        });

        alertDialog.setView(add_menu_layout);

        //setting the buttons
        alertDialog.setPositiveButton("UPLOAD CATEGORY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               // dialogInterface.dismiss();
                uploadImage();

            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            saveUri = data.getData();
            //imageSelected.setTextColor(Color.GREEN);
            imageSelected.setTextColor(this.getResources().getColor(R.color.colorPrimary));
            imageSelected.setText("Image Selected");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), Common.PICK_IMAGE_REQUEST);

    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading....");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("category/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "Image uploaded succesfully",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set a value for a new category if image uploaded, and then get the download link
                                    newCategory = new Category(edtName.getText().toString(), uri.toString());
                                    category.push().setValue(newCategory);
                                    Toast.makeText(AdminHomeActivity.this, "New category " + newCategory.getName()+" has been added", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AdminHomeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading "+progress+"%");

                        }
                    });


        }
    }
    private void loadCategory() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,
                R.layout.layout_product_category_list,
                MenuViewHolder.class,
                category
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, final Category model, int position) {
                viewHolder.productName.setText(model.getName());
                Picasso.with(AdminHomeActivity.this).load(model.getLink()).into(viewHolder.productImage);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //send category id and start a new activity for produt
                        final String categoryName = model.getName();
                        Intent itemList = new Intent(AdminHomeActivity.this, AdminFoodList.class);
                        itemList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        itemList.putExtra("CategoryName", categoryName);
                        startActivity(itemList);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();//refresh data if it has changed data
        recycler_view.setAdapter(adapter);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.admin_nav_home) {
            // Handle the camera action
        }
        else if (id == R.id.admin_nav_orders) {
            intent = new Intent(AdminHomeActivity.this, AdminViewOrders.class);
            startActivity(intent);

        }
        else if (id == R.id.admin_nav_distributors) {
            intent = new Intent(AdminHomeActivity.this, AdminDistributorActivity.class);
            startActivity(intent);

        }else if (id == R.id.admin_nav_settings) {
            Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.admin_nav_logout) {
            //firebaseAuth.signOut();
            intent = new Intent(AdminHomeActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Toast.makeText(AdminHomeActivity.this, "Thanks for visiting!\nSee you soon.", Toast.LENGTH_SHORT)
                    .show();
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
