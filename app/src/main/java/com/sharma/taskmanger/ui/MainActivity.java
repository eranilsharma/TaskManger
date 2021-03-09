package com.sharma.taskmanger.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sharma.taskmanger.R;
import com.sharma.taskmanger.adapter.MyAdapter;
import com.sharma.taskmanger.handler.DatabaseHelper;
import com.sharma.taskmanger.model.Task;
import com.sharma.taskmanger.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    String email = "";
    User user;
    TextView txtNoTask;
    FloatingActionButton fabAddTask;
    RecyclerView recyclerview;
    List<Task> taskList = new ArrayList<>();
    List<User> userList = new ArrayList<>();
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtNoTask = findViewById(R.id.txt_no_task);
        fabAddTask = findViewById(R.id.fab_add_task);
        recyclerview = findViewById(R.id.recyclerview);
        email = getIntent().getStringExtra("EMAIL");
        databaseHelper = new DatabaseHelper(this);
        user = databaseHelper.getUser(email);
        setTitle(user.getName());
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        if (user.getUserType().equals("User")) {
            taskList = databaseHelper.getUserTask(user.getId());
            if(taskList.size()>0) {
                adapter = new MyAdapter(this, taskList, userList, "User", new MyAdapter.MyAdapterListener() {
                    @Override
                    public void editOnClick(View v, int position) {
                        showEditUserTaskDialog(taskList.get(position));
                    }

                    @Override
                    public void deleteOnClick(View v, int position) {
                        databaseHelper.deleteUserTask(taskList.get(position));
                        taskList.remove(position);
                        Toast.makeText(MainActivity.this, "Task deleted successfully...", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                });
                recyclerview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else {
                recyclerview.setVisibility(View.GONE);
                txtNoTask.setVisibility(View.VISIBLE);
            }
        } else {
            userList = databaseHelper.getAllUser();
            if(userList.size()>0) {
                adapter = new MyAdapter(this, taskList, userList, "Admin", new MyAdapter.MyAdapterListener() {
                    @Override
                    public void editOnClick(View v, int position) {
                        showEditUserDialog(userList.get(position));
                    }

                    @Override
                    public void deleteOnClick(View v, int position) {
                        databaseHelper.deleteUser(userList.get(position));
                        userList.remove(position);
                        Toast.makeText(MainActivity.this, "User deleted successfully...", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                });
                recyclerview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else {
                txtNoTask.setText("No User found, Please add some");
                recyclerview.setVisibility(View.GONE);
                txtNoTask.setVisibility(View.VISIBLE);

            }
        }

        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getUserType().equals("User")) {
                    createTaskDialog();
                } else {
                    createNewUserDialog();
                }
            }
        });
    }

    private void showEditUserTaskDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.update_task_layout, null);
        EditText edtTitle = view.findViewById(R.id.edt_title);
        EditText edtDesc = view.findViewById(R.id.edt_desc);
        Button btnUpdate = view.findViewById(R.id.btn_update);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        edtTitle.setText(task.getTaskTitle());
        edtDesc.setText(task.getTaskDesc());
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtTitle.getText().toString().trim())) {
                    edtTitle.setError("Title is required");
                    edtTitle.requestFocus();
                } else if (TextUtils.isEmpty(edtDesc.getText().toString().trim())) {
                    edtDesc.setError("Description is required");
                    edtDesc.requestFocus();
                } else {
                    task.setTaskTitle(edtTitle.getText().toString().trim());
                    task.setTaskDesc(edtDesc.getText().toString().trim());
                    databaseHelper.updateTask(task);
                    Toast.makeText(MainActivity.this, "Task updated successfully...", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
            }
        });

    }

    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.update_user_layout, null);
        EditText edtName = view.findViewById(R.id.edt_name);
        EditText edtEmail = view.findViewById(R.id.edt_email);
        EditText edtPassword = view.findViewById(R.id.edt_password);
        Button btnUpdate = view.findViewById(R.id.btn_update);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        edtName.setText(user.getName());
        edtEmail.setText(user.getEmail());
        edtPassword.setText(user.getPassword());
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtName.getText().toString().trim())) {
                    edtName.setError("Name is required");
                    edtName.requestFocus();
                } else if (TextUtils.isEmpty(edtEmail.getText().toString().trim())) {
                    edtEmail.setError("Email is required");
                    edtEmail.requestFocus();
                } else if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
                    edtPassword.setError("Password is required");
                    edtPassword.requestFocus();
                } else {
                    if (edtEmail.getText().toString().trim().equals(user.getEmail())||!databaseHelper.checkUser(edtEmail.getText().toString().trim())) {
                        user.setName(edtName.getText().toString().trim());
                        user.setEmail(edtEmail.getText().toString().trim());
                        user.setPassword(edtPassword.getText().toString().trim());
                        databaseHelper.updateUser(user);
                        Toast.makeText(MainActivity.this, "User updated successfully...", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Email id already exits", Toast.LENGTH_SHORT).show();
                    }
                }}
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void createNewUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.create_new_user, null);
        EditText edtName = view.findViewById(R.id.edt_name);
        EditText edtEmail = view.findViewById(R.id.edt_email);
        EditText edtPassword = view.findViewById(R.id.edt_password);

        Button btnCreate = view.findViewById(R.id.btn_sign_up);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtName.getText().toString().trim())) {
                    edtName.setError("Name is require");
                    edtName.requestFocus();
                } else if (TextUtils.isEmpty(edtEmail.getText().toString().trim())) {
                    edtEmail.setError("Email is require");
                    edtEmail.requestFocus();
                } else if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
                    edtPassword.setError("Password is require");
                    edtPassword.requestFocus();
                } else {
                    if (!databaseHelper.checkUser(edtEmail.getText().toString().trim())) {
                        user.setName(edtName.getText().toString().trim());
                        user.setEmail(edtEmail.getText().toString().trim());
                        user.setPassword(edtPassword.getText().toString().trim());
                        user.setUserType("User");
                        databaseHelper.addUser(user);
                        Toast.makeText(MainActivity.this, "New User added successful", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        userList.add(user);
                        adapter.notifyDataSetChanged();

                        //startActivity(new Intent(MainActivity.this,MainActivity.this));
                    } else {
                        Toast.makeText(MainActivity.this, "Email id already exits", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void createTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_new_task, null);
        EditText edtTitle = view.findViewById(R.id.edt_title);
        EditText edtDesc = view.findViewById(R.id.edt_desc);
        Button btnCreate = view.findViewById(R.id.btn_create);
        Button btnClose=view.findViewById(R.id.btn_close);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtTitle.getText().toString().trim())) {
                    edtTitle.setError("Title is require");
                    edtTitle.requestFocus();
                } else if (TextUtils.isEmpty(edtDesc.getText().toString().trim())) {
                    edtDesc.setError("Description is require");
                    edtDesc.requestFocus();
                } else {
                    Task task = new Task();
                    task.setTaskTitle(edtTitle.getText().toString().trim());
                    task.setTaskDesc(edtDesc.getText().toString().trim());
                    task.setUserId(user.getId());
                    databaseHelper.addTask(task);
                    alertDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Task submit sucessfully", Toast.LENGTH_SHORT).show();
                    taskList.add(task);
                    if(taskList.size()>1)
                    adapter.notifyDataSetChanged();
                    else {
                        adapter = new MyAdapter(MainActivity.this, taskList, userList, "User", new MyAdapter.MyAdapterListener() {
                            @Override
                            public void editOnClick(View v, int position) {
                                showEditUserTaskDialog(taskList.get(position));
                            }

                            @Override
                            public void deleteOnClick(View v, int position) {
                                databaseHelper.deleteUserTask(taskList.get(position));
                                taskList.remove(position);
                                Toast.makeText(MainActivity.this, "Task deleted successfully...", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                            }
                        });
                        recyclerview.setAdapter(adapter);
                    }
                }
            }
        });

    }



}