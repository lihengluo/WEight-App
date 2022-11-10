package fragment;

import android.content.Context;
import android.widget.TextView;

import com.example.myapplication.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * @Author : qiangyu
 * @Date : on 2022-11-04 16:59.
 * @Description :描述
 */
public class XYMarkerView extends MarkerView {

    private TextView tvContent;
    public XYMarkerView(Context context) {
        super(context, R.layout.custom_marker_view);
        initView();
    }
    private void initView() {
        tvContent = findViewById(R.id.tvContent);
    }
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //更新显示数据，由于是柱状图，所以可以强制转换为BarEntry
        BarEntry barEntry = (BarEntry) e;
        //获取Y值列表
        float[] values = barEntry.getYVals();

        tvContent.setText(values[0]+"");
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {
        //设置MarkerView的偏移量，就是提示框显示的位置
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}
