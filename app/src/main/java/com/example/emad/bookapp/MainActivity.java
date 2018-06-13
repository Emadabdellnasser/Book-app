package com.example.emad.bookapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<result_object>> {
    private String requested_url = "https://www.googleapis.com/books/v1/volumes?q=";

    private LoaderManager loaderManager;
    private static int loaded_book_id = 1;
    private myAdapter madapter;
    private ProgressBar progbar;
    EditText txt_search;
    Button bt_search;
    ListView lv;
    TextView emptytxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.book_listview);
        emptytxt = (TextView) findViewById(R.id.emptyview);
        bt_search = (Button) findViewById(R.id.bt_search);
        progbar = (ProgressBar) findViewById(R.id.progressbar);
        txt_search = (EditText) findViewById(R.id.search_edittext);
        progbar.setVisibility(View.GONE);
        lv.setEmptyView(emptytxt);
//reference to the inner class array adapter
        madapter = new myAdapter(this, new ArrayList<result_object>());
        lv.setAdapter(madapter);
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //checking network connection
        if (networkInfo != null && networkInfo.isConnected()) {
            //default starting daata
            requested_url = requested_url + "Java";
            loaderManager = getLoaderManager();
            loaderManager.initLoader(loaded_book_id, null, MainActivity.this);
        } else {
            emptytxt.setText("check your network connection");
        }
// handling the search buttom
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String queried_book = txt_search.getText().toString();
                //if clicked the buttom without entering anything to search
                if (txt_search.length() == 0) {
                    txt_search.setHint(" you shoud enter data to search");
                }
                //if Entered data
                else {
                    loaded_book_id += 1;
                    requested_url = "https://www.googleapis.com/books/v1/volumes?q=";
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    //checking network connection
                    if (networkInfo != null && networkInfo.isConnected()) {

                        //to hide ancient data in book list and set the new

                        madapter.clear();
                        // Remove  spaces from user's querey
                        String edited_user_query = queried_book.replaceAll("\\s+", "");
                        // Update the requested url
                        requested_url = requested_url + edited_user_query;
                        // getting the data from general class
                        loaderManager = getLoaderManager();
                        loaderManager.initLoader(loaded_book_id, null, MainActivity.this);
                    }
                    //if ther is no connection
                    else {
                        //cleaning the addapter
                        madapter.clear();

                        progbar.setVisibility(View.GONE);
                        //tell the user that ther is no internet connection
                        emptytxt.setText("no network connection");
                    }

                }
            }
        });
//when the user press on any item in the list
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                result_object etnBook = madapter.getItem(i);
                String link = etnBook.getlink();
                if (link != null) {
                    Uri bookUri = Uri.parse(link);
                    Intent booksite = new Intent(Intent.ACTION_VIEW, bookUri);
                    startActivity(booksite);
                } else {

                    Toast t = Toast.makeText(MainActivity.this, "no link provided", Toast.LENGTH_LONG);
                    t.show();

                }
            }
        });
    }

    //loadmanager functions
    @Override
    public Loader<List<result_object>> onCreateLoader(int i, Bundle bundle) {
        progbar.setVisibility(View.VISIBLE);
        Uri main_uri = Uri.parse(requested_url);
        Uri.Builder URI_builder = main_uri.buildUpon();

        return new Asynclass(this, URI_builder.toString());

    }


    @Override
    public void onLoaderReset(Loader<List<result_object>> loader) {
        madapter.clear();
    }

    @Override
    public void onLoadFinished(Loader<List<result_object>> loader, List<result_object> books) {

        progbar.setVisibility(View.GONE);

        madapter.clear();

        //if have data update list
        if (books != null && !books.isEmpty()) {
            madapter.addAll(books);
        }
        else
        {
            emptytxt.setText("ther is no results");
        }
    }


    class myAdapter extends ArrayAdapter<result_object> {

        ArrayList<result_object> Booklist;
        Context context;
        LayoutInflater linf;

        public myAdapter(Context context, ArrayList<result_object> objects) {
            super(context, 0, objects);
            Booklist = objects;
            this.context = context;
            linf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //List checking
            View listItemView = convertView;
            holder_of_view holder;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item, parent, false);

                // using holder in findview
                holder = new holder_of_view();
                holder.Name = (TextView) listItemView.findViewById(R.id.bookname);
                holder.author = (TextView) listItemView.findViewById(R.id.authors);
                holder.information = (TextView) listItemView.findViewById(R.id.information);

                listItemView.setTag(holder);
            } else {
                holder = (holder_of_view) listItemView.getTag();
            }
            //set data
            holder.Name.setText(Booklist.get(position).getname());
            holder.author.setText(Booklist.get(position).getauthor());
            holder.information.setText(Booklist.get(position).getdescreption());

            return listItemView;
        }

        //the holder of the class
        class holder_of_view {
            public TextView Name;
            public TextView author;
            public TextView information;
        }


    }
}