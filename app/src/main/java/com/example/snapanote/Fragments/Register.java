package com.example.snapanote.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.snapanote.Activities.Base;
import com.example.snapanote.Activities.LoggedIn;
import com.example.snapanote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends Fragment {


    Button Register,Back;
    ProgressBar progress;
    EditText username,emailreg,pass,confpass;
    TextView error;
    private FirebaseAuth auth;
    private DatabaseReference db;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.register,container,false);

        Register = (Button) view.findViewById(R.id.registerButton);
        Back = (Button) view.findViewById(R.id.regback);
        progress = (ProgressBar) view.findViewById(R.id.regprogress);
        username = (EditText) view.findViewById(R.id.registerName);
        emailreg = (EditText) view.findViewById(R.id.registerEmail);
        pass = (EditText) view.findViewById(R.id.registerPassword);
        confpass = (EditText) view.findViewById(R.id.registerConfirmPassword);
        error = (TextView) view.findViewById(R.id.regerror);


        //getting the FirebaseAuth instance and Database reference
        db = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        //if the register button is clicked
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hiding the buttons, clearing the error if it already appeared and showing the progressbar
                Register.setVisibility(View.INVISIBLE);
                Back.setVisibility(View.INVISIBLE);
                error.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);


                //getting the values
                String email = emailreg.getText().toString();
                String password = pass.getText().toString();
                String confpassword = confpass.getText().toString();
                final String name = username.getText().toString();



                //if the email given is not valid
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    progress.setVisibility(View.GONE);
                    Register.setVisibility(View.VISIBLE);
                    Back.setVisibility(View.VISIBLE);
                    emailreg.setError("Please enter a valid email");
                    emailreg.requestFocus();
                    return;

                }


                //if email field is empty
                if(email.isEmpty())
                {
                    progress.setVisibility(View.GONE);
                    Register.setVisibility(View.VISIBLE);
                    Back.setVisibility(View.VISIBLE);
                    emailreg.setError("Email is empty");
                    emailreg.requestFocus();
                    return;
                }

                //if username field is empty
                if(name.isEmpty())
                {
                    progress.setVisibility(View.GONE);
                    Register.setVisibility(View.VISIBLE);
                    Back.setVisibility(View.VISIBLE);
                    username.setError("Username is empty");
                    username.requestFocus();
                    return;
                }

                //if the password field is empty
                if(password.isEmpty())
                {
                    progress.setVisibility(View.GONE);
                    Register.setVisibility(View.VISIBLE);
                    Back.setVisibility(View.VISIBLE);
                    pass.setError("Password is empty");
                    pass.requestFocus();
                    return;
                }

                //if the password given is less than 6
                if(password.length()<6)
                {
                    progress.setVisibility(View.GONE);
                    Register.setVisibility(View.VISIBLE);
                    Back.setVisibility(View.VISIBLE);
                    pass.setError("Minimum password length is 6");
                    pass.requestFocus();
                    return;
                }



                //if confirm password is empty
                if(confpassword.isEmpty())
                {
                    progress.setVisibility(View.GONE);
                    Register.setVisibility(View.VISIBLE);
                    Back.setVisibility(View.VISIBLE);
                    confpass.setError("Confirm Password");
                    confpass.requestFocus();
                    return;
                }

                //if confirm password doesn't match the password
                if(!confpassword.matches(password))
                {
                    progress.setVisibility(View.GONE);
                    Register.setVisibility(View.VISIBLE);
                    Back.setVisibility(View.VISIBLE);
                    confpass.setError("Passwords don't match");
                    confpass.requestFocus();
                    return;
                }

                //attempt to create the user
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        //if it's successfull, get the user ID, get the current user, set the user's display name as the username given, create a path in the database with the unique user id
                        if(task.isSuccessful())
                        {
                            String Uid = task.getResult().getUser().getUid().toString();


                            FirebaseUser user = auth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();

                            user.updateProfile(profileUpdates);


                            db.child("users").child(Uid).child("modules").child("InitialRecord001122").setValue(2);
                            Intent intent = new Intent(getActivity(), LoggedIn.class);
                            startActivity(intent);


                        }

                        //otherwise hide the progressbar, show the 2 buttons again, if its caused by user collision, show the error on the email field, otherwise display a textview error
                        else
                        {
                            progress.setVisibility(View.GONE);
                            Register.setVisibility(View.VISIBLE);
                            Back.setVisibility(View.VISIBLE);

                            if(task.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                emailreg.setError("Email already registered!");
                                emailreg.requestFocus();
                                return;
                            }

                            else {
                                error.setVisibility(View.VISIBLE);
                            }
                        }




                    }
                });




            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Base)getActivity()).setViewPager(0);
            }
        });






        return view;


    }



    private void registerUser()
    {

        Register.setVisibility(View.INVISIBLE);
        Back.setVisibility(View.INVISIBLE);
        error.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);


        String email = emailreg.getText().toString();
        String password = pass.getText().toString();
        String confpassword = confpass.getText().toString();
        final String name = username.getText().toString();


        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            progress.setVisibility(View.GONE);
            Register.setVisibility(View.VISIBLE);
            Back.setVisibility(View.VISIBLE);
            emailreg.setError("Please enter a valid email");
            emailreg.requestFocus();
            return;

        }


        if(email.isEmpty())
        {
            progress.setVisibility(View.GONE);
            Register.setVisibility(View.VISIBLE);
            Back.setVisibility(View.VISIBLE);
            emailreg.setError("Email is empty");
            emailreg.requestFocus();
            return;
        }

        if(name.isEmpty())
        {
            progress.setVisibility(View.GONE);
            Register.setVisibility(View.VISIBLE);
            Back.setVisibility(View.VISIBLE);
            username.setError("Username is empty");
            username.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            progress.setVisibility(View.GONE);
            Register.setVisibility(View.VISIBLE);
            Back.setVisibility(View.VISIBLE);
            pass.setError("Password is empty");
            pass.requestFocus();
            return;
        }
        if(password.length()<6)
        {
            progress.setVisibility(View.GONE);
            Register.setVisibility(View.VISIBLE);
            Back.setVisibility(View.VISIBLE);
            pass.setError("Minimum password length is 6");
            pass.requestFocus();
            return;
        }



        if(confpassword.isEmpty())
        {
            progress.setVisibility(View.GONE);
            Register.setVisibility(View.VISIBLE);
            Back.setVisibility(View.VISIBLE);
            confpass.setError("Confirm Password");
            confpass.requestFocus();
            return;
        }

        if(!confpassword.matches(password))
        {
            progress.setVisibility(View.GONE);
            Register.setVisibility(View.VISIBLE);
            Back.setVisibility(View.VISIBLE);
            confpass.setError("Passwords don't match");
            confpass.requestFocus();
            return;
        }

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful())
                {
                    String Uid = task.getResult().getUser().getUid().toString();


                    FirebaseUser user = auth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();

                    user.updateProfile(profileUpdates);


                    db.child("users").child(Uid).child("modules").child("test").setValue(Long.valueOf(1));
                    ((Base)getActivity()).setViewPager(0);


                }
                else
                {
                    progress.setVisibility(View.GONE);
                    Register.setVisibility(View.VISIBLE);
                    Back.setVisibility(View.VISIBLE);

                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        emailreg.setError("Email already registered!");
                        emailreg.requestFocus();
                        return;
                    }

                    else {
                        error.setVisibility(View.VISIBLE);
                    }
                }




            }
        });




    }







}
