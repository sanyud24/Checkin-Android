package com.checkin.app.checkin.Auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.checkin.app.checkin.Data.TestDb;
import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel.GENDER;
import com.checkin.app.checkin.Utility.Constants;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements SignUpFragmentInteraction {
    private String TAG = SignUpActivity.class.getSimpleName();
    FragmentManager fragmentManager;
    private SharedPreferences mPrefs;
    private boolean goBack = true;
    private static final int PHONE_LOGIN_REQUEST_CODE = 100;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider mPhoneAuth;
    private String mVerificationId;
    private AuthViewModel mAuthViewModel;

    @BindView(R.id.dark_back) View mDarkBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, SignupPhoneFragment.newInstance(this));
            transaction.commit();
        }

        mAuth = FirebaseAuth.getInstance();
        mPhoneAuth = PhoneAuthProvider.getInstance(mAuth);
        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                goBack = false;
                replaceFragmentContainer(SignupUserInfoFragment.newInstance(SignUpActivity.this));
                Toast.makeText(getApplicationContext(),"Verification Error",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                SignUpActivity.this.mVerificationId = verificationId;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId) {
                super.onCodeAutoRetrievalTimeOut(verificationId);
                mAuthViewModel.setOtpTimeout(0);
            }
        };

        mAuthViewModel = ViewModelProviders.of(this, new AuthViewModel.Factory(getApplication())).get(AuthViewModel.class);
    }

    private void verifyPhoneNumber() {
        mAuthViewModel.setOtpTimeout(Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT);
        mPhoneAuth.verifyPhoneNumber(
                mAuthViewModel.getPhoneNo(),
                Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT,
                TimeUnit.MILLISECONDS,
                this,
                mCallBack
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "Phone verified!");
                        goBack = false;
                        replaceFragmentContainer(SignupUserInfoFragment.newInstance(SignUpActivity.this));

                    } else {
                        // Sign in failed, display a message and update the UI
                        Toast.makeText(getApplicationContext(),"Error in signing up!", Toast.LENGTH_LONG).show();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(),"Invalid Verification Code", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void replaceFragmentContainer(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


   @Override
    public void onPhoneNumberProcess(String phoneNo) {
        Log.e(TAG, "Phone number: " + phoneNo);
        mAuthViewModel.setPhoneNo(phoneNo);
        verifyPhoneNumber();
        OtpVerificationFragment otpVerificationFragment = OtpVerificationFragment.newInstance(this);
        Fade fade = new Fade();
        fade.setDuration(5);
        otpVerificationFragment.setEnterTransition(fade);
        replaceFragmentContainer(otpVerificationFragment);
        mDarkBack.animate()
                .alpha(0.67f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDarkBack.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onResendOtpRequest() {
        verifyPhoneNumber();
    }

    @Override
    public void onOtpVerificationProcess(String otp) {
        Log.e(TAG, "OTP: " + otp);
        PhoneAuthCredential credential =PhoneAuthProvider.getCredential(mVerificationId,otp);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public void onUserInfoProcess(String firstName, String lastName, String password, GENDER gender) {
        Log.e(TAG, "First name: " + firstName + " | Last name: " + lastName + " | Password: " + password + " | Gender: " + gender.name());
        // TODO: Test data population
        TestDb.populateWithTestData(getApplicationContext());

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(Constants.SP_USER_ID, "1");
        editor.putBoolean(Constants.SP_LOGGED_IN, true);
        editor.apply();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onSignInClicked() {
        startActivityForResult(new Intent(this, LoginActivity.class), PHONE_LOGIN_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        if (goBack) {
            mDarkBack.animate()
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mDarkBack.setVisibility(View.GONE);
                        }
                    });
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHONE_LOGIN_REQUEST_CODE:
                if (resultCode == RESULT_OK && data.getBooleanExtra(Constants.SP_LOGGED_IN, false)) {
                    finish();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
