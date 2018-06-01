package com.bkic.lymenglong.audiobookbkic.handleLists.history;

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
import com.bkic.lymenglong.audiobookbkic.utils.Const;
import com.bkic.lymenglong.audiobookbkic.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter {
    private ArrayList<Book> books;
    private Activity activity;
    private int bookId, bookLength;
    private String bookTitle, bookImage, bookAuthor;

    public HistoryAdapter(Activity activity, ArrayList<Book> books) {
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

        private TextView name;
//        private ImageView imgNext;

        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameStory);
//            imgNext = itemView.findViewById(R.id.imgNext);

            itemView.setOnClickListener(onClickListener);
            itemView.setOnLongClickListener(onLongClickListener);
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
        activity.startActivity(intent);
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
