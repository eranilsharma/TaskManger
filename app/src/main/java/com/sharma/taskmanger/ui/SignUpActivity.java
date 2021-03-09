package com.sharma.taskmanger.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.sharma.taskmanger.R;
import com.sharma.taskmanger.handler.DatabaseHelper;
import com.sharma.taskmanger.model.User;

public class SignUpActivity extends AppCompatActivity {
    EditText edtName,edtEmail,edtPassword;
    Button btnSignUp;
    TextView txtLogin;
    private DatabaseHelper databaseHelper;
    private User user;
    MaterialAutoCompleteTextView  userTypeAutoComplete;
    String userType[] = {"User", "Admin"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edtName=findViewById(R.id.edt_name);
        edtEmail=findViewById(R.id.edt_email);
        edtPassword=findViewById(R.id.edt_password);
        btnSignUp=findViewById(R.id.btn_sign_up);
        txtLogin=findViewById(R.id.txt_login);
        userTypeAutoComplete=findViewById(R.id.user_type_auto_complete);
        String text = "<font color=#000000>Already a Member? </font> <font color=#FF6200EE>Login</font>";
        txtLogin.setText(Html.fromHtml(text));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.select_dialog_item, userType);
        userTypeAutoComplete.setThreshold(1);
        userTypeAutoComplete.setAdapter(adapter);
        userTypeAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userTypeAutoComplete.setText(userType[i]);
            }
        });
        databaseHelper = new DatabaseHelper(this);
        user = new User();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDataToSQLite();
            }
        });
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    private void postDataToSQLite() {
        if (TextUtils.isEmpty(edtName.getText().toString().trim())) {
            edtName.setError("Name is required");
            edtName.requestFocus();
        } else if (TextUtils.isEmpty(edtEmail.getText().toString().trim())) {
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();
        } else if (TextUtils.isEmpty(edtPassword.getText().toString().trim()) || edtPassword.getText().length() < 5) {
            edtPassword.setError("Password is require");
            edtPassword.requestFocus();
        } else {
            if (!databaseHelper.checkUser(edtEmail.getText().toString().trim())) {
                user.setName(edtName.getText().toString().trim());
                user.setEmail(edtEmail.getText().toString().trim());
                user.setPassword(edtPassword.getText().toString().trim());
                user.setUserType(userTypeAutoComplete.getText().toString().trim());
                databaseHelper.addUser(user);
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this, "Email id already exits", Toast.LENGTH_SHORT).show();
            }
        }
    }
}