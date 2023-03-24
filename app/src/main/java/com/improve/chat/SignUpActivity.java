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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    public CircleImageView circleImageView;
    private TextInputEditText editTextEmailSignup, editTextPasswordSignup, editTextUsernameSignup;
    private Button buttonRegister;
    Boolean imageControl = false;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Uri imageUri;

    ActivityResultLauncher<Intent> activityResultLauncherForImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        circleImageView = findViewById(R.id.circleImageView);
        editTextEmailSignup = findViewById(R.id.editTextEmailSignUp);
        editTextPasswordSignup = findViewById(R.id.editTextPasswordSignUp);
        editTextUsernameSignup = findViewById(R.id.editTextUsernameSignup);
        buttonRegister = findViewById(R.id.buttonRegister);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        registerActivityForImage();

        buttonRegister.setOnClickListener(v -> {

            String email = editTextEmailSignup.getText().toString();
            String password = editTextPasswordSignup.getText().toString();
            String userName = editTextUsernameSignup.getText().toString();

            if (!email.equals("") && !password.equals("") && !userName.equals(""))
            {
                signup(email,password,userName);
            }
            else
            {
                Toast.makeText(SignUpActivity.this, "Write something in the fields", Toast.LENGTH_SHORT).show();
            }


        });

        circleImageView.setOnClickListener(v -> imageChooser());
    }

    public  void imageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent,1);
        activityResultLauncherForImage.launch(intent);
    }

    //It's deprecated, but for now it works. I've decided not to use it.
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(circleImageView);
            imageControl = true;
        }
        else
        {
            imageControl = false;
        }
    }
*/

    public void registerActivityForImage()
    {
        activityResultLauncherForImage = registerForActivityResult(new ActivityResultContracts
                .StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK)
            {
                Intent data = result.getData();
                if (data != null)
                {
                    imageUri = data.getData();
                    Picasso.get().load(imageUri).into(circleImageView);
                    imageControl = true;
                }
            }
            else if (result.getResultCode() == RESULT_CANCELED)
            {
                imageControl = false;
            }
        });
    };
    public void signup(String email, String password, String userName)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {

            if (task.isSuccessful())
            {
                reference.child("Users").child(auth.getUid()).child("userName").setValue(userName);

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
                                            .makeText(SignUpActivity.this, "Write to database is successful"
                                                    , Toast.LENGTH_SHORT).show()).addOnFailureListener(e ->
                                            Toast.makeText(SignUpActivity.this, "Write to database is not successful"
                                                    , Toast.LENGTH_LONG).show());
                        });

                    });
                }
                else
                {
                    reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                }

                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);

                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(SignUpActivity.this, "There is a problem", Toast.LENGTH_SHORT).show();
            }
        });


    }
}