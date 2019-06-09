package com.mcdenny.farmerapp.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.farmerapp.Common.Common;
import com.mcdenny.farmerapp.Model.User;
import com.mcdenny.farmerapp.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    TextView signup;
    FancyButton login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        //getting the reference
        username = (MaterialEditText) findViewById(R.id.phone);
        password = (MaterialEditText) findViewById(R.id.password);
        login = (FancyButton) findViewById(R.id.login);
        signup = (TextView) findViewById(R.id.tvSignup);

        //initializing firebase database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
       final DatabaseReference user_table = database.getReference("User");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
               //checking whether the edit text is empty
               if(username.getText().toString().isEmpty()) {
                   username.setError("You must fill in the phone number!");
                   username.requestFocus();
               }
               else if (password.getText().toString().isEmpty()){
                   password.setError("You must fill in the password!");
                   password.requestFocus();
               }
               //If the textfields are not empty
               else {
                   //setting a dialog to tell the user to wait
                   mDialog.setMessage("Please wait.....");
                   mDialog.show();

                   user_table.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           //checking if the user exists in the database
                           if (dataSnapshot.child(username.getText().toString()).exists()) {
                               //stopping the progress dialog
                               mDialog.dismiss();

                               //getting the users information
                               User user = dataSnapshot.child(username.getText().toString()).getValue(User.class);
                               user.setPhone(username.getText().toString());
                               if (user.getPassword().equals(password.getText().toString())) {
                                   Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                   Common.user_Current = user;//the user details are stored in user_current variable
                                   startActivity(intent);
                                   finish();//stops the login activity
                               } else {
                                   Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                   mDialog.dismiss();
                               }
                           }
                           //if user doesn't exist
                           else {
                               Toast.makeText(LoginActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                               mDialog.dismiss();
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
               }//end of else if
           }
       });
    }

}
