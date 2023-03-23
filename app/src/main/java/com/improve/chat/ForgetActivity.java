package com.improve.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgetActivity extends AppCompatActivity {

    private TextInputEditText editTextForget;
    private Button buttonForget;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        editTextForget = findViewById(R.id.editTextForget);
        buttonForget = findViewById(R.id.buttonForget);

        auth = FirebaseAuth.getInstance();

        buttonForget.setOnClickListener(v -> {

            String email = editTextForget.getText().toString();
            if (!email.equals(""))
            {
                passwordReset(email);
            }

        });

    }

    public void passwordReset(String email)
    {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                Toast.makeText(ForgetActivity.this, "Reset link is in your email", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ForgetActivity.this,MyLoginActivity.class);
                startActivity(i);
                finish();

            }
            else
            {
                Toast.makeText(ForgetActivity.this, "Problem...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}