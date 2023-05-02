package com.example.rjdapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MyMenuActivity extends AppCompatActivity {
    final String[] listMyName1 = new String[]{"Мои обращения","Замечания","Ответы на замечания","Контрольные сроки","лицевой счет","Нарушения","Приказы","Мой рейтинг"};

    private TextView closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymenu_layout);
        init();
        initList();




    }

    public void init() {
        closeBtn = (TextView) findViewById(R.id.close_MyMenu);




        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMyMenu = new Intent(MyMenuActivity.this, MainActivity.class);
                startActivity(intentMyMenu);
            }
        });
    }

    public void initList() {
        ListView listView1 = (ListView) findViewById(R.id.listMy1);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listMyName1);
        listView1.setAdapter(adapter1);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intentAppeals = new Intent(MyMenuActivity.this, AppealsActivity.class);
                    startActivity(intentAppeals);

                }
            }
        });

    }

}
