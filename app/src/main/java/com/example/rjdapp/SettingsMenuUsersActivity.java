package com.example.rjdapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsMenuUsersActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameET, userPhoneET, specUserET, railwayUserET, structureUserET;
    private TextView saveBtn, closeBtn;
    private String checker = "";
    private FirebaseDatabase db;
    private Uri imageUri;
    String myUrl = "";
    private FirebaseAuth mAuth;
    private StorageReference storageProfilePictureRef;
    private StorageTask uploadTask;
    private DatabaseReference databaseReference;
    String onlineDriverUserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settins_menu_users);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Worker").child("Profile");
        init();

        userInfoDisplay();
    }
    public void init() {

        mAuth = FirebaseAuth.getInstance();
        profileImageView = (CircleImageView) findViewById(R.id.settings_account_image);
        fullNameET = (EditText) findViewById(R.id.settings_fullnameUser);
        userPhoneET = (EditText) findViewById(R.id.settings_phoneUser);
        specUserET = (EditText) findViewById(R.id.settings_specUser);
        railwayUserET = (EditText) findViewById(R.id.settings_railwayUser);
        structureUserET = (EditText) findViewById(R.id.settings_railwaystructureUser);
        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        closeBtn = (TextView) findViewById(R.id.close_settings_tw);
        saveBtn = (TextView) findViewById(R.id.save_settings_tw);



        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentHomeActivity = new Intent(SettingsMenuUsersActivity.this, MainActivity.class);
                startActivity(intentHomeActivity);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checker.equals("clicked")) {
                   userInfoSave();
                } else {
                    updateOnlyUserInfo();
                }
            }


        });

        profileImageView.setOnClickListener(view -> {
            checker = "clicked";
            CropImage.activity().setAspectRatio(1,1).start(SettingsMenuUsersActivity.this);
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data!= null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);


        } else {


            startActivity(new Intent(SettingsMenuUsersActivity.this, MainActivity.class));

        }
    }

    private void userInfoSave() {
        if (TextUtils.isEmpty(fullNameET.getText().toString())) {
            Toast.makeText(this, "Заполните имя", Toast.LENGTH_SHORT).show();
        }else {
        }if (TextUtils.isEmpty(userPhoneET.getText().toString())) {
            Toast.makeText(this, "Заполните номер телефона", Toast.LENGTH_SHORT).show();
        } else {
        }if (TextUtils.isEmpty(specUserET.getText().toString())) {
            Toast.makeText(this, "Заполните специальность", Toast.LENGTH_SHORT).show();
        } else {
        }if (TextUtils.isEmpty(railwayUserET.getText().toString())) {
            Toast.makeText(this, "Заполните ж/д направление", Toast.LENGTH_SHORT).show();
        } else {
        }if (TextUtils.isEmpty(structureUserET.getText().toString())) {
            Toast.makeText(this, "Заполните структурное подразделение", Toast.LENGTH_SHORT).show();
        } else {
            if (checker.equals("clicked")) {
                uploadImage();

            }
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Обновление данных...");
        progressDialog.setMessage("Пожалуйста, подождите");
        progressDialog.show();

        if (imageUri !=null) {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(mAuth.getCurrentUser().getUid()+".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();

                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();



                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("uid",mAuth.getCurrentUser().getUid());
                        userMap.put("name",fullNameET.getText().toString());
                        userMap.put("phone",userPhoneET.getText().toString());
                        userMap.put("spec",specUserET.getText().toString());
                        userMap.put("way",railwayUserET.getText().toString());
                        userMap.put("structure",structureUserET.getText().toString());
                        userMap.put("image", myUrl);

                        databaseReference.child(mAuth.getCurrentUser().getUid()).child("ProfileData").updateChildren(userMap);

                        startActivity(new Intent(SettingsMenuUsersActivity.this,MainActivity.class));
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsMenuUsersActivity.this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(SettingsMenuUsersActivity.this, "Изображение не выбрано", Toast.LENGTH_SHORT).show();
        }

    }




    private void updateOnlyUserInfo() {

        if (TextUtils.isEmpty(fullNameET.getText().toString())) {
            Toast.makeText(this, "Заполните имя", Toast.LENGTH_SHORT).show();
        }else {
        }if (TextUtils.isEmpty(userPhoneET.getText().toString())) {
            Toast.makeText(this, "Заполните номер телефона", Toast.LENGTH_SHORT).show();
        } else {
        }if (TextUtils.isEmpty(specUserET.getText().toString())) {
            Toast.makeText(this, "Заполните специальность", Toast.LENGTH_SHORT).show();
        } else {
        }if (TextUtils.isEmpty(railwayUserET.getText().toString())) {
            Toast.makeText(this, "Заполните ж/д направление", Toast.LENGTH_SHORT).show();
        } else {
        }if (TextUtils.isEmpty(structureUserET.getText().toString())) {
            Toast.makeText(this, "Заполните структурное подразделение", Toast.LENGTH_SHORT).show();
        } else {
            HashMap userMap = new HashMap();
            onlineDriverUserID = mAuth.getCurrentUser().getUid();
            userMap.put("name",fullNameET.getText().toString());
             userMap.put("phone",userPhoneET.getText().toString());
            userMap.put("spec",specUserET.getText().toString());
            userMap.put("way",railwayUserET.getText().toString());
            userMap.put("structure",structureUserET.getText().toString());



            databaseReference.child(onlineDriverUserID).child("ProfileData").updateChildren(userMap);

            startActivity(new Intent(SettingsMenuUsersActivity.this,MainActivity.class));


        }

    }
    private void userInfoDisplay() {
        databaseReference.child(mAuth.getCurrentUser().getUid()).child("ProfileData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>2) {
                    String name = snapshot.child("name").getValue().toString();
                    String phone = snapshot.child("phone").getValue().toString();
                    String spec = snapshot.child("spec").getValue().toString();
                    String railway = snapshot.child("way").getValue().toString();
                    String structure = snapshot.child("structure").getValue().toString();

                    fullNameET.setText(name);
                    userPhoneET.setText(phone);
                    specUserET.setText(spec);
                    railwayUserET.setText(railway);
                    structureUserET.setText(structure);

                    if (snapshot.hasChild("image")) {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}