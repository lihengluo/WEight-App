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
        if((carbohydrateNum / 360) > 1.0){
            carbon.setTextColor(Color.RED);}

        TextView protein = findViewById(R.id.t4);
        protein.setText(String.format("%.1f %%", proteinNum / 0.65));
        if((proteinNum / 65) > 1.0){
            protein.setTextColor(Color.RED);}

        TextView fat = findViewById(R.id.t6);
        fat.setText(String.format("%.1f %%", fatNum / 0.5));
        if((fatNum / 50) > 1.0){
            fat.setTextColor(Color.RED);}

        String text;
        TextView recomm = findViewById(R.id.t8);
        if(carbohydrateNum / 3.6 >= proteinNum / 0.65){
            if(proteinNum / 0.65 > fatNum / 0.5){
                text = "适量脂肪";
            }else{
                text = "蛋白质，例如肉、蛋、奶等食物";
            }
        }else {
            if (carbohydrateNum / 3.6 > fatNum / 0.5) {
                text = "适量脂肪";
            } else {
                text = "碳水化合物，例如谷薯类食物";
            }
        }
        recomm.setText("建议您今日优先补充"+text);

        final ImageView back = (ImageView) this.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
