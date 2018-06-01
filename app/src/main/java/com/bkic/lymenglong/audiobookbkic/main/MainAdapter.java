package com.bkic.lymenglong.audiobookbkic.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.account.showUserInfo.UserInfoActivity;
import com.bkic.lymenglong.audiobookbkic.handleLists.favorite.ListFavorite;
import com.bkic.lymenglong.audiobookbkic.handleLists.history.ListHistory;
import com.bkic.lymenglong.audiobookbkic.handleLists.listCategory.ListCategory;
import com.bkic.lymenglong.audiobookbkic.handleLists.listOffline.ListOfflineBook;
import com.bkic.lymenglong.audiobookbkic.search.ListBookSearch;
import com.bkic.lymenglong.audiobookbkic.help.HelpActivity;

import java.util.ArrayList;


public class MainAdapter extends RecyclerView.Adapter {
    private ArrayList<Menu> menus;
    private Activity activity;

    public MainAdapter(Activity activity, ArrayList<Menu> menus) {
        this.menus = menus;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new HomeHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HomeHolder) {
            HomeHolder homeHolder = (HomeHolder) holder;

            homeHolder.name.setText(menus.get(position).getTitle());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
//        private ImageView imgNext;

        HomeHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameStory);
//            imgNext = itemView.findViewById(R.id.imgNext);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView){
                String title = menus.get(getAdapterPosition()).getTitle();
                if (title.equals(activity.getString(R.string.prompt_book_type))) {
                    Intent intent = new Intent(activity, ListCategory.class);
                    intent.putExtra("MenuId", menus.get(getAdapterPosition()).getId());
                    intent.putExtra("MenuTitle", menus.get(getAdapterPosition()).getTitle());
                    activity.startActivity(intent);
                }
                if (title.equals(activity.getString(R.string.prompt_history))){
                    Intent intent = new Intent(activity, ListHistory.class);
                    intent.putExtra("MenuId", menus.get(getAdapterPosition()).getId());
                    intent.putExtra("MenuTitle", menus.get(getAdapterPosition()).getTitle());
                    activity.startActivity(intent);
                }
                if (title.equals(activity.getString(R.string.prompt_favorite))){
                    Intent intent = new Intent(activity, ListFavorite.class);
                    intent.putExtra("MenuId", menus.get(getAdapterPosition()).getId());
                    intent.putExtra("MenuTitle", menus.get(getAdapterPosition()).getTitle());
                    activity.startActivity(intent);
                }
                if (title.equals(activity.getString(R.string.prompt_help))){
                    Intent intent = new Intent(activity, HelpActivity.class);
                    intent.putExtra("MenuId", menus.get(getAdapterPosition()).getId());
                    intent.putExtra("MenuTitle", menus.get(getAdapterPosition()).getTitle());
                    activity.startActivity(intent);
                }
                if (title.equals(activity.getString(R.string.prompt_account))){
                    Intent intent = new Intent(activity, UserInfoActivity.class);
                    intent.putExtra("MenuId", menus.get(getAdapterPosition()).getId());
                    intent.putExtra("MenuTitle", menus.get(getAdapterPosition()).getTitle());
                    activity.startActivity(intent);
                }
                if (title.equals(activity.getString(R.string.prompt_offline_book))){
                    Intent intent = new Intent(activity, ListOfflineBook.class);
                    intent.putExtra("MenuId", menus.get(getAdapterPosition()).getId());
                    intent.putExtra("MenuTitle", menus.get(getAdapterPosition()).getTitle());
                    activity.startActivity(intent);
                }
                if (title.equals(activity.getString(R.string.prompt_search))){
                    Intent intent = new Intent(activity, ListBookSearch.class);
                    intent.putExtra("MenuId", menus.get(getAdapterPosition()).getId());
                    intent.putExtra("MenuTitle", menus.get(getAdapterPosition()).getTitle());
                    activity.startActivity(intent);
                }
                if (title.equals("Thoát")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            //        builder.setTitle("Exit");
                    builder.setMessage("Bạn có muốn thoát ứng dụng không?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.finishAffinity(activity);
                            System.exit(0);
                        }
                    });
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }

        }
    }
}
