package com.sharma.taskmanger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharma.taskmanger.R;
import com.sharma.taskmanger.model.Task;
import com.sharma.taskmanger.model.User;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        Context context;
        List<Task> taskList;
        List<User> userList;
        String type="";
        MyAdapterListener onClickListener;
    public MyAdapter(Context context, List<Task> taskList, List<User> userList, String type,MyAdapterListener myAdapterListener) {
        this.context = context;
        this.taskList = taskList;
        this.userList = userList;
        this.type = type;
        this.onClickListener=myAdapterListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_task_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(type.equals("User")){
            Task task=taskList.get(position);
            holder.txtTitle.setText(task.getTaskTitle());
            holder.txtDesc.setText(task.getTaskDesc());
        }
        else {
            User user=userList.get(position);
            holder.txtTitle.setText(user.getName());
            holder.txtDesc.setText(user.getEmail());
        }
            holder.btnEdt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        onClickListener.editOnClick(view,position);
                }
            });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.deleteOnClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return type.equals("User")?taskList.size():userList.size();
    }
    public interface MyAdapterListener {

        void editOnClick(View v, int position);

        void deleteOnClick(View v, int position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle,txtDesc;
        Button btnDelete,btnEdt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDesc=itemView.findViewById(R.id.txt_desc);
            txtTitle=itemView.findViewById(R.id.txt_title);
            btnDelete=itemView.findViewById(R.id.btn_delete);
            btnEdt=itemView.findViewById(R.id.btn_edt);
        }
    }
}
