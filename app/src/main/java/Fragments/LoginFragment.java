package Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.locationsharing.LoginEmailActivity;
import com.example.locationsharing.R;
import com.example.locationsharing.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {

    private View mView;
    private FirebaseAuth mAuth;

    private Button btn_login;
    private Button btn_signup;

    public LoginFragment() {

    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();

        //Bind Views
        initializeBindViews();
        //Login Button Function
        btnLoginFunction();
        //Signup Button Function
        btnSignupFunction();
        return mView;
    }
    private void initializeBindViews(){
         btn_login = mView.findViewById(R.id.btn_login);
         btn_signup = mView.findViewById(R.id.btn_signup);
    }
    private void btnLoginFunction(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLoginEmailActivity();
            }
        });
    }
    private void btnSignupFunction(){
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSignUpActivity();
            }
        });
    }
    private void loadLoginEmailActivity(){
        Intent mIntent = new Intent(getActivity(), LoginEmailActivity.class);
        startActivity(mIntent);
    }
    private void loadSignUpActivity(){
        Intent mIntent = new Intent(getActivity(), SignUpActivity.class);
        startActivity(mIntent);
    }
}