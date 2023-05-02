package com.example.rjdapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {



    private EditText etLoginEmail, etPassword;
    private TextView AppName;
    SharedPreferences sPref2;
    TextView forgotPass;
    public DatabaseReference userRef,databaseReference;
    String onlineDriverUserID;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        sPref2 = getSharedPreferences("Data", MODE_PRIVATE);
        init();
        init1();

        //init2();



    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();
        if (cUser != null) {

        } else {

        }
    }
    private void init() {
        db = FirebaseDatabase.getInstance();
        AppName = findViewById(R.id.tvNameAPP);
        etLoginEmail = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPass);
        mAuth = FirebaseAuth.getInstance();
        forgotPass =  findViewById(R.id.btnRestorePass);



        if (sPref2.contains("save_LoginKey")) {
            etLoginEmail.setText(sPref2.getString("save_LoginKey", ""));
        }
        if (sPref2.contains("save_PassKey")) {
            etPassword.setText(sPref2.getString("save_PassKey", ""));
        }



    }

    public void init1() {
        Intent intentExitAccount = getIntent();
        Integer name = intentExitAccount.getIntExtra("test", 0);

        if (name == 1) {
            SharedPreferences.Editor editor = sPref2.edit();
            editor.remove("save_PassKey");
            editor.commit();
        }
    }

    public void initNameUID() {
        onlineDriverUserID = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Worker").child("Profile");
        DatabaseReference refName = db.getReference().child("User").child("Worker").child("Profile").child(onlineDriverUserID).child("ProfileData");
        refName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                databaseReference.child(onlineDriverUserID).setValue(name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    public void onClickSignIn(View view) {

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        if(!TextUtils.isEmpty(etLoginEmail.getText().toString()) && !TextUtils.isEmpty(etPassword.getText().toString())) {
            mAuth.signInWithEmailAndPassword(etLoginEmail.getText().toString(),etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {



                    if(task.isSuccessful()) {




                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            if (user.isEmailVerified()) {


                            manageConnections();


                            SharedPreferences.Editor edit1 = sPref2.edit();
                            edit1.putString("save_LoginKey",etLoginEmail.getText().toString());
                            edit1.putString("save_PassKey",etPassword.getText().toString());
                            edit1.apply();
                            Toast.makeText(getApplicationContext()
                                    , "Пользователь успешно авторизирован", Toast.LENGTH_SHORT).show();

                            startActivity(intent);
                            }  else {
                                sendEmailVer();
                                Toast.makeText(LoginActivity.this, "Ошибка входа, подтвердите вашу почту", Toast.LENGTH_SHORT).show();
                            }





                    } else {
                        Toast.makeText(getApplicationContext()
                                , "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Введите все поля и проверьте правильность данных", Toast.LENGTH_SHORT).show();
        }

    }

    public void manageConnections() {
        onlineDriverUserID = mAuth.getCurrentUser().getUid();
        DatabaseReference refName = db.getReference().child("User").child("Worker").child("Profile").child(onlineDriverUserID).child("ProfileData");
        DatabaseReference conRef1 = db.getReference().child("User").child("Worker").child("Connections");
        DatabaseReference conRef2 = db.getReference().child("User").child("Worker").child("Profile").child(onlineDriverUserID).child("Connections");
        DatabaseReference lastCon = db.getReference().child("User").child("Worker").child("Profile").child(onlineDriverUserID).child("Connections");
        DatabaseReference infoCon = db.getReference(".info/connected");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd-EEEE-yyyy");
        String date = sdf.format(Calendar.getInstance().getTime());

        refName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                conRef1.child(onlineDriverUserID).setValue(name);
                conRef1.child(onlineDriverUserID).onDisconnect().setValue(name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        infoCon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);

                if (connected) {
                    DatabaseReference con1 = conRef1.child(onlineDriverUserID);
                    DatabaseReference con2 = conRef2;
                    con1.child("Status").setValue("Online");
                    con1.child("Status").onDisconnect().setValue("Offline");
                    con2.child("Status").setValue("Online");
                    con2.child("Status").onDisconnect().setValue("Offline");
                    lastCon.child("lastConnection").onDisconnect().setValue(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void init2() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        if(!TextUtils.isEmpty(etLoginEmail.getText().toString()) && !TextUtils.isEmpty(etPassword.getText().toString())) {
            mAuth.signInWithEmailAndPassword(etLoginEmail.getText().toString(),etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    if (user.isEmailVerified()) {
                    if(task.isSuccessful()) {

                        SharedPreferences.Editor edit1 = sPref2.edit();

                        edit1.putString("save_LoginKey",etLoginEmail.getText().toString());
                        edit1.putString("save_PassKey",etPassword.getText().toString());
                        edit1.apply();
                        Toast.makeText(getApplicationContext()
                                , "Пользователь успешно авторизирован", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }  else {
                        sendEmailVer();
                        Toast.makeText(LoginActivity.this, "Ошибка входа, подтвердите вашу почту", Toast.LENGTH_SHORT).show();
                    }





                    } else {
                        Toast.makeText(getApplicationContext()
                                , "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public void onClickSignUp(View view) {

            if (!TextUtils.isEmpty(etLoginEmail.getText().toString()) && !TextUtils.isEmpty(etPassword.getText().toString())) {
                mAuth.createUserWithEmailAndPassword(etLoginEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            onlineDriverUserID = mAuth.getCurrentUser().getUid();
                            userRef = FirebaseDatabase.getInstance().getReference().child("User").child("Drivers").child(onlineDriverUserID);
                            userRef.child("Email").setValue(etLoginEmail.getText().toString());
                            userRef.child("Pass").setValue(etPassword.getText().toString());
                            Toast.makeText(getApplicationContext()
                                    , "Пользователь успешно зарегистрирован", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(getApplicationContext()
                                    , "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Введите все поля и проверьте правильность данных", Toast.LENGTH_SHORT).show();
            }


    }



    public void onClickRestorePass(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.forgoat_pass, null);
        EditText emailBox = dialogView.findViewById(R.id.emailBox);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = emailBox.getText().toString();
                if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "Введите свою почту на которую зарегистрирован аккаунт", Toast.LENGTH_SHORT).show();
                }
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Проверьте свою почту", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else  {
                            Toast.makeText(LoginActivity.this, "Не удаётся отправить письмо на вашу почту, обратитесь в центр поддержки кадров", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        if (dialog.getWindow() != null ) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }







    private void sendEmailVer() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Проверьте вашу почту для подтверждения входа", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка отправки письма подтверждения пользователя", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void syncUser() {


        String email = etLoginEmail.getText().toString();
        String pass = etPassword.getText().toString();
        String key = userRef.push().getKey();
        userRef.child(key).child("E-mail").setValue(email);
        userRef.child(key).child("Pass").setValue(pass);

    }



}