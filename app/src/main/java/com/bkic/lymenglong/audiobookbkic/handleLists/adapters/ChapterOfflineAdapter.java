package com.bkic.lymenglong.audiobookbkic.handleLists.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.overrideTalkBack.PresenterOverrideTalkBack;
import com.bkic.lymenglong.audiobookbkic.player.PlayControl;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import java.util.ArrayList;


public class ChapterOfflineAdapter extends RecyclerView.Adapter {
    private ArrayList<Chapter> chapters;
    private Activity activity;
    private int ChapterId, ChapterLength, BookId;
    private String ChapterTitle, ChapterUrl;

    public ChapterOfflineAdapter(Activity activity, ArrayList<Chapter> chapters) {
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
                String sLength = chapterHolder.presenterOverrideTalkBack.getConvertedDuration(iLength);
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

    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView name;
//        private ImageView imgNext;
        private PresenterOverrideTalkBack presenterOverrideTalkBack = new PresenterOverrideTalkBack(activity);
        private View layoutItem;
        private TextView sLength;

        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title_item);
//            imgNext = itemView.findViewById(R.id.imgNext);
            layoutItem = itemView.findViewById(R.id.layout_item_list);
            sLength = itemView.findViewById(R.id.item_length);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            //Do allow talk back to read content when user touch screen
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView);
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.title_item));
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.imgNext));

        }

        @Override
        public boolean onLongClick(View v) {
            ChapterId = chapters.get(getAdapterPosition()).getId();
            ChapterTitle = chapters.get(getAdapterPosition()).getTitle();
            ChapterLength = chapters.get(getAdapterPosition()).getLength();
            ChapterUrl = chapters.get(getAdapterPosition()).getFileUrl();
            BookId = chapters.get(getAdapterPosition()).getBookId();
            adapterPosition = getAdapterPosition();
            showAlertDialog();
            return true;
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

    private int adapterPosition;
    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Chọn Dạng Sách");
        builder.setMessage("Bạn muốn xóa khỏi danh sách không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                chapters.remove(adapterPosition);
                notifyDataSetChanged();
                RemoveChapterData(BookId, ChapterId);
                Toast.makeText(activity, ChapterTitle + " Đã Xóa", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(activity, "Đã Kích Không", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void RemoveChapterData(int bookId, int chapterId) {
        UpdateChapterStatus(bookId,chapterId);
        DeleteDownloadStatusData(bookId,chapterId);
    }

    private void DeleteDownloadStatusData(int bookId, int chapterId) {
        DBHelper dbHelper = new DBHelper(activity, Const.DB_NAME, null, Const.DB_VERSION);
        dbHelper.QueryData(
                "DELETE FROM downloadStatus WHERE BookId = '"+bookId+"' AND ChapterId = '"+chapterId+"';"
        );
        dbHelper.close();
    }

    private void UpdateChapterStatus(int bookId, int chapterId){
        DBHelper dbHelper = new DBHelper(activity, Const.DB_NAME, null, Const.DB_VERSION);
        dbHelper.QueryData(
                "UPDATE chapter " +
                        "SET ChapterStatus = '0' " +
                        "WHERE ChapterId = '" + chapterId + "' AND BookId = '"+bookId+"'" +
                        ";"
        );
        dbHelper.close();
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

}
