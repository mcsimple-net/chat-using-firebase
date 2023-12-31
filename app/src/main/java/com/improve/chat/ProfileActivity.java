package com.improve.chat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView circleImageViewProfile;

    private TextInputEditText editTextUsernameProfile;
    private Button buttonUpdate;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Uri imageUri;
    Boolean imageControl = false;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String image;

    ActivityResultLauncher<Intent> activityResultLauncherForImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        circleImageViewProfile = findViewById(R.id.circleImageViewProfile);
        editTextUsernameProfile = findViewById(R.id.editTextUsernameProfile);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        registerActivityForImage();

        getUserInfo();

        circleImageViewProfile.setOnClickListener(v -> imageChooser());

        buttonUpdate.setOnClickListener(v -> updateProfile());
    }

    public void updateProfile()
    {

        String userName = editTextUsernameProfile.getText().toString();
        reference.child("Users").child(firebaseUser.getUid()).child("userName").setValue(userName);

        if (imageControl)
        {
            UUID randomID = UUID.randomUUID();
            String imageName = "images/" + randomID + ".jpg";
            storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

                StorageReference myStorageRef = firebaseStorage.getReference(imageName);
                myStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String filePath = uri.toString();
                    reference.child("Users").child(auth.getUid()).child("image")
                            .setValue(filePath).addOnSuccessListener(unused -> Toast
                                    .makeText(ProfileActivity.this
                                            , "Write to database is successful"
                                            , Toast.LENGTH_SHORT).show()).addOnFailureListener(e ->
                                    Toast.makeText(ProfileActivity.this, "Write to database is not successful"
                                            , Toast.LENGTH_LONG).show());
                });

            });
        }
        else
        {
            reference.child("Users").child(auth.getUid()).child("image").setValue(image);
        }

        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
        intent.putExtra("userName",userName);
        startActivity(intent);
        finish();


    }

    public void getUserInfo()
    {
        reference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("userName").getValue().toString();
                image = snapshot.child("image").getValue().toString();
                editTextUsernameProfile.setText(name);

                if (image.equals("null"))
                {
                    circleImageViewProfile.setImageResource(R.drawable.chat_login);
                }
                else
                {
                    Picasso.get().load(image).into(circleImageViewProfile);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public  void imageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent,1); //It's deprecated, but for now it works. I've decided not to use it.
        activityResultLauncherForImage.launch(intent);
    }

    public void registerActivityForImage()
    {
        activityResultLauncherForImage = registerForActivityResult(new ActivityResultContracts
                .StartActivityForResult(), result -> {
                   /* if (result.getResultCode() == RESULT_OK)
                    {
                         imageUri = result.getData().getData();
                         Picasso.get().load(imageUri).into(circleImageViewProfile);
                         imageControl = true;
                    } */
//this variant looks better
                     if (result.getResultCode() == RESULT_OK) {
                         Intent data = result.getData();
                         if (data != null) {
                             imageUri = data.getData();
                             Picasso.get().load(imageUri).into(circleImageViewProfile);
                             imageControl = true;
                         }
                     }
                    else
                    {
                        imageControl = false;
                    }
                });
    }

    //It's deprecated, but for now it works. I've decided not to use it.
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(circleImageViewProfile);
            imageControl = true;
        }
        else
        {
            imageControl = false;
        }
    } */
}