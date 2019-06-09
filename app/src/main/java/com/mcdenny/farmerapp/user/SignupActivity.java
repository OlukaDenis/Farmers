package com.mcdenny.farmerapp.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.farmerapp.Model.User;
import com.mcdenny.farmerapp.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import mehdi.sakout.fancybuttons.FancyButton;

public class SignupActivity extends AppCompatActivity {
    MaterialEditText usrphone, usrname, usrpassword;
    TextView login;
    FancyButton signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        usrphone = (MaterialEditText) findViewById(R.id.phone);
        usrname = (MaterialEditText) findViewById(R.id.name);
        usrpassword = (MaterialEditText) findViewById(R.id.password);
        signup = (FancyButton) findViewById(R.id.signup);
        login = (TextView) findViewById(R.id.tvLogin);

        //initializing firebase database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_table = database.getReference("User");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(SignupActivity.this);
                //checking whether the edit text is empty
                if(usrphone.getText().toString().isEmpty()) {
                    usrphone.setError("Invalid Phone number");
                    usrphone.requestFocus();
                }
                else if(usrname.getText().toString().isEmpty()) {
                    usrname.setError("Invalid Name");
                    usrname.requestFocus();
                }
                else if(usrpassword.getText().toString().isEmpty()){
                    usrpassword.setError("Invalid Password");
                    usrpassword.requestFocus();
                }
                else {
                    mDialog.setMessage("Please wait.....");
                    mDialog.show();


                    user_table.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //check if the user exists
                            if (dataSnapshot.child(usrphone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignupActivity.this, "User phone already exists", Toast.LENGTH_SHORT).show();
                            }

                            //if user doesnot exist, creates new
                            else {
                                mDialog.dismiss();
                                User user = new User(usrname.getText().toString(), usrpassword.getText().toString());
                                user_table.child(usrphone.getText().toString()).setValue(user);
                                Toast.makeText(SignupActivity.this, "Succesfully registered!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }//end of else
            }
        });
    }

}
