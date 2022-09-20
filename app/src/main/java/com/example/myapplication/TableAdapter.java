
package com.example.myapplication;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.myapplication.R;


public class TableAdapter extends BaseAdapter {

    private List<Goods> list;
    private LayoutInflater inflater;

    public TableAdapter(Context context, List<Goods> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int ret = 0;
        if(list!=null){
            ret = list.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Goods goods = (Goods) this.getItem(position);

        ViewHolder viewHolder;

        if(convertView == null){

            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.activity_list_table, null);
            viewHolder.goodId = (TextView) convertView.findViewById(R.id.text_id);
            viewHolder.foodname = (TextView) convertView.findViewById(R.id.text_name);
            viewHolder.heats = (TextView) convertView.findViewById(R.id.text_heats);
            viewHolder.fat  = (TextView) convertView.findViewById(R.id.text_fat);
            viewHolder.protein = (TextView) convertView.findViewById(R.id.text_protein);
            viewHolder.Cabohydrates = (TextView) convertView.findViewById(R.id.text_Carbohydrates);
            viewHolder.Ca = (TextView) convertView.findViewById(R.id.text_Ca);
            viewHolder.Fe = (TextView) convertView.findViewById(R.id.text_Fe);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.goodId.setText(goods.getId());
        viewHolder.goodId.setTextSize(13);
        viewHolder.foodname.setText(goods.getFoodName());
        viewHolder.foodname.setTextSize(13);
        viewHolder.heats.setText(goods.getHeats()+"");
        viewHolder.heats.setTextSize(13);
        viewHolder.fat.setText(goods.getFat()+"");
        viewHolder.fat.setTextSize(13);
        viewHolder.protein.setText(goods.getProtein()+"");
        viewHolder.protein.setTextSize(13);
        viewHolder.Cabohydrates.setText(goods.getCarbohydrates()+"");
        viewHolder.Cabohydrates.setTextSize(13);
        viewHolder.Ca.setText(goods.getCa()+"");
        viewHolder.Ca.setTextSize(13);
        viewHolder.Fe.setText(goods.getFe()+"");
        viewHolder.Fe.setTextSize(13);

        return convertView;
    }

    public static class ViewHolder{
        public TextView goodId;
        public TextView foodname;
        public TextView heats;
        public TextView fat;
        public TextView protein;
        public TextView Cabohydrates;
        public TextView Ca;
        public TextView Fe;
    }

}