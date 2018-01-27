 package com.carikado.ori.carikado;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

 public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView registerClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerClick = findViewById(R.id.registerClick);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.registerClick:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
        }
    }
}
