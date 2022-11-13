package fragment;

import android.content.Intent;
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

        final Intent myIntend = getIntent();

        float carbohydrateNum = myIntend.getFloatExtra("carbohydrate", 0.0f);
        float proteinNum = myIntend.getFloatExtra("protein", 0.0f);
        float fatNum = myIntend.getFloatExtra("fat", 0.0f);

        p_1 = findViewById(R.id.p_1);
        p_2 = findViewById(R.id.p_2);
        p_3 = findViewById(R.id.p_3);
        // https://www.zhihu.com/question/289104021 标准数据来源
        p_1.setUsedColor(R.color.orange).setTotalColor(getResources().getColor(R.color.tan)).setPercent(carbohydrateNum / 360).update();
        p_2.setUsedColor(R.color.green).setTotalColor(getResources().getColor(R.color.greenyellow)).setPercent(proteinNum / 65).update();
        p_3.setUsedColor(R.color.royalblue).setTotalColor(getResources().getColor(R.color.lightblue)).setPercent(fatNum / 50).update();

        TextView carbon = findViewById(R.id.t2);
        carbon.setText(String.format("%.1f %%", (carbohydrateNum / 3.6)));

        TextView protein = findViewById(R.id.t4);
        protein.setText(String.format("%.1f %%", proteinNum / 0.65));

        TextView fat = findViewById(R.id.t6);
        fat.setText(String.format("%.1f %%", fatNum / 0.5));

        TextView recomm = findViewById(R.id.t8);
        recomm.setText("数据参考自《中国居民膳食营养素参考摄入量》");

        final ImageView back = (ImageView) this.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
