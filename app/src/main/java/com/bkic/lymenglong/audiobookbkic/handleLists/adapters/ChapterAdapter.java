package com.bkic.lymenglong.audiobookbkic.handleLists.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.overrideTalkBack.PresenterOverrideTalkBack;
import com.bkic.lymenglong.audiobookbkic.player.PlayControl;

import java.util.ArrayList;


public class ChapterAdapter extends RecyclerView.Adapter {
    private ArrayList<Chapter> chapters;
    private Activity activity;
    private int ChapterId, ChapterLength, BookId;
    private String ChapterTitle, ChapterUrl;

    public ChapterAdapter(Activity activity, ArrayList<Chapter> chapters) {
        this.chapters = chapters;
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

            chapterHolder.name.setText(chapters.get(position).getTitle());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
//        private ImageView imgNext;

        private PresenterOverrideTalkBack presenterOverrideTalkBack = new PresenterOverrideTalkBack(activity);

        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameItem);
//            imgNext = itemView.findViewById(R.id.imgNext);

            itemView.setOnClickListener(this);

            //Do allow talk back to read content when user touch screen
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView);
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.nameItem));
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.imgNext));
        }

        @Override
        public void onClick(View view) {
            if(view == itemView) {
                ChapterId = chapters.get(getAdapterPosition()).getId();
                ChapterTitle = chapters.get(getAdapterPosition()).getTitle();
                ChapterLength = chapters.get(getAdapterPosition()).getLength();
                ChapterUrl = chapters.get(getAdapterPosition()).getFileUrl();
                BookId = chapters.get(getAdapterPosition()).getBookId();
                IntentToPlayerControl();
//                showAlertDialog();
            }
        }
    }

    private void IntentToPlayerControl() {
        Intent intent = new Intent(activity, PlayControl.class);
        intent.putExtra("ChapterId", ChapterId);
        intent.putExtra("ChapterTitle", ChapterTitle);
        intent.putExtra("ChapterUrl", ChapterUrl);
        intent.putExtra("ChapterLength", ChapterLength);
        intent.putExtra("BookId", BookId);
        activity.startActivity(intent);
    }


    //region ShowDialog
    /*private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Chọn Dạng Sách");
        builder.setMessage("Bạn muốn chọn dạng nào?");
        builder.setCancelable(false);
        builder.setPositiveButton("Văn bản", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(activity, "Dạng văn bản", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, ViewReading.class);
                intent.putExtra("idChapter", ChapterId);
                intent.putExtra("titleChapter", ChapterTitle);
                intent.putExtra("content", ChapterLength);
                intent.putExtra("fileUrl", ChapterUrl);
                dialogInterface.dismiss();
                activity.startActivity(intent);

            }
        });
        builder.setNegativeButton("Ghi âm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(activity, "Dạng ghi âm", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, PlayControl.class);
                intent.putExtra("ChapterId", ChapterId);
                intent.putExtra("ChapterTitle", ChapterTitle);
                intent.putExtra("ChapterUrl", ChapterLength);
                intent.putExtra("ChapterLength", ChapterUrl);
                intent.putExtra("BookId", BookId);
                dialogInterface.dismiss();
                activity.startActivity(intent);
            }
        });
        builder.setNeutralButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }*/
    //endregion

}
