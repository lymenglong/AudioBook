package com.bkic.lymenglong.audiobookbkic.handleLists.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Category;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.handleLists.listBook.ListBook;
import com.bkic.lymenglong.audiobookbkic.handleLists.listCategory.ListCategory;
import com.bkic.lymenglong.audiobookbkic.overrideTalkBack.PresenterOverrideTalkBack;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import java.util.ArrayList;


public class CategoryAdapter extends RecyclerView.Adapter {
    private ArrayList<Category> categories;
    private Activity activity;

    public CategoryAdapter(Activity activity, ArrayList<Category> categories) {
        this.categories = categories;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ChapterHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChapterHolder) {
            ChapterHolder chapterHolder = (ChapterHolder) holder;

            chapterHolder.name.setText(categories.get(position).getTitle());

            //fix content description for item list
            chapterHolder.layoutItem.setContentDescription(chapterHolder.name.getText());
        }

    }


    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
//        private ImageView imgNext;

        private PresenterOverrideTalkBack presenterOverrideTalkBack = new PresenterOverrideTalkBack(activity);
        private View layoutItem;

        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameItem);
//            imgNext = itemView.findViewById(R.id.imgNext);
            layoutItem = itemView.findViewById(R.id.layout_item_list);
            itemView.setOnClickListener(this);

            //Do allow talk back to read content when user touch screen
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView);
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.nameItem));
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.imgNext));

        }

        @Override
        public void onClick(View view) {
            if(view == itemView) {
                int numOfChild = categories.get(getAdapterPosition()).getNumOfChild();
                if (numOfChild != 0) {
                    Intent intent = new Intent(activity, ListCategory.class);
                    intent.putExtra("CategoryId", categories.get(getAdapterPosition()).getId());
                    intent.putExtra("CategoryTitle", categories.get(getAdapterPosition()).getTitle());
                    intent.putExtra("CategoryDescription", categories.get(getAdapterPosition()).getDescription());
                    intent.putExtra("CategoryParent", categories.get(getAdapterPosition()).getParentId());
                    intent.putExtra("NumOfChild", categories.get(getAdapterPosition()).getNumOfChild());
                    activity.startActivityForResult(intent, Const.REQUEST_CODE_BACK_HOME);
                    /*Bundle bundle = new Bundle();
                    bundle.putInt("CategoryId", categories.get(getAdapterPosition()).getId());
                    bundle.putString("CategoryTitle", categories.get(getAdapterPosition()).getTitle());
                    bundle.putString("CategoryDescription", categories.get(getAdapterPosition()).getDescription());
                    bundle.putInt("CategoryParent", categories.get(getAdapterPosition()).getParentId());
                    bundle.putInt("NumOfChild", categories.get(getAdapterPosition()).getNumOfChild());
                    Intent intent = new Intent(activity, ListCategory.class);
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent,100);*/
                } else {
                    Intent intent = new Intent(activity, ListBook.class);
                    intent.putExtra("CategoryId", categories.get(getAdapterPosition()).getId());
                    intent.putExtra("CategoryTitle", categories.get(getAdapterPosition()).getTitle());
                    intent.putExtra("CategoryDescription", categories.get(getAdapterPosition()).getDescription());
                    intent.putExtra("CategoryParent", categories.get(getAdapterPosition()).getParentId());
                    intent.putExtra("NumOfChild", categories.get(getAdapterPosition()).getNumOfChild());
                    activity.startActivityForResult(intent, Const.REQUEST_CODE_BACK_HOME);
                }

            }
        }
    }
}
