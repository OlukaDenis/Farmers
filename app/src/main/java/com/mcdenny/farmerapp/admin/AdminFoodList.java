package com.mcdenny.farmerapp.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
import com.mcdenny.farmerapp.Model.Product;
import com.mcdenny.farmerapp.R;
import com.mcdenny.farmerapp.ViewHolder.AdminFoodViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;

public class AdminFoodList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;

    //firebase
    FirebaseDatabase db;
    DatabaseReference itemList;
    FirebaseStorage storage;
    StorageReference storageReference;

    MaterialEditText edtName, editDescription, editPrice, editDiscount;
    FancyButton  btnUploadImage;
    Button btnSelectImage;
    TextView imageSelected;
    Product newProduct;
    Uri saveUri;
    AlertDialog.Builder alertDialog;
    ProgressDialog progressDialog;


    String categoryId = "";
    FirebaseRecyclerAdapter<Product, AdminFoodViewHolder> productAdapter;
    String category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_food_list);
        category_name = getIntent().getStringExtra("CategoryName");
        setTitle(category_name);

        //categoryId = getIntent().getStringExtra("CategoryId");
       alertDialog = new AlertDialog.Builder(AdminFoodList.this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");

        //firebase
        db = FirebaseDatabase.getInstance();
        itemList = db.getReference("Products");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //init
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_foodlist);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        fab = (FloatingActionButton) findViewById(R.id.fab_add_product);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddProductDialog();
            }
        });

        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if(!categoryId.isEmpty() && categoryId != null) {
            loadListProduct(categoryId);
        }
    }

    private void loadListProduct(String categoryId) {
        progressDialog.setMessage("Loading food....");
        progressDialog.show();
        productAdapter = new FirebaseRecyclerAdapter<Product, AdminFoodViewHolder>(
                Product.class,
                R.layout.layout_admin_product_list,
                AdminFoodViewHolder.class,
                itemList.orderByChild("menuid").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(AdminFoodViewHolder viewHolder, Product model, final int position) {
                viewHolder.adminProductItemName.setText(model.getName());

                Locale locale = new Locale("en", "UG");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                int thePrice = (Integer.parseInt(model.getPrice()));
                viewHolder.adminProductItemPrice.setText(numberFormat.format(thePrice));

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.adminProductItemImage);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClicked) {

                    }
                });

                final String refKey = productAdapter.getRef(position).getKey();
                viewHolder.deleteProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProduct(refKey);
                        productAdapter.notifyDataSetChanged();
                        productAdapter.notifyItemRemoved(position);
                    }
                });

                progressDialog.dismiss();
            }
        };
        productAdapter.notifyDataSetChanged();//Refresh data if it has been changed
        //Setting the adapter
        recyclerView.setAdapter(productAdapter);
    }

    private  void deleteProduct(String key){
        itemList.child(key).removeValue();
    }

    private void showAddProductDialog() {

        //final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminFoodList.this);
        alertDialog.setTitle("Add new food");
        alertDialog.setMessage("Please fill in the information");
        alertDialog.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_product_layout, null);

        edtName = add_menu_layout.findViewById(R.id.editName);
        editDescription = add_menu_layout.findViewById(R.id.editDescription);
        editDiscount = add_menu_layout.findViewById(R.id.editDiscount);
        editPrice = add_menu_layout.findViewById(R.id.editPrice);
        imageSelected = add_menu_layout.findViewById(R.id.img_selected);
        btnSelectImage = add_menu_layout.findViewById(R.id.select_product_image);


        //adding onclick event on the buttons
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validateFields();
                if (edtName.getText().toString().isEmpty()) {
                    edtName.setError("You must fill in this field!");
                    edtName.requestFocus();
                }
                else if(editDescription.getText().toString().isEmpty()) {
                    editDescription.setError("You must fill in this field!");
                    editDescription.requestFocus();
                }
                else if(editDiscount.getText().toString().isEmpty()) {
                    editDiscount.setError("You must fill in this field!");
                    editDiscount.requestFocus();
                }
                else if(editPrice.getText().toString().isEmpty()) {
                    editPrice.setError("You must fill in this field!");
                    editPrice.requestFocus();
                }
                else {
                    chooseImage();//let the user choose the image and save its uri
                }

            }
        });

        alertDialog.setView(add_menu_layout);


        //setting the buttons
        alertDialog.setPositiveButton("UPLOAD FOOD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), Common.PICK_IMAGE_REQUEST);


    }

    private void uploadImage() {
        if (saveUri != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Please wait..");
                progressDialog.setMessage("Uploading....");
                progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("foods/" + imageName);
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
                                    newProduct = new Product();
                                    newProduct.setDescription(editDescription.getText().toString());
                                    newProduct.setDiscount(editDiscount.getText().toString());
                                    newProduct.setImage(uri.toString());
                                    newProduct.setMenuid(categoryId);
                                    newProduct.setName(edtName.getText().toString());
                                    newProduct.setPrice(editPrice.getText().toString());
                                    //adding new food to the database
                                    itemList.push().setValue(newProduct);
                                    Toast.makeText(AdminFoodList.this, "New Product " + newProduct.getName() + " has been added", Toast.LENGTH_SHORT).show();
                                    /*alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            dialog.dismiss();
                                        }
                                    });*/

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AdminFoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading " + progress + "%");

                        }
                    });
        }
        else {
            Toast.makeText(this, "Food not added! No image was selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            imageSelected.setText("Image Selected");
            imageSelected.setTextColor(this.getResources().getColor(R.color.colorPrimary));
        }
    }
}
