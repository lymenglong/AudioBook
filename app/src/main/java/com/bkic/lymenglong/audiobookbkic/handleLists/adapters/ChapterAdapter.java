package com.bkic.lymenglong.audiobookbkic.handleLists.adapters;

import android.annotation.SuppressLint;
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
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import java.text.SimpleDateFormat;
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
            String sTitle = chapters.get(position).getTitle();
            ChapterHolder chapterHolder = (ChapterHolder) holder;
            chapterHolder.name.setText(sTitle);

            int iLength = chapters.get(position).getLength()*1000; //convert to millisecond
            String cdLength = null;
            String sContentDescription;
            //check book length
            if(iLength!=0) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                String sLength = simpleDateFormat.format(iLength);
                chapterHolder.sLength.setVisibility(View.VISIBLE);
                chapterHolder.sLength.setText(String.valueOf(sLength));
                cdLength = chapterHolder.presenterOverrideTalkBack.DurationContentDescription(iLength);
            }else chapterHolder.sLength.setVisibility(View.GONE);

            //fix content description for item list
            if(cdLength!=null)
                sContentDescription = activity.getResources().getString(
                        R.string.item_chapter_cd_title_length, sTitle, cdLength);
            else sContentDescription = sTitle;
            chapterHolder.layoutItem.setContentDescription(sContentDescription);
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
        private TextView sLength;

        private PresenterOverrideTalkBack presenterOverrideTalkBack = new PresenterOverrideTalkBack(activity);
        private View layoutItem;

        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title_item);
//            imgNext = itemView.findViewById(R.id.imgNext);
            layoutItem = itemView.findViewById(R.id.layout_item_list);
            sLength = itemView.findViewById(R.id.item_length);
            itemView.setOnClickListener(this);

            //Do allow talk back to read content when user touch screen
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView);
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.title_item));
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
        activity.startActivityForResult(intent, Const.REQUEST_CODE_BACK_HOME);
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
