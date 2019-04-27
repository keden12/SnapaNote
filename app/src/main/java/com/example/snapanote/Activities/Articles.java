package com.example.snapanote.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.snapanote.R;
import com.example.snapanote.Utils.MainAdapter;
import com.example.snapanote.Utils.MultiTaskHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Articles extends AppCompatActivity {

    ProgressDialog pd;
    String title,description,articleUrl,imageUrl,date;
    TextView textTitle,textDescription,textUrl;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> imgurls = new ArrayList<>();
    ArrayList<String> modules = new ArrayList<>();
    RecyclerView articleList;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    MultiTaskHandler multiTaskHandler;
    CardView note;
    int count = 0;
    DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler);
        //textTitle = (TextView) findViewById(R.id.articleTitle);
        //textDescription = (TextView) findViewById(R.id.articleDescription);
        //textUrl = (TextView) findViewById(R.id.articleUrl);
        articleList = (RecyclerView) findViewById(R.id.articleRecyclerView);
        note = (CardView) findViewById(R.id.articleNote);

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String uid = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("modules");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    modules.add(String.valueOf(postSnapshot.getKey()));
                    Log.d("MyModules",String.valueOf(postSnapshot.getKey()));
                }


                Log.d("ModuleSize",""+modules.size());
                for(int i=0;i<modules.size();i++) {
                    if(modules.get(i).contains(" "))
                    {
                        String replace = modules.get(i).replace(" ", "%20");
                        modules.set(i,replace);
                    }
                    new FetchArticles().execute("https://newsapi.org/v2/everything?q="+modules.get(i)+"&from="+date+"&sortBy=popularity&apiKey=a2325a8864954c48b557525475860caa");

                }



                int totalNumOfTasks = modules.size();
                multiTaskHandler = new MultiTaskHandler(totalNumOfTasks) {
                    @Override
                    protected void onAllTasksCompleted() {
                        if(count == 0)
                        {
                            note.setVisibility(View.VISIBLE);
                        }
                        else {
                            articleList.setHasFixedSize(true);
                            mLayoutManager = new LinearLayoutManager(Articles.this);
                            mAdapter = new MainAdapter(titles, descriptions, urls, imgurls);
                            Log.d("MyCount", "" + titles.size());
                            articleList.setLayoutManager(mLayoutManager);
                            articleList.setAdapter(mAdapter);
                        }
                    }
                };

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }









    private class FetchArticles extends AsyncTask<String, String, String> {



        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);
                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            if(result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    JSONArray articlesArray = json.getJSONArray("articles");
                    JSONObject article = articlesArray.getJSONObject(0);
                    title = article.getString("title");
                    description = article.getString("description");
                    articleUrl = article.getString("url");
                    imageUrl = article.getString("urlToImage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(title != null)
                {
                    count++;
                    //if the image url is empty
                    if (imageUrl == null) {
                        imageUrl = "https://www.legendchapter.fr/wp-content/uploads/2010/12/icon_newsletter.jpg";
                    }

                    //making sure there is no duplicates
                    if(!titles.contains(title)) {
                        titles.add(title);
                        descriptions.add(description);
                        urls.add(articleUrl);
                        imgurls.add(imageUrl);
                    }
                }

                multiTaskHandler.taskComplete();

            }
        }

    }

    public void onBackPressed() {
        finish();
    }


    }






