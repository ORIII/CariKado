 package com.carikado.ori.carikado;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

 public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mEmailField, mPassField;
    private Button mLoginButton;
    private TextView registerClick;
    private ProgressDialog progressDialog;

    private String email, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmailField = findViewById(R.id.emailField);
        mPassField = findViewById(R.id.passField);
        mLoginButton = findViewById(R.id.loginButton);
        registerClick = findViewById(R.id.registerClick);
        progressDialog = new ProgressDialog(this);

        mLoginButton.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.registerClick:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
            case R.id.loginButton:
                onLoginClick();
                break;
        }
    }

    public void onLoginClick()
    {
        email = mEmailField.getText().toString().trim();
        password = mPassField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty())
            Toast.makeText(this, "Please fill the empty field", Toast.LENGTH_SHORT).show();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmailField.setError("Invalid Email");
            mEmailField.requestFocus();
            return;
        }

        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                    }
                });
    }
}
