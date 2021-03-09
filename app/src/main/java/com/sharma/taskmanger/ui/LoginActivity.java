package com.sharma.taskmanger.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sharma.taskmanger.R;
import com.sharma.taskmanger.handler.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    Button btnLogin;
    TextView txtCreate;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        txtCreate = findViewById(R.id.txt_create);
        String text = "<font color=#000000>Not a Member yet?</font> <font color=#FF6200EE>Sign Up</font>";
        txtCreate.setText(Html.fromHtml(text));
        databaseHelper = new DatabaseHelper(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyFromSQLite();
            }
        });
        txtCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));

            }
        });
    }

    private void verifyFromSQLite() {
        if (TextUtils.isEmpty(edtUsername.getText().toString().trim())) {
            edtUsername.setError(getString(R.string.error_message_email));
            edtUsername.requestFocus();
        } else if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
            edtPassword.setError(getString(R.string.error_message_password));
            edtPassword.requestFocus();
        } else {
            if (databaseHelper.checkUser(edtUsername.getText().toString().trim(), edtPassword.getText().toString().trim())) {
                finish();
                Intent accountsIntent = new Intent(this, MainActivity.class);
                accountsIntent.putExtra("EMAIL", edtUsername.getText().toString().trim());
                startActivity(accountsIntent);
            } else {
                Toast.makeText(this, getString(R.string.error_valid_email_password), Toast.LENGTH_SHORT).show();

            }
        }
    }
}