package com.bkic.lymenglong.audiobookbkic.handleLists.history;

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

import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Book;
import com.bkic.lymenglong.audiobookbkic.overrideTalkBack.PresenterOverrideTalkBack;
import com.bkic.lymenglong.audiobookbkic.utils.Const;
import com.bkic.lymenglong.audiobookbkic.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter {
    private ArrayList<Book> books;
    private Activity activity;
    private int bookId, bookLength;
    private String bookTitle, bookImage, bookAuthor;

    HistoryAdapter(Activity activity, ArrayList<Book> books) {
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

    class ChapterHolder extends RecyclerView.ViewHolder {
        private final TextView subTitle;
        private final TextView sLength;
        private TextView name;
//        private ImageView imgNext;
        private PresenterOverrideTalkBack presenterOverrideTalkBack = new PresenterOverrideTalkBack(activity);
        private View layoutItem;


        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title_item);
//            imgNext = itemView.findViewById(R.id.imgNext);
            subTitle = itemView.findViewById(R.id.sub_title_item);
            sLength = itemView.findViewById(R.id.item_length);
            layoutItem = itemView.findViewById(R.id.layout_item_list);


            itemView.setOnClickListener(onClickListener);
            itemView.setOnLongClickListener(onLongClickListener);

            //Do allow talk back to read content when user touch screen
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView);
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.title_item));
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.imgNext));

        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == itemView) {
                    bookId = books.get(getAdapterPosition()).getId();
                    bookTitle = books.get(getAdapterPosition()).getTitle();
                    bookImage = books.get(getAdapterPosition()).getUrlImage();
                    bookLength = books.get(getAdapterPosition()).getLength();
                    bookAuthor = books.get(getAdapterPosition()).getAuthor();
                    IntentActivity(activity,ListHistoryChapter.class);
//                    showAlertDialog();
                }
            }
        };
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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
        };
    }

    private void IntentActivity(Activity activity, Class classIntent) {
        Intent intent = new Intent(activity, classIntent);
        intent.putExtra("BookId", bookId);
        intent.putExtra("BookTitle", bookTitle);
        intent.putExtra("BookImage", bookImage);
        intent.putExtra("BookLength", bookLength);
        intent.putExtra("BookAuthor", bookAuthor);
        activity.startActivityForResult(intent, Const.REQUEST_CODE_BACK_HOME);
    }
    private Book bookModel;
    private int adapterPosition;
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
                Toast.makeText(activity, bookModel.getTitle()+ " Đã Xóa", Toast.LENGTH_SHORT).show();
                RemoveHistoryData(bookModel.getId());
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
    private void RemoveHistoryData(int bookId){
        DBHelper dbHelper = new DBHelper(activity, Const.DB_NAME, null, Const.DB_VERSION);
        /*dbHelper.QueryData(
                "UPDATE history " +
                        "SET BookRemoved = '"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"' " +
                        "WHERE BookId = '"+bookId+"'"
        );*/

        try {
            dbHelper.QueryData(
                    "INSERT INTO bookHistorySyncs " +
                            "VALUES " +
                            "(" +
                            "'"+bookId+"', " +
                            "'"+Const.BOOK_SYNCED_WITH_SERVER+"', " +
                            "'"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"'" +
                            ")" +
                        ";"
            );
        } catch (Exception ignored) {
            dbHelper.QueryData(
                    "UPDATE bookHistorySyncs " +
                            "SET " +
                            "BookSync = '"+Const.BOOK_SYNCED_WITH_SERVER+"', " +
                            "BookRemoved = '"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"' " +
                            "WHERE BookId = '"+bookId+"'" +
                            ";"
            );
        }

        dbHelper.QueryData("DELETE FROM history WHERE BookId = '"+bookId+"'");

        dbHelper.close();
    }
}
