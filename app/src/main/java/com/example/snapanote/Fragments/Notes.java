package com.example.snapanote.Fragments;
import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.snapanote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;


public class Notes extends Activity {

        public class ImageAdapter extends BaseAdapter {

            private Context mContext;
            ArrayList<String> urls = new ArrayList<String>();

            public ImageAdapter(Context c) {
                mContext = c;
            }

            void add(String url) {
                urls.add(url);
            }

            @Override
            public int getCount() {
                return urls.size();
            }

            @Override
            public Object getItem(int arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            public void clearImages()
            {
                urls.clear();
            }

            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView;
                if (convertView == null) {  // if it's not recycled, initialize some attributes
                    imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new GridView.LayoutParams(420, 420));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setPadding(4, 4, 4, 4);
                    imageView.setRotation(90);
                } else {
                    imageView = (ImageView) convertView;
                }

                String url = urls.get(position);

                Glide
                        .with(mContext)
                        .load(url)
                        .into(imageView);

                return imageView;
            }


        }

        ImageAdapter myImageAdapter;
        CardView card;
        DatabaseReference ref,mRef;
        FirebaseStorage mFirebaseStorage;
        GridView gridview;
        final ArrayList<String> notes = new ArrayList<String>();
        final ArrayList<String> keys = new ArrayList<String>();
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.module);

            gridview = (GridView) findViewById(R.id.gridview);
            myImageAdapter = new ImageAdapter(this);
            gridview.setAdapter(myImageAdapter);
            card = (CardView) findViewById(R.id.noteNotice);

            final FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            final String uid = user.getUid();
            ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("modules").child(Modules.getModuleClicked());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(notes.size()>0)
                    {
                        notes.clear();
                    }
                    if(keys.size()>0)
                    {
                        keys.clear();
                    }
                    myImageAdapter.clearImages();
                    for (DataSnapshot objSnapshot: snapshot.getChildren()) {
                        String module = objSnapshot.getKey();
                        String noteName = objSnapshot.child(module).getKey();
                        keys.add(noteName);
                        Log.e("MyNote","Note -"+noteName);

                        String url = objSnapshot.child("url").getValue(String.class);
                        notes.add(url);
                        Log.e("MyNote","URL -"+url);
                    }


                    if(notes.size()<1)
                    {
                        card.setVisibility(View.VISIBLE);

                    }
                    else {
                        card.setVisibility(View.GONE);

                        for(int i=0; i<notes.size(); i++)
                        {

                            myImageAdapter.add(notes.get(i));
                        }
                        gridview.setOnItemClickListener(myOnItemClickListener);

                    }

                    myImageAdapter.notifyDataSetChanged();
                    gridview.setAdapter(myImageAdapter);


                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("Read failed", firebaseError.getMessage());
                }
            });


       }

           AdapterView.OnItemClickListener myOnItemClickListener
            = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position,
                                long id) {
            String prompt = String.valueOf(position);
            AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(Notes.this);
            View addView = getLayoutInflater().inflate(R.layout.displaynote, null);
            final int index = position;
            ImageView displayNote = addView.findViewById(R.id.noteDisplay);
            FloatingActionButton removeNote = addView.findViewById(R.id.deleteNote);

            Glide.with(addView).load(notes.get(position)).into(displayNote);

            DialogBuilder.setView(addView);
            final AlertDialog dialog = DialogBuilder.create();
            dialog.show();
            removeNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(Notes.this);
                    final View addView = getLayoutInflater().inflate(R.layout.accept_delete, null);

                    final FloatingActionButton delete = addView.findViewById(R.id.acceptDelete);
                    final FloatingActionButton deny = addView.findViewById(R.id.denyDelete);
                    final ProgressBar progress = addView.findViewById(R.id.deleteProgress);


                    DialogBuilder.setView(addView);
                    final AlertDialog dialog2 = DialogBuilder.create();
                    dialog2.show();

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delete.hide();
                            deny.hide();
                            progress.setVisibility(View.VISIBLE);
                            ref.child(keys.get(index)).setValue(null);
                            mFirebaseStorage = FirebaseStorage.getInstance();
                            StorageReference storageRef = mFirebaseStorage.getReference();
                            final FirebaseAuth auth = FirebaseAuth.getInstance();
                            FirebaseUser user = auth.getCurrentUser();
                            final String uid = user.getUid();
                            StorageReference photoRef = storageRef.child("users").child(uid).child(Modules.getModuleClicked()).child(keys.get(index));

                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("MyDelete", "onSuccess: deleted file");
                                    dialog2.cancel();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Log.d("MyDelete", "onFailure: did not delete file");
                                }
                            });

                        }
                    });

                    deny.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.cancel();
                        }
                    });
                }
            });
        }};


        public Boolean deleteImg(int position)
        {

            String targetPath = Environment.getExternalStorageDirectory() + "/"+Modules.getModuleClicked();
            File targetDirector = new File(targetPath);
            File[] files = targetDirector.listFiles();

            for (int i = 0;i<files.length;i++) {

                myImageAdapter.add(files[i].getAbsolutePath());
            }

            File file = new File(new File("/sdcard/"+Modules.getModuleClicked()), "note-"+position);

            if (file.exists()) {
                file.delete();
                return true;
            }else
                {
                return false;
            }




            //Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();




        }


    public void onBackPressed() {
        finish();
    }

}




