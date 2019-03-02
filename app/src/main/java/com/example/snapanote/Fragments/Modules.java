package com.example.snapanote.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.snapanote.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Modules extends Fragment {

    CardView note;
    ListView listmodules;
    FloatingActionButton addModule;
    BottomAppBar bar;
    List<String> modulelist = new ArrayList<String>();
    public static String moduleClicked = "";

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.modulelist, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        note = (CardView) view.findViewById(R.id.moduleNote);
        listmodules = (ListView) view.findViewById(R.id.listModules);
        addModule = (FloatingActionButton) view.findViewById(R.id.addModules);
        bar = (BottomAppBar)  view.findViewById(R.id.bottombar);
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("modules");



        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                if (snapshot.hasChild("InitialRecord001122")) {
                    note.setVisibility(View.VISIBLE);
                    listmodules.setVisibility(View.INVISIBLE);
                    modulelist = new ArrayList<String>();
                    Log.e("Count " ,""+snapshot.getChildrenCount());
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        modulelist.add(String.valueOf(postSnapshot.getKey()));
                    }
                }
                else{
                    note.setVisibility(View.INVISIBLE);
                    listmodules.setVisibility(View.VISIBLE);
                    modulelist = new ArrayList<String>();
                    Log.e("Count " ,""+snapshot.getChildrenCount());
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        modulelist.add(String.valueOf(postSnapshot.getKey()));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            getActivity(),
                            android.R.layout.simple_list_item_1,
                            modulelist ){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent){
                            // Get the Item from ListView
                            View view = super.getView(position, convertView, parent);

                            // Initialize a TextView for ListView each Item
                            TextView tv = (TextView) view.findViewById(android.R.id.text1);

                            // Set the text color of TextView (ListView Item)
                            tv.setTextColor(Color.BLACK);
                            tv.setTextSize(22);
                            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_school, 0, 0, 0);


                            // Generate ListView Item using TextView
                            return view;
                        }
                    };

                    listmodules.setAdapter(arrayAdapter);

                    listmodules.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                            String pos= modulelist.get(position);
                            moduleClicked = modulelist.get(position);
                            Log.d("MyTag",pos);
                            Intent intent = new Intent(getActivity(), Notes.class);
                            startActivity(intent);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        bar.setNavigationOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 rootRef.addValueEventListener(new ValueEventListener() {


                                                                                   @Override
                                                                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                       modulelist = new ArrayList<String>();
                                                                                       Log.e("Count ", "" + snapshot.getChildrenCount());
                                                                                       for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                                                           modulelist.add(String.valueOf(postSnapshot.getKey()));
                                                                                       }
                                                                                   }

                                                                                   @Override
                                                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                   }
                                                                               });
                                                 AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(getActivity());
                                                 View addView = getLayoutInflater().inflate(R.layout.moduledelete, null);
                                                 final EditText deleteModule = (EditText) addView.findViewById(R.id.deleteModuleName);
                                                 FloatingActionButton submitDelete = (FloatingActionButton) addView.findViewById(R.id.submitDelete);
                                                 DialogBuilder.setView(addView);
                                                 final AlertDialog dialog = DialogBuilder.create();
                                                 dialog.show();

                                                 submitDelete.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {
                                                         Boolean checkIfModuleExists = false;
                                                         String getModule = deleteModule.getText().toString();
                                                         for (int i = 0; i < modulelist.size(); i++) {
                                                             Log.d("MyTag", modulelist.get(i));

                                                             if(getModule.equals(modulelist.get(i))) {
                                                                 checkIfModuleExists = true;

                                                             }
                                                         }



                                                         if(checkIfModuleExists)
                                                         {

                                                             if(Logged.getCurrentSetModule().equals(getModule))
                                                             {
                                                                 Logged.setCurrentModule("");
                                                             }
                                                             if(modulelist.size()== 1)
                                                             {
                                                                 rootRef.child("InitialRecord001122").setValue(2);
                                                             }
                                                             //Delete all local contents of the module
                                                             File dir = new File(Environment.getExternalStorageDirectory() + "/" + getModule);
                                                             if (dir.isDirectory())
                                                             {
                                                                 String[] children = dir.list();
                                                                 for (int i = 0; i < children.length; i++)
                                                                 {
                                                                     new File(dir, children[i]).delete();
                                                                 }
                                                             }
                                                             rootRef.child(getModule).removeValue();

                                                             dialog.cancel();



                                                         } else
                                                         {
                                                             deleteModule.setError("Module does not exist");
                                                         }

                                                     }
                                                 });

                                             }
                                         });



        addModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(getActivity());
                View addView = getLayoutInflater().inflate(R.layout.addmodules, null);

                final EditText ModuleName = addView.findViewById(R.id.deleteModuleName);
                FloatingActionButton Submit = addView.findViewById(R.id.submitModule);


                DialogBuilder.setView(addView);
                final AlertDialog dialog = DialogBuilder.create();
                dialog.show();

                Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String module = ModuleName.getText().toString();
                        rootRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                modulelist = new ArrayList<String>();
                                Log.e("Count ", "" + snapshot.getChildrenCount());
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    modulelist.add(String.valueOf(postSnapshot.getKey()));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        if(modulelist.contains(module)) {
                            for (int i = 0; i < modulelist.size(); i++) {
                                Log.d("MyTag", modulelist.get(i));

                                if (modulelist.get(i).equals(module)) {
                                    ModuleName.setError("Module already exists!");
                                }

                            }
                        }
                        else if (module.isEmpty()) {
                            ModuleName.setError("Field is Empty");
                        } else {
                            if (modulelist.contains("InitialRecord001122")) {

                                        rootRef.child("InitialRecord001122").removeValue();

                                }

                            note.setVisibility(View.INVISIBLE);
                            listmodules.setVisibility(View.VISIBLE);


                            rootRef.child(module).setValue(Long.valueOf(0));
                            dialog.cancel();
                        }



                    }
                });


            }
        });



        return view;

    }



    public static String getModuleClicked()
    {
        return moduleClicked;
    }





    }
