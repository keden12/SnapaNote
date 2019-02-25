package com.example.snapanote.Fragments;

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

import com.example.snapanote.Controllers.Base;
import com.example.snapanote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends Fragment {

    private static final String TAG = "Login";

    private EditText Email, Password;
    private Button Login;
    private TextView Register, Error;
    private ProgressBar progress;
    private FirebaseAuth auth;
    private FirebaseUser current;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.login,container,false);
        Email = (EditText) view.findViewById(R.id.logemail);
        Password = (EditText) view.findViewById(R.id.logpassword);
        Login = (Button) view.findViewById(R.id.btnlogin);
        Register = (TextView) view.findViewById(R.id.registerbtn);
        progress = (ProgressBar) view.findViewById(R.id.logprogress);
        Error = (TextView) view.findViewById(R.id.logerror);

        //getting the FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        Register.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){

                //If the signup text view is clicked, go to register fragment
                ((Base)getActivity()).setViewPager(1);

            }


        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //In case the rare error will be visible, clear it after every button click
                Error.setVisibility(View.GONE);


                //get given email and passwords
                final String email = Email.getText().toString();
                final String password = Password.getText().toString();


                //error checking

                //if email is empty
                if(email.isEmpty())
                {
                    Email.setError("Email is empty");
                    Email.requestFocus();
                    return;
                }

                //if the email given is not valid
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Email.setError("Please enter a valid email");
                    Email.requestFocus();
                    return;

                }

                //if password is empty
                if(password.isEmpty())
                {
                    Password.setError("Password is empty");
                    Password.requestFocus();
                    return;
                }

                //if password is smaller than 6
                if(password.length()<6)
                {
                    Password.setError("Minimum password length is 6");
                    Password.requestFocus();
                    return;
                }


                //Hiding buttons, showing progressbar
                Login.setVisibility(View.INVISIBLE);
                Register.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);

                //attempt to sign in
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        //if logged in
                        if(task.isSuccessful())
                        {
                            //show the logged in fragment
                            ((Base)getActivity()).setViewPager(2);

                        }
                        //if login failed even after the error check
                        else
                        {

                            //setting buttons back to visible and hiding the progressbar
                            progress.setVisibility(View.INVISIBLE);
                            Login.setVisibility(View.VISIBLE);
                            Register.setVisibility(View.VISIBLE);

                            //if the credentials are incorrect
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Password.setError("Invalid Password");
                                Password.requestFocus();
                                return;
                            }
                            //if there is no such user
                            if(task.getException() instanceof FirebaseAuthInvalidUserException)
                            {
                                Email.setError("User does not exist!");
                                Email.requestFocus();
                                return;
                            }

                            //otherwise (Rare occasion)
                            else {
                                //Show the error textview
                                Error.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                });






            }
        });










        return view;
    }










}