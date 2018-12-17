package ru.proletarsky.pzstock;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by alexey on 08/12/2018.
 */

public class DataAdapter extends RecyclerView.Adapter {

    private List<DataMember> dataList;

    public DataAdapter() {
        super();
        dataList = new ArrayList<DataMember>();
    }

    public DataAdapter(List<DataMember> data) {
        super();
        dataList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DataViewHolder)holder).bindData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.data_item;
    }

    public List<DataMember> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataMember> list) {
        dataList = list;
        notifyDataSetChanged();
    }

    public void removeAt(int pos) {
        if (dataList.size() > pos) {
            dataList.remove(pos);
        }
    }
}
