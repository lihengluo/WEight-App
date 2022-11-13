package fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

/**
 * @Author : qiangyu
 * @Date : on 2022-11-04 15:25.
 * @Description :描述
 */
public class DayCountActivity extends AppCompatActivity {
    private PercentRectangleView p_1,p_2,p_3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daycount);
        p_1 = findViewById(R.id.p_1);
        p_2 = findViewById(R.id.p_2);
        p_3 = findViewById(R.id.p_3);
        p_1.setUsedColor(R.color.orange).setTotalColor(getResources().getColor(R.color.tan)).setPercent(0.80f).update();
        p_2.setUsedColor(R.color.green).setTotalColor(getResources().getColor(R.color.greenyellow)).setPercent(0.40f).update();
        p_3.setUsedColor(R.color.royalblue).setTotalColor(getResources().getColor(R.color.lightblue)).setPercent(0.85f).update();

        TextView carbon = findViewById(R.id.t2);
        carbon.setText(String.format("%.1f %%", 80.0));

        TextView protein = findViewById(R.id.t4);
        protein.setText(String.format("%.1f %%", 40.0));

        TextView fat = findViewById(R.id.t6);
        fat.setText(String.format("%.1f %%", 85.0));

        TextView recomm = findViewById(R.id.t8);
        recomm.setText("根据《中国居民平衡膳食宝塔》，建议您今日补充更多的"+"蛋白质");

        final ImageView back = (ImageView) this.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
