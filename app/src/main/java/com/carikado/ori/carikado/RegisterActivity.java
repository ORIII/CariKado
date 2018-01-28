package com.carikado.ori.carikado;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText mFirstName, mLastName, mEmailField, mPassField, mPass2Field, mAddressField;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button signUpButton;
    private ProgressDialog progressDialog;

    private String firstName, lastName, email, pass, pass2, address, sex;
    private boolean emailStatus;
    private int selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mFirstName = findViewById(R.id.firstNameField);
        mLastName = findViewById(R.id.lastNameField);
        mEmailField = findViewById(R.id.emailField);
        mPass2Field = findViewById(R.id.pass2Field);
        mPassField = findViewById(R.id.passField);
        mAddressField = findViewById(R.id.addressField);
        radioGroup= findViewById(R.id.genderGroup);
        signUpButton = findViewById(R.id.signUpButton);

        progressDialog = new ProgressDialog(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpFunc();
            }
        });
    }

    private void signUpFunc()
    {
        firstName = mFirstName.getText().toString().trim();
        lastName = mLastName.getText().toString().trim();
        email = mEmailField.getText().toString().trim();
        pass = mPassField.getText().toString().trim();
        pass2 = mPass2Field.getText().toString().trim();
        address = mAddressField.getText().toString();
        selectedGender = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(selectedGender);
        sex = radioButton.getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty() || address.isEmpty())
            Toast.makeText(this, "Please fill the empty field", Toast.LENGTH_SHORT).show();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmailField.setError("Invalid Email");
            mEmailField.requestFocus();
            return;
        }

        if (pass.length() < 6)
        {
            mPassField.setError("Please input minimum 6 character");
            mPassField.requestFocus();
            return;
        }
        else if (!pass.equals(pass2))
            Toast.makeText(this, "your password doesn't match", Toast.LENGTH_SHORT).show();
        else
        {
            progressDialog.setMessage("Creating Data");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful())
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            else
                            {
                                String currentId = mAuth.getCurrentUser().getUid();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference mRef = database.getReference("users").child(currentId);
                                final FirebaseUser user = mAuth.getCurrentUser();
                                emailStatus = user.isEmailVerified();

                                Map<String, String> newUser= new HashMap<>();

                                newUser.put("fist_name", firstName);
                                newUser.put("last_name", lastName);
                                newUser.put("gender", sex);
                                newUser.put("email", email);
                                newUser.put("address", address);
                                newUser.put("email_verified", String.valueOf(emailStatus)); // digunakan untuk pengecekan
                                                                                                // email sudah di verified atau belum
                                                                                                // kalau udah, nantinya key "email_verified" diupdate jadi true

                                mRef.setValue(newUser);

                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful())
                                            Toast.makeText(RegisterActivity.this, "Registration Complete, Verification email send to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                        else
                                        {
                                            Log.d("VER_Messages", task.getException().getMessage());
                                            Toast.makeText(RegisterActivity.this, "Failed to send email verification, please try again", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                            progressDialog.dismiss();
                        }
                    });
        }

    }
}
