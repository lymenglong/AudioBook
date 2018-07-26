package com.bkic.lymenglong.audiobookbkic.handleLists.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.handleLists.listChapter.ListChapter;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Book;
import com.bkic.lymenglong.audiobookbkic.overrideTalkBack.PresenterOverrideTalkBack;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import java.util.ArrayList;


public class BookAdapter extends RecyclerView.Adapter {
    private ArrayList<Book> books;
    private Activity activity;
    /*
private int getIdChapter;
private String getTitleChapter, getContentChapter, getfileUrlChapter;
*/

    public BookAdapter(Activity activity, ArrayList<Book> books) {
        this.books = books;
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
            chapterHolder.name.setText(books.get(position).getTitle());
            String sTitle = books.get(position).getTitle();
            String sAuthor = books.get(position).getAuthor();
            String cdLength = null;
            String sContentDescription;
            int iLength = books.get(position).getLength()*1000; // response in sec convert to millisecond
            //check book author
            if (!sAuthor.toLowerCase().trim().equals("null")
                    && !sAuthor.toLowerCase().trim().equals("undefined")) {
                //check book length
                if(iLength!=0) {
                    String sLength = chapterHolder.presenterOverrideTalkBack.getConvertedDuration(iLength);
                    chapterHolder.sLength.setVisibility(View.VISIBLE);
                    chapterHolder.sLength.setText(String.valueOf(sLength));
                    cdLength = chapterHolder.presenterOverrideTalkBack.DurationContentDescription(iLength);
                }else chapterHolder.sLength.setVisibility(View.GONE);

                chapterHolder.subTitle.setText(sAuthor);
                chapterHolder.subTitle.setVisibility(View.VISIBLE);

                //fix content description for item list
                if(cdLength != null) sContentDescription = activity.getResources().getString(
                        R.string.item_book_cd_title_author_length, sTitle, sAuthor,cdLength);
                else sContentDescription = activity.getResources().getString(
                        R.string.item_book_cd_title_author, sTitle, sAuthor);
            } else {
                chapterHolder.subTitle.setVisibility(View.GONE);
                //fix content description for item list
                sContentDescription = activity.getResources().getString(
                        R.string.item_book_cd_title_only,sTitle
                );
            }
            chapterHolder.layoutItem.setContentDescription(sContentDescription);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
//        private ImageView imgNext;
        private TextView subTitle;
        private TextView sLength;
        private View layoutItem;
        private PresenterOverrideTalkBack presenterOverrideTalkBack = new PresenterOverrideTalkBack(activity);
        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title_item);
//            imgNext = itemView.findViewById(R.id.imgNext);
            subTitle = itemView.findViewById(R.id.sub_title_item);
            sLength = itemView.findViewById(R.id.item_length);
            layoutItem = itemView.findViewById(R.id.layout_item_list);
            itemView.setOnClickListener(this);
            //Do allow talk back to read content when user touch screen
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView);
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.title_item));
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.imgNext));
        }

        @Override
        public void onClick(View view) {
            if(view == itemView) {
                /*getIdChapter = books.get(getAdapterPosition()).getId();
                getTitleChapter = books.get(getAdapterPosition()).getTitle();
                getContentChapter = books.get(getAdapterPosition()).getContent();
                getfileUrlChapter = books.get(getAdapterPosition()).getFileUrl();
                showAlertDialog();*/
                Intent intent = new Intent(activity, ListChapter.class);
                intent.putExtra("BookId", books.get(getAdapterPosition()).getId());
                intent.putExtra("BookTitle", books.get(getAdapterPosition()).getTitle());
                intent.putExtra("BookImage", books.get(getAdapterPosition()).getUrlImage());
                intent.putExtra("BookLength", books.get(getAdapterPosition()).getLength());
                intent.putExtra("CategoryId", books.get(getAdapterPosition()).getCategoryId());
                activity.startActivityForResult(intent, Const.REQUEST_CODE_BACK_HOME);
            }
        }
    }

    //region ShowAlertDialog
/*
    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Chọn Dạng Sách");
        builder.setMessage("Bạn muốn chọn dạng nào?");
        builder.setCancelable(false);
        builder.setPositiveButton("Văn bản", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(activity, "Dạng văn bản", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, ViewReading.class);
                intent.putExtra("idChapter", getIdChapter);
                intent.putExtra("titleChapter", getTitleChapter);
                intent.putExtra("content", getContentChapter);
                intent.putExtra("fileUrl", getfileUrlChapter);
                dialogInterface.dismiss();
                activity.startActivity(intent);

            }
        });
        builder.setNegativeButton("Ghi âm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(activity, "Dạng ghi âm", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, PlayControl.class);
                intent.putExtra("idChapter", getIdChapter);
                intent.putExtra("titleChapter", getTitleChapter);
                intent.putExtra("content", getContentChapter);
                intent.putExtra("fileUrl", getfileUrlChapter);
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

    }
*/
    //endregion

}
