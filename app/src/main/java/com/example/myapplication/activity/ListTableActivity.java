package com.example.myapplication.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.myapplication.Goods;
import com.example.myapplication.R;
import com.example.myapplication.TableAdapter;

public class ListTableActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_table);

        //设置表格标题的背景颜色
        ViewGroup tableTitle = (ViewGroup) findViewById(R.id.table_title);
        tableTitle.setBackgroundColor(Color.rgb(177, 173, 172));

        List<Goods> list = new ArrayList<Goods>();
        list.add(new Goods("01", "伊利婴儿加盖奶粉110ml",12,12,12, 34, 23, 23));


        double heats_sum = list.stream().mapToDouble(Goods::getHeats).sum();
        double fat_sum = list.stream().mapToDouble(Goods::getFat).sum();
        double protein_sum = list.stream().mapToDouble(Goods::getProtein).sum();
        double Cabohydrates_sum = list.stream().mapToDouble(Goods::getCarbohydrates).sum();
        double Ca_sum = list.stream().mapToDouble(Goods::getCa).sum();
        double Fe_sum = list.stream().mapToDouble(Goods::getFe).sum();

        list.add(new Goods("all","总计", (float) heats_sum, (float)fat_sum, (float)protein_sum,
                (float)Cabohydrates_sum, (float)Ca_sum, (float)Fe_sum));

        ListView tableListView = (ListView) findViewById(R.id.list);

        TableAdapter adapter = new TableAdapter(this, list);

        tableListView.setAdapter(adapter);
    }

}

