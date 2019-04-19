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
import android.widget.Toast;

import com.example.snapanote.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.cardview.widget.CardView;


public class Notes extends Activity {

        public class ImageAdapter extends BaseAdapter {

            private Context mContext;
            ArrayList<String> itemList = new ArrayList<String>();

            public ImageAdapter(Context c) {
                mContext = c;
            }

            void add(String path) {
                itemList.add(path);
            }

            @Override
            public int getCount() {
                return itemList.size();
            }

            @Override
            public Object getItem(int arg0) {
                // TODO Auto-generated method stub
                return null;
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

                Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 420, 420);

                imageView.setImageBitmap(bm);
                return imageView;
            }

            public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

                Bitmap bm = null;
                // First decode with inJustDecodeBounds=true to check dimensions
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(path, options);

                return bm;
            }

            public int calculateInSampleSize(

                    BitmapFactory.Options options, int reqWidth, int reqHeight) {
                // Raw height and width of image
                final int height = options.outHeight;
                final int width = options.outWidth;
                int inSampleSize = 1;

                if (height > reqHeight || width > reqWidth) {
                    if (width > height) {
                        inSampleSize = Math.round((float) height / (float) reqHeight);
                    } else {
                        inSampleSize = Math.round((float) width / (float) reqWidth);
                    }
                }

                return inSampleSize;
            }

        }

        ImageAdapter myImageAdapter;
        CardView card;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.module);

            GridView gridview = (GridView) findViewById(R.id.gridview);
            myImageAdapter = new ImageAdapter(this);
            gridview.setAdapter(myImageAdapter);
            card = (CardView) findViewById(R.id.noteNotice);

            File directory = new File(Environment.getExternalStorageDirectory() + "/" +Modules.getModuleClicked());

            if (!directory.exists()) {
                File makeDir = new File("/sdcard/"+Modules.getModuleClicked());
                makeDir.mkdirs();
            }
            String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory() + "/"+Modules.getModuleClicked();

            String targetPath = ExternalStorageDirectoryPath;

            //Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
            File targetDirector = new File(targetPath);

            File[] files = targetDirector.listFiles();

            if(files.length<1)
            {
             card.setVisibility(View.VISIBLE);

            }
            else {
                card.setVisibility(View.GONE);
                for (File file : files) {

                    myImageAdapter.add(file.getAbsolutePath());
                }

                gridview.setOnItemClickListener(myOnItemClickListener);

            }
        }

    AdapterView.OnItemClickListener myOnItemClickListener
            = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            String prompt = (String)parent.getItemAtPosition(position);
            File imgFile = new  File("/sdcard/"+Modules.getModuleClicked()+"/note-"+(position+1));
            if(imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(Notes.this);
                View addView = getLayoutInflater().inflate(R.layout.displaynote, null);

                ImageView displayNote = addView.findViewById(R.id.noteDisplay);

                displayNote.setImageBitmap(myBitmap);
                DialogBuilder.setView(addView);
                final AlertDialog dialog = DialogBuilder.create();
                dialog.show();

            }


            Log.d("MyTag ="," "+position);

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

}




