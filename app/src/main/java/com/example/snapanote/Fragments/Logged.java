package com.example.snapanote.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snapanote.Activities.Help;
import com.example.snapanote.Activities.LoggedIn;
import com.example.snapanote.Activities.Articles;
import com.example.snapanote.Activities.SignInGoogle;
import com.example.snapanote.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
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
import com.google.firebase.storage.UploadTask;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Logged extends Fragment {

    FloatingActionButton camera;
    ImageView scannedImageView;
    CardView modules,help,settings,logout;
    BottomAppBar bar;
    TextView currentPicked;
    static String SetModule = "";
    private StorageReference sRef;
    private FirebaseAuth auth;
    GoogleApiClient mGoogleApiClient;
    DatabaseReference myRef;
    List<String> modulelist = new ArrayList<String>();
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.logged, container, false);

        BottomAppBar bar = (BottomAppBar) view.findViewById(R.id.bar);
        camera = (FloatingActionButton) view.findViewById(R.id.photo);
        modules = (CardView) view.findViewById(R.id.moduleCard);
        help = (CardView) view.findViewById(R.id.helpCard);
        currentPicked = (TextView) view.findViewById(R.id.current);
        settings = (CardView) view.findViewById(R.id.settingsCard);
        logout = (CardView) view.findViewById(R.id.logoutCard);

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("modules");





        bar.setNavigationOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 rootRef.addValueEventListener(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(DataSnapshot snapshot) {
                                                         modulelist = new ArrayList<String>();
                                                         Log.e("Count ", "" + snapshot.getChildrenCount());
                                                         for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                             modulelist.add(String.valueOf(postSnapshot.getKey()));
                                                         }

                                                         if(SetModule.equals(""))
                                                         {
                                                             currentPicked.setText("-Empty-");
                                                         } else {
                                                             currentPicked.setText(SetModule);
                                                         }


                                                     }




                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                                     }
                                                 });

                                                 /*if (snapshot.hasChild("InitialRecord001122")) {
                                                     AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(getActivity());
                                                     View addView = getLayoutInflater().inflate(R.layout.emptylist, null);
                                                     DialogBuilder.setView(addView);
                                                     final AlertDialog dialog = DialogBuilder.create();
                                                     dialog.show();
                                                 }*/



                                                     AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(getActivity());
                                                     View addView = getLayoutInflater().inflate(R.layout.setmodule, null);
                                                     final EditText currentModule = (EditText) addView.findViewById(R.id.currentModule);
                                                     FloatingActionButton submitCurrentModule = (FloatingActionButton) addView.findViewById(R.id.submitCurrentModule);
                                                     DialogBuilder.setView(addView);
                                                     final AlertDialog dialog = DialogBuilder.create();
                                                     dialog.show();

                                                     submitCurrentModule.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             Boolean checkIfModuleExists = false;
                                                             String getModule = currentModule.getText().toString();

                                                             for (int i = 0; i < modulelist.size(); i++) {
                                                                 Log.d("MyTag", modulelist.get(i));

                                                                 if(getModule.equals(modulelist.get(i))) {
                                                                     checkIfModuleExists = true;

                                                                 }
                                                             }

                                                             if(checkIfModuleExists)
                                                             {
                                                                 SetModule = getModule;
                                                                 currentPicked.setText(getModule);
                                                                 dialog.cancel();
                                                             }
                                                             else
                                                             {
                                                                 currentModule.setError("Module doesn't exist");
                                                             }
                                                         }

                                                     });





                                             }
                                         });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean checkIfModuleExists = false;

                for (int i = 0; i < modulelist.size(); i++) {
                    Log.d("MyTag", modulelist.get(i));

                    if(SetModule.equals(modulelist.get(i))) {
                        checkIfModuleExists = true;

                    }
                }

                if(SetModule.equals(""))
                {
                    AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(getActivity());
                    View addView = getLayoutInflater().inflate(R.layout.notpicked, null);
                    DialogBuilder.setView(addView);
                    final AlertDialog dialog = DialogBuilder.create();
                    dialog.show();
                } else if(!checkIfModuleExists)
                {
                    AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(getActivity());
                    View addView = getLayoutInflater().inflate(R.layout.notexist, null);
                    DialogBuilder.setView(addView);
                    final AlertDialog dialog = DialogBuilder.create();
                    dialog.show();
                }
                else {

                   openCamera(view);
                }
            }
        });

        modules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoggedIn)getActivity()).setViewPager(1);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Help.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Articles.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Intent intent = new Intent(getActivity(), SignInGoogle.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;

    }

    public static String getCurrentSetModule()
    {
        return SetModule;
    }

    public static void setCurrentModule(String s)
    {
        SetModule = s;
    }

    public void openCamera(View v){

        int REQUEST_CODE = 99;
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);



    }


    public void callParentMethod(){

        getActivity().onBackPressed();
    }

    @Override
    public void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(getActivity());
            View addView = getLayoutInflater().inflate(R.layout.upload, null);

            DialogBuilder.setView(addView);
            final AlertDialog dialog = DialogBuilder.create();
            dialog.show();


                myRef = FirebaseDatabase.getInstance().getReference();
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                sRef = FirebaseStorage.getInstance().getReference();
                FirebaseUser user = auth.getCurrentUser();
                final String uid = user.getUid();

                try {
                    final String randomString = getRandomString(6);
                    StorageReference storageReference = sRef.child("users/"+uid+"/"+SetModule+"/note-"+randomString);
                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Log.d("MyTag","Upload Success");
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                     @Override
                                      public void onSuccess(Uri uri) {

                                         String upload = uri.toString();
                                         myRef.child("users").child(uid).child("modules").child(SetModule).child("note-"+randomString).child("url").setValue(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 dialog.cancel();
                                             }
                                         });


                                      }

                            });


                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // getActivity().getContentResolver().delete(uri, null, null);
                // scannedImageView.setImageBitmap(bitmap);
        }
    }

    private static String getRandomString(final int sizeOfRandomString)
    {
        String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }







}
