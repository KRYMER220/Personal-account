package com.example.rjdapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rjdapp.Model.Appeals;
import com.example.rjdapp.ViewHolder.AppealsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AppealsActivity extends AppCompatActivity {
    private TextView closeBtn;
    private FirebaseAuth mAuth;
    String saveCurDate, saveCurTime, randomKey;
    private DatabaseReference databaseReference, dbRef;
    String onlineDriverUserID;
    private RecyclerView recyclerViewAppeals;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appeals_layout);

        init();
        recyclerViewAppeals = findViewById(R.id.recycler_appeals);
        recyclerViewAppeals.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        recyclerViewAppeals.setLayoutManager(layoutManager);


    }

    public void init() {

        closeBtn = (TextView) findViewById(R.id.close_MyAppeals);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Appeals");
        dbRef = FirebaseDatabase.getInstance().getReference().child("Appeals").child(mAuth.getCurrentUser().getUid());






        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAppeals = new Intent(AppealsActivity.this, MyMenuActivity.class);
                startActivity(intentAppeals);
            }
        });
    }


    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<Appeals> options = new FirebaseRecyclerOptions.Builder<Appeals>()
                .setQuery(dbRef,Appeals.class).build();

        FirebaseRecyclerAdapter<Appeals, AppealsViewHolder> adapter = new FirebaseRecyclerAdapter<Appeals, AppealsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AppealsViewHolder holder, int i, @NonNull Appeals model) {
                holder.idAppeals.setText(model.getId());
                holder.titleAppeals.setText(model.getTitleAppeals());
                holder.textAppeals.setText(model.getTextAppeals());

            }

            @NonNull
            @Override
            public AppealsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appeals_item, parent, false);
                AppealsViewHolder holder = new AppealsViewHolder(view);
                return holder;
            }
        };

        recyclerViewAppeals.setAdapter(adapter);
        adapter.startListening();


    }

    public void onClickAddAppeals(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AppealsActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.appeals_add_layout, null);
        EditText titleAppeals = dialogView.findViewById(R.id.titleAppeals);
        EditText textAppeals = dialogView.findViewById(R.id.textAppeals);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.btnAddAppeals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
                SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
                saveCurDate = currentDate.format(calendar.getTime());
                saveCurTime = currentTime.format(calendar.getTime());

                randomKey = saveCurDate + saveCurTime;
                String Appeals = titleAppeals.getText().toString();
                if (TextUtils.isEmpty(Appeals) ) {
                    Toast.makeText(AppealsActivity.this, "Введите тему обращения", Toast.LENGTH_SHORT).show();
                } else {
                } if(TextUtils.isEmpty(Appeals)) {
                    Toast.makeText(AppealsActivity.this, "Введите текст обращения", Toast.LENGTH_SHORT).show();
                } else {
                    onlineDriverUserID = mAuth.getCurrentUser().getUid();


                    HashMap userMap = new HashMap();

                    userMap.put("titleAppeals",titleAppeals.getText().toString());
                    userMap.put("textAppeals",textAppeals.getText().toString());
                    userMap.put("id",randomKey);



                    databaseReference.child(onlineDriverUserID).child(randomKey).updateChildren(userMap);
                    dialog.dismiss();
                }





            }
        });
        dialogView.findViewById(R.id.btnCancelAppeals).setOnClickListener(new View.OnClickListener() {
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
}