package com.example.googlemapsandplaces.glogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemapsandplaces.MainActivity;
import com.example.googlemapsandplaces.R;
import com.example.googlemapsandplaces.databinding.ActivityGpayBinding;
import com.example.googlemapsandplaces.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = ".LoginActivity";

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleBtn;
    Button loginButton;

    ActivityLoginBinding binding; // Use the correct binding class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use the correct binding class for your activity
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String msg = "onActivityResult(): Successfully signed in. You can do further processing if needed.";
                Log.d(TAG, msg);
                navigateToSecondActivity();
            } catch (ApiException e) {
                // Display the actual error message
                String errMsg = "onActivityResult():Google sign-in failed: \n\t" + e.getMessage();
                Log.d(TAG, errMsg);
                Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }


    void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
