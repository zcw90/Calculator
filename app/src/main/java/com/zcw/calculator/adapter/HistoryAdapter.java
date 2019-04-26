package com.zcw.calculator.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zcw.calculator.R;
import com.zcw.calculator.bean.History;

import java.util.List;

/**
 * 历史记录适配器
 */
public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private List<History> datas;

    public HistoryAdapter(Context context, List<History> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if(convertView == null) {
            view = View.inflate(context, R.layout.layout_history_item, null);

            holder = new ViewHolder();
            holder.expression = view.findViewById(R.id.tv_history_expression);
            holder.result = view.findViewById(R.id.tv_history_result);
            holder.time = view.findViewById(R.id.tv_history_time);

            view.setTag(holder);
        }
        else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        holder.expression.setText(datas.get(position).getExpression());
        holder.result.setText(datas.get(position).getResult());
        holder.time.setText(datas.get(position).getDate());
        return view;
    }

    private static class ViewHolder {
        public TextView expression;
        public TextView result;
        public TextView time;
    }
}
