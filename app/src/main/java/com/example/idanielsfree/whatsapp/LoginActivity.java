package com.example.idanielsfree.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //Atributos

    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink, ForgetPasswordLink, PhoneLoginButton;

    //Firebase

    private FirebaseAuth mAuth;

    //Outros

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Starters

        startID();

        //Ações

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

    }

    //Ações

    private void AllowUserToLogin() {

        loadingBar.setTitle("Fazendo Login");
        loadingBar.setMessage("Por favor, aguarde enquanto carregamos suas informações");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor, entre com seu e-mail", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, entre com sua senha", Toast.LENGTH_SHORT).show();
        } else {

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        SendUserToMainActivity();
                        loadingBar.dismiss();
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "E-Mail ou senha incorretos", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }

    //

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    //

    private void startID() {

        //Atribuições
        LoginButton = findViewById(R.id.login_sing_in);
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        NeedNewAccountLink = findViewById(R.id.login_create_accont);
        ForgetPasswordLink = findViewById(R.id.login_forget_password);
        PhoneLoginButton = findViewById(R.id.login_phone);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        //Outros
        loadingBar = new ProgressDialog(this);

    }

}
