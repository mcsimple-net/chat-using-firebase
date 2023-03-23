package com.improve.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyLoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonSignin, buttonSignup;
    private TextView textViewForgot;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null)
        {
            Intent intent = new Intent(MyLoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignin = findViewById(R.id.buttonSignIn);
        buttonSignup = findViewById(R.id.buttonSignUp);
        textViewForgot = findViewById(R.id.textViewForgot);

        auth = FirebaseAuth.getInstance();


        buttonSignin.setOnClickListener(v -> {

            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (!email.equals("") && !password.equals(""))
            {
                signin(email,password);
            }
            else
            {
                Toast.makeText(this, "Write your credentials"
                        , Toast.LENGTH_SHORT).show();
            }

        });

        buttonSignup.setOnClickListener(v -> {

            Intent intent = new Intent(MyLoginActivity.this,SignUpActivity.class);
            startActivity(intent);
            finish();

        });

        textViewForgot.setOnClickListener(v -> {

            Intent i = new Intent(MyLoginActivity.this,ForgetActivity.class);
            startActivity(i);
            finish();

        });

    }

    public void signin(String email, String password)
    {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                Intent intent = new Intent(MyLoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
            else
            {
                Toast.makeText(MyLoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}