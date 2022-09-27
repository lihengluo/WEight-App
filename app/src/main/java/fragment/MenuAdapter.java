package fragment;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.myapplication.R;
import fragment.bean.MenuBean;

import java.util.Calendar;

public class MenuAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> {
    public MenuAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MenuBean item) {
        if (item.isCheck()){
            helper.getView(R.id.lin).setBackgroundResource(R.drawable.radius1);
            ((TextView)helper.getView(R.id.tv_1)).setTextColor(mContext.getResources().getColor(R.color.white));
            ((TextView)helper.getView(R.id.tv_2)).setTextColor(mContext.getResources().getColor(R.color.white));
        }else {
            helper.getView(R.id.lin).setBackgroundResource(R.drawable.radius2);
            ((TextView)helper.getView(R.id.tv_1)).setTextColor(mContext.getResources().getColor(R.color.color1));
            ((TextView)helper.getView(R.id.tv_2)).setTextColor(mContext.getResources().getColor(R.color.color1));
        }
        helper.setText(R.id.tv_1,item.getWeek())
                .setText(R.id.tv_2,item.getDay());
    }
}
