package com.bkic.lymenglong.audiobookbkic.handleLists.favorite;

import android.annotation.SuppressLint;
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
import com.bkic.lymenglong.audiobookbkic.handleLists.listChapter.ListChapter;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Book;
import com.bkic.lymenglong.audiobookbkic.overrideTalkBack.PresenterOverrideTalkBack;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FavoriteAdapter extends RecyclerView.Adapter {
    private ArrayList<Book> books;
    private Activity activity;
    private Book bookModel;
    private int adapterPosition;
    //    private int getIdChapter;
//    private String getTitleChapter,getContentChapter, getFileUrlChapter;

    FavoriteAdapter(Activity activity, ArrayList<Book> books) {
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
            if (!sAuthor.toLowerCase().trim().equals("null")) {
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

    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView name;
//        private ImageView imgNext;
        private PresenterOverrideTalkBack presenterOverrideTalkBack = new PresenterOverrideTalkBack(activity);
        private View layoutItem;
        private final TextView subTitle;
        private final TextView sLength;

        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title_item);
//            imgNext = itemView.findViewById(R.id.imgNext);
            layoutItem = itemView.findViewById(R.id.layout_item_list);
            subTitle = itemView.findViewById(R.id.sub_title_item);
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
            bookModel = new Book(
                    books.get(getAdapterPosition()).getId(),
                    books.get(getAdapterPosition()).getTitle(),
                    books.get(getAdapterPosition()).getUrlImage(),
                    books.get(getAdapterPosition()).getLength(),
                    books.get(getAdapterPosition()).getAuthor()
            );
            adapterPosition = getAdapterPosition();
            showAlertDialog();
            return true;
        }

        @Override
        public void onClick(View view) {
            if(view == itemView) {
                /*
                getIdChapter = books.get(getAdapterPosition()).getId();
                getTitleChapter = books.get(getAdapterPosition()).getTitle();
                getContentChapter = books.get(getAdapterPosition()).getContent();
                getFileUrlChapter = books.get(getAdapterPosition()).getFileUrl();
                showAlertDialog();
                */
                Intent intent = new Intent(activity, ListChapter.class);
                intent.putExtra("BookId", books.get(getAdapterPosition()).getId());
                intent.putExtra("BookTitle", books.get(getAdapterPosition()).getTitle());
                intent.putExtra("BookImage", books.get(getAdapterPosition()).getUrlImage());
                intent.putExtra("BookLength", books.get(getAdapterPosition()).getLength());
                intent.putExtra("BookAuthor", books.get(getAdapterPosition()).getAuthor());
                activity.startActivityForResult(intent,Const.REQUEST_CODE_BACK_HOME);
            }
        }
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Chọn Dạng Sách");
        builder.setMessage("Bạn muốn xóa khỏi danh sách không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                books.remove(adapterPosition);
                notifyDataSetChanged();
                Toast.makeText(activity, bookModel.getTitle()+" Đã Xóa", Toast.LENGTH_SHORT).show();
                RemoveFavoriteData(bookModel.getId());
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
    private void RemoveFavoriteData(int bookId){
        DBHelper dbHelper = new DBHelper(activity, Const.DB_NAME, null, Const.DB_VERSION);
        dbHelper.QueryData("UPDATE favorite SET BookRemoved = '"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"' WHERE BookId = '"+bookId+"'");
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String insertTime = simpledateformat.format(calendar.getTime());
        try {
            dbHelper.QueryData(
                    "INSERT INTO bookFavoriteSyncs " +
                            "VALUES " +
                            "(" +
                                    "'"+bookId+"', " +
                                    "'"+Const.BOOK_SYNCED_WITH_SERVER+"', " +
                                    "'"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"', " +
                                    "'"+insertTime+"'" +
                            ")" +
                        ";"
            );
        } catch (Exception ignored) {
            dbHelper.QueryData(
                    "UPDATE bookFavoriteSyncs " +
                            "SET " +
                            "BookSync = '"+Const.BOOK_SYNCED_WITH_SERVER+"', " +
                            "BookRemoved = '"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"', " +
                            "InsertTime = '"+insertTime+"' " +
                            "WHERE BookId = '"+bookId+"'" +
                            ";"
            );
        }

        dbHelper.QueryData("DELETE FROM favorite WHERE BookId = '"+bookId+"'");

        dbHelper.close();
    }
}
