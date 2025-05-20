package com.foodapp.adapters.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;
    private UserListener listener;

    public interface UserListener {
        void onUserClick(User user);

        void onEditClick(User user);

        void onDeleteClick(User user);
    }

    public UserAdapter(Context context, List<User> userList, UserListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUsername.setText(user.getMaND());
        holder.tvFullName.setText(user.getHoTen());
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getSdt());
        holder.tvUserType.setText("Loại tài khoản: " + user.getLoaiTaiKhoan());

        holder.cardUser.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });

        holder.ivEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(user);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CardView cardUser;
        TextView tvUsername, tvFullName, tvEmail, tvPhone, tvUserType;
        ImageView ivEdit, ivDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardUser = itemView.findViewById(R.id.cardUser);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvUserType = itemView.findViewById(R.id.tvUserType);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}