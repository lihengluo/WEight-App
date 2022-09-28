package fragment;

import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.myapplication.R;
import fragment.bean.MainBean;
import fragment.util.ColorUtils;

public class AdapterMain extends BaseQuickAdapter<MainBean, BaseViewHolder> {
    public AdapterMain(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MainBean item) {
        Log.e("========",ColorUtils.getColor()+"");
        helper.getView(R.id.v_1).setBackgroundResource(ColorUtils.getColor());
        helper.setImageBitmap(R.id.iv_1, BitmapFactory.decodeFile(item.getImg()));
        helper.setText(R.id.tv_1,item.getT1())
                .setText(R.id.tv_2,item.getT2())
                .setText(R.id.tv_3,item.getT3())
                .setText(R.id.tv_4,item.getT4())
                .setText(R.id.tv_5,item.getT5())
                .setText(R.id.tv_6,item.getT4())
                .setText(R.id.tv_7,item.getT5());
    }
}
