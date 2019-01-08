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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    //Atributos

    private Button CreateAccountButton;
    private EditText UserEmail, UserPassword;
    private TextView AreadyHaveAccountLink;

    //Outros

    private ProgressDialog loadingBar;

    //Firebase

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Starters

        startaID();

        //Ações

        AreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    //Ações

    private void CreateNewAccount() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor, digite seu e-mail", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, digite sua senha", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("Criando uma nova conta");
            loadingBar.setMessage("Por favor, espere enquanto criamos uma nova conta para você");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Setando o usuário no DB
                        String currentUserID= mAuth.getCurrentUser().getUid();
                        RootRef.child("users").child(currentUserID).setValue("");

                        //
                        SendUserToMainActivity();
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Conta criada com sucesso", Toast.LENGTH_SHORT).show();
                    } else {
                        String exception = task.getException().toString();
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "exception", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    //Sistema

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void startaID() {

        //Atributos

        CreateAccountButton = findViewById(R.id.register_create);
        UserEmail = findViewById(R.id.register_email);
        UserPassword = findViewById(R.id.register_password);
        AreadyHaveAccountLink = findViewById(R.id.register_already_account);

        //Firebase

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        //Outros

        loadingBar = new ProgressDialog(this);

    }
}
