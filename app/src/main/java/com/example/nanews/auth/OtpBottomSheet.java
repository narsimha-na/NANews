package com.example.nanews.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nanews.MainActivity;
import com.example.nanews.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OtpView;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class OtpBottomSheet extends BottomSheetDialogFragment {

    private LinearLayout obPhoneLayout,obOtpLayout;
    private OtpView obOtpEdit;
    private EditText obPhoneEdit;
    private String obPhoneNumber,obOtp,obVerificationId;
    private FirebaseAuth obAuth;
    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.otp_bottom_sheet, container, false);

        obPhoneLayout = (LinearLayout)v.findViewById(R.id.otp_layout_phone);
        obOtpLayout = (LinearLayout)v.findViewById(R.id.otp_layout_otp);

        obPhoneEdit = (EditText)v.findViewById(R.id.otp_phone_number);
        obOtpEdit = (OtpView)v.findViewById(R.id.otp_view);

        obAuth = FirebaseAuth.getInstance();

        v.findViewById(R.id.otp_button_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obPhoneNumber = obPhoneEdit.getText().toString();
                if(obPhoneNumber.length() < 10 || obPhoneNumber.isEmpty()){
                    Toast.makeText(getContext(), "Please Enter a 10 digit phone number", Toast.LENGTH_SHORT).show();                    return;
                }else {
                   sendVerificationCode(obPhoneNumber);
                    obPhoneLayout.setVisibility(View.GONE);
                    obOtpLayout.setVisibility(View.VISIBLE);
                }


            }
        });

        v.findViewById(R.id.otp_button_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Hi", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private void sendVerificationCode(String obPhoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+obPhoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();


            //In Sometimes  the code may not be detected so the code may be null
            if(code !=  null){
                obOtpEdit.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //Storing the verification id  that is sent to user
            obVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {

        //Creating the credential
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(obVerificationId,code);

        //Signing the user
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        obAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Welcome !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }else{
                            String message = "Something is wrong, we will fix it soon...";
                            Toast.makeText(getContext(), "Something went wrong ! ", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                                Toast.makeText(getContext(), "Entered code is not valid", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}