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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    //Atributos
    private Button UpdateAccountSetting;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    //Outros
    private String currentUserID;

    private ProgressDialog loadingBar;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Starters

        startaID();

        //Ações

        UpdateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

    }

    //Métodos

    private void RetrieveUserInfo() {

        RootRef.child("users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")) {

                    //Recuperando dados
                    String UserName = dataSnapshot.child("name").getValue().toString();
                    String UserStatus = dataSnapshot.child("status").getValue().toString();
                    String UserImage = dataSnapshot.child("image").getValue().toString();

                    //Setando dados
                    userName.setText(UserName);
                    userStatus.setText(UserStatus);

                } else if (dataSnapshot.exists() && dataSnapshot.hasChild("name")) {

                    //Recuperando dados
                    String UserName = dataSnapshot.child("name").getValue().toString();
                    String UserStatus = dataSnapshot.child("status").getValue().toString();

                    //Setando dados
                    userName.setText(UserName);
                    userStatus.setText(UserStatus);

                } else {
                    Toast.makeText(SettingsActivity.this, "Por favor, atualize suas informações...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void UpdateSettings() {

        String setUserName= userName.getText().toString();
        String setStatus= userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "Por favor, digite seu nome de usuário...", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(setStatus)) {
            Toast.makeText(this, "Por favor, digite seu status...", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("Atualizando Dados");
            loadingBar.setMessage("Por favor, aguarde enquanto salvamos os seus dados");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            HashMap<String, String> profileMap = new HashMap<>();
                profileMap.put("uid", currentUserID);
                profileMap.put("name", setUserName);
                profileMap.put("status", setStatus);

            RootRef.child("users").child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        SendUserToMainActivity();
                        loadingBar.dismiss();
                        Toast.makeText(SettingsActivity.this, "Informações salvas", Toast.LENGTH_SHORT).show();
                    } else {
                        String exception = task.getException().toString();
                        loadingBar.dismiss();
                        Toast.makeText(SettingsActivity.this, exception, Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }

    //Sistema

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void startaID() {

        //Atributos
        UpdateAccountSetting = findViewById(R.id.update_settings_button);
        userName = findViewById(R.id.set_user_name);
        userStatus = findViewById(R.id.set_profile_status);
        userProfileImage = findViewById(R.id.set_profile_image);

        //Firebase
        RootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        currentUserID = mAuth.getCurrentUser().getUid();

        //Outros

        loadingBar = new ProgressDialog(this);

        RetrieveUserInfo();

    }
}
