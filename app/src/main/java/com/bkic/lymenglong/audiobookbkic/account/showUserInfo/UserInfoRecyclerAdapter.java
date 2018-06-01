package com.bkic.lymenglong.audiobookbkic.account.showUserInfo;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bkic.lymenglong.audiobookbkic.account.utils.User;
import com.bkic.lymenglong.audiobookbkic.R;

import java.util.List;


public class UserInfoRecyclerAdapter extends RecyclerView.Adapter<UserInfoRecyclerAdapter.UserViewHolder> {

    private List<User> listUsers;

    public UserInfoRecyclerAdapter(List<User> listUsers) {
        this.listUsers = listUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating recycler item view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_info_recycler, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.textViewName.setText(listUsers.get(position).getName());
        holder.textViewEmail.setText(listUsers.get(position).getEmail());
        holder.textViewUsername.setText(listUsers.get(position).getUsername());
//        holder.textViewIdNumber.setText(listUsers.get(position).getIdentitynumber());
        holder.textViewAddress.setText(listUsers.get(position).getAddress());
//        holder.textViewBirthday.setText(listUsers.get(position).getBirthday());
        holder.textViewPhoneNumber.setText(listUsers.get(position).getPhonenumber());
    }

    @Override
    public int getItemCount() {
        Log.v(UserInfoRecyclerAdapter.class.getSimpleName(),""+listUsers.size());
        return listUsers.size();
    }


    /**
     * ViewHolder class
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView textViewName;
        public AppCompatTextView textViewEmail;
//        public AppCompatTextView textViewBirthday;
        public AppCompatTextView textViewUsername;
        public AppCompatTextView textViewAddress;
//        public AppCompatTextView textViewIdNumber;
        public AppCompatTextView textViewPhoneNumber;

        UserViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.textViewName);
            textViewEmail = view.findViewById(R.id.textViewEmail);
//            textViewBirthday = view.findViewById(R.id.textViewBirthday);
            textViewAddress = view.findViewById(R.id.textViewAddress);
            textViewUsername = view.findViewById(R.id.textViewUsername);
//            textViewIdNumber = view.findViewById(R.id.textViewIdNumber);
            textViewPhoneNumber = view.findViewById(R.id.textViewPhoneNumber);
        }
    }


}
