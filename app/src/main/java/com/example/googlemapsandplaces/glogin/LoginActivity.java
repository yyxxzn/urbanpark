package com.example.googlemapsandplaces.glogin;

import static com.example.googlemapsandplaces.GeneralUtils.EMAIL;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemapsandplaces.MainActivity;
import com.example.googlemapsandplaces.R;
import com.example.googlemapsandplaces.UserDetails;
import com.example.googlemapsandplaces.databinding.ActivityGpayBinding;
import com.example.googlemapsandplaces.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = ".LoginActivity";

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleBtn;
    Button loginButton;

    ActivityLoginBinding binding;

    private UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use the correct binding class for your activity
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // This sets the thread policy. It's better to be at the root
        setThreadPolicy();

        googleBtn = binding.imageView2;

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount acct = task.getResult(ApiException.class);

                String surname = new String();

                if (acct != null) {
                    String email = acct.getEmail().toString().split("@")[0];
                    String name = acct.getGivenName().toString();
                    try {
                        surname = acct.getFamilyName().toString();
                    } catch (Exception e) {
                        // Handle the exception
                         surname = "null";
                    }


                    EMAIL = email;

                    Log.d(TAG, "email: "+email);
                    Log.d(TAG, "Name----: "+name);
                    Log.d(TAG, "Account-----: "+surname);

                    userDetails = new UserDetails(email, name, surname);

                    // on the below line we are getting database reference.
                    DatabaseReference uRef = FirebaseDatabase.getInstance().getReference("user");

                    uRef.child(email).setValue(userDetails);
                }

                String msg = "onActivityResult(): Successfully signed in. You can do further processing if needed.";
                Log.d(TAG, msg);
                navigateToMainActivity();
            } catch (ApiException e) {
                // Display the actual error message
                String errMsg = "onActivityResult():Google sign-in failed: \n\t" + e.getMessage();
                Log.d(TAG, errMsg);
                Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();
            }
        }
    }


    void navigateToMainActivity() {
        finish();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void setThreadPolicy(){
        if (android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
}
