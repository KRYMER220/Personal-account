package com.example.rjdapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private CircleImageView headProfileImageView;
    private Integer AuthId = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    String onlineDriverUserID;
    private StorageReference storageProfilePictureRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.main_layout);
        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Worker").child("Profile");



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.nav_open, R.string.nav_exit);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmen_cont, new MainFragment()).commit();
            navigationView.setCheckedItem(R.id.mainMenu);
        }
      init();




    }

    public  void init() {
        mAuth = FirebaseAuth.getInstance();
        onlineDriverUserID = mAuth.getCurrentUser().getUid();
        manager = getSupportFragmentManager();




        headProfileImageView = findViewById(R.id.header_account_image);
        getHeadDriverInformation();


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainMenu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmen_cont, new MainFragment()).commit();

                break;
            case R.id.profile:
                Intent intentMyRating = new Intent(MainActivity.this, MyMenuActivity.class);
                startActivity(intentMyRating);
                break;


            case R.id.settingsProfile:
                Intent intentSettingsProfile = new Intent(MainActivity.this, SettingsMenuUsersActivity.class);
                startActivity(intentSettingsProfile);
                break;
            case R.id.exit:
                FirebaseAuth.getInstance().signOut();
                    Intent intentExitAccount = new Intent(MainActivity.this, LoginActivity.class);
                intentExitAccount.putExtra("test",1);
                    startActivity(intentExitAccount);
                    finish();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }






    private void getHeadDriverInformation() {

        NavigationView navView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navView.getHeaderView(0);
        TextView headNameTV = headerView.findViewById(R.id.textHeaderName);
        TextView specNameTv = headerView.findViewById(R.id.textSpec);
        TextView wayNameTV = headerView.findViewById(R.id.textWay);
        CircleImageView headImagePhoto = headerView.findViewById(R.id.header_account_image);

        databaseReference.child(mAuth.getCurrentUser().getUid()).child("ProfileData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>2) {
                    String name = snapshot.child("name").getValue().toString();
                    String spec = snapshot.child("spec").getValue().toString();
                    String way = snapshot.child("way").getValue().toString();
                    headNameTV.setText(name);
                   specNameTv.setText(spec);
                   wayNameTV.setText(way);

                    if (snapshot.hasChild("image")) {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(headImagePhoto);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void initStatusHomeFrag(View view) {
        TextView textView = (TextView) findViewById(R.id.status);



    }

}