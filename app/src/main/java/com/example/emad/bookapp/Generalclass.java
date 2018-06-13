package com.example.emad.bookapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by EMAD on 12/2/2017.
 */

public class Generalclass {
    static HttpURLConnection urlConnection = null;
    private static final String log_cat = Generalclass.class.getSimpleName();

    public Generalclass() {

    }

    public static List<result_object> fetch(String request) {

        URL mrl = converter(request);
        InputStream inputs = null;
        String jsonResponse = null;
        try {
            inputs = makeHttpRequest(mrl);
            jsonResponse = getjsonFromStream(inputs);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputs != null) {
                inputs.close();
            }

            if (inputs != null) {
                inputs.close();
            }


        } catch (IOException e) {
            Log.e(log_cat, "Problem making the HTTP request.", e);
        }


        List<result_object> Books = getingdetailesfromjson(jsonResponse);


        return Books;

    }

    public static URL converter(String ul) {
        URL r = null;
        try {
            r = new URL(ul);
        } catch (MalformedURLException e) {
            Log.e(log_cat, "ther is a problem in converting url", e);
        }
        return r;

    }

    public static InputStream makeHttpRequest(URL ul) throws IOException {

        InputStream inputStream = null;

        // URL checking
        if (ul == null) {
            return inputStream;
        }


        try {
            urlConnection = (HttpURLConnection) ul.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // check the connection
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();

            } else {
                Log.e(log_cat, "Error in connection: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(log_cat, "ther is aproblem in getting json.", e);
        }
        return inputStream;


    }

    private static String getjsonFromStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                line = reader.readLine();
            }
        }
        //converting from stringbuilder to string
        return result.toString();
    }


    //
    public static List<result_object> getingdetailesfromjson(String json_response) {


        // Creaing list to add books in it
        List<result_object> Books = new ArrayList<>();

        //Json Parsing
        try {
            JSONObject baseJsonResponse = new JSONObject(json_response);
            JSONArray BookArray = baseJsonResponse.getJSONArray("items");
            int j = 0;
            while (j < BookArray.length()) {

                JSONObject curBook = BookArray.getJSONObject(j);
                JSONObject bookVolumeInfo = curBook.getJSONObject("volumeInfo");

                String title;
                String exact_author;
                String description;
                // geting the value for the title
                title = bookVolumeInfo.getString("title");

                // geting values from the array of the authors
                JSONArray authorsArr;
                StringBuilder author = new StringBuilder();
                //getting the authors of every book from authors array
                if (bookVolumeInfo.has("authors")) {
                    authorsArr = bookVolumeInfo.getJSONArray("authors");
                    for (int f = 0; f < authorsArr.length(); f++) {
                        author.append(authorsArr.getString(f));

                        if (f != authorsArr.length() - 1) {
                            author.append(",and ");
                        } else {
                            author.append(".");
                        }
                    }

                } else {

                    author.append(" ther is No Authors");
                }


                exact_author = String.valueOf(author);

                // Extract the descreption of the book

                if (bookVolumeInfo.has("description")) {
                    description = bookVolumeInfo.getString("description");
                } else {
                    description = " ther is  No Description";
                }
//extracting the link of the book
                String theLink;
                if (bookVolumeInfo.has("infoLink")) {
                    theLink = bookVolumeInfo.getString("infoLink");

                } else {
                    theLink = null;
                }


                result_object book = new result_object(title, exact_author, description, theLink);
                Books.add(book);
                j++;
            }

        } catch (JSONException e) {
            Log.e("Generalclass", "ther is a problem in parsing ", e);
        }


        return Books;
    }


}
