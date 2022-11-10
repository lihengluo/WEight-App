package fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import fragment.util.LineChartUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author : qiangyu
 * @Date : on 2022-11-04 15:52.
 * @Description :描述
 */
public class HistoryCountActivity extends AppCompatActivity {
    private LineChart chart1,chart2,chart3;
    private TextView sp_1;
    private int xLableCount = 7;
    private int xRangeMaximum = xLableCount - 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historycount);
        chart1 = findViewById(R.id.chart1);
        chart2 = findViewById(R.id.chart2);
        chart3 = findViewById(R.id.chart3);
        sp_1 = findViewById(R.id.sp_1);
        List<String> stringList = new ArrayList<>();
        for (int i=0;i<36;i++){
            stringList.add(i+"-"+(i+1));
        }
        List<Entry> lineList = new ArrayList<>();
        List<Entry> lineList2 = new ArrayList<>();
        List<Entry> lineList3 = new ArrayList<>();
        for (int i =0;i<36;i++){
            lineList.add(new BarEntry(Float.valueOf(i),new Random().nextInt(400)));
        }
        for (int i =0;i<36;i++){
            lineList2.add(new BarEntry(Float.valueOf(i),new Random().nextInt(400)));
        }
        for (int i =0;i<36;i++){
            lineList3.add(new BarEntry(Float.valueOf(i),new Random().nextInt(400)));
        }
        xLableCount = 10;
        xRangeMaximum = xLableCount - 1;
        LineChartUtils.setXAxis(chart1, xLableCount,36, xRangeMaximum);
        LineChartUtils.notifyDataSetChanged(chart1, lineList, stringList,R.drawable.gradient_1);
        SetHeightLimit(chart1,100f,"预警",Color.rgb(255,0,0));
        LineChartUtils.initChart(this,chart1, true, false, false);

        LineChartUtils.setXAxis(chart2, xLableCount,36, xRangeMaximum);
        LineChartUtils.notifyDataSetChanged(chart2, lineList2, stringList,R.drawable.gradient_2);
        SetHeightLimit(chart2,100f,"预警",Color.rgb(255,0,0));
        LineChartUtils.initChart(this,chart2, true, false, false);

        LineChartUtils.setXAxis(chart3, xLableCount,36, xRangeMaximum);
        LineChartUtils.notifyDataSetChanged(chart3, lineList3, stringList,R.drawable.gradient_3);
        SetHeightLimit(chart3,100f,"预警",Color.rgb(255,0,0));
        LineChartUtils.initChart(this,chart3, true, false, false);

        final ImageView back = (ImageView) this.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sp_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });
    }

    /**
     * 设置折线图的警告线
     */
    private void SetHeightLimit(LineChart lineChart,float height,String name,int color){
        //实例化警告线，并传输高度即警告先在图表中的位置,name 警告线叫什么名字
        LimitLine limitLine = new LimitLine(height,name);
        //设置警告先的宽度
        limitLine.setLineWidth(2f);
        //设置警告线上文本的颜色
        limitLine.setTextColor(color);
        //设置警告线的颜色
        limitLine.setLineColor(color);
        //设计警告线上文本的字体类型
        limitLine.setTypeface(Typeface.DEFAULT_BOLD);
        //设计警告线在x轴上的偏移量
        //应用警告线
        lineChart.getAxisLeft().addLimitLine(limitLine);
    }


    private void showDate() {
        new DialogDateSlot(this, "2022-10-06",
                "2022-11-06", new InteDateSlot() {
            @Override
            public void call(String dateSta, String dateEnd) {
                Toast.makeText(HistoryCountActivity.this,
                                dateSta+"-"+dateEnd,
                                Toast.LENGTH_LONG)
                        .show();
            }
        }).show();
    }


}
