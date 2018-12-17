package ru.proletarsky.pzstock;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by alexey on 08/12/2018.
 */

public class DataViewHolder extends RecyclerView.ViewHolder {
    private TextView mNomenView;
    private TextView mLeftPane;
    private TextView mRightPane;

    public DataViewHolder(final View itemView) {
        super(itemView);
        mNomenView = (TextView) itemView.findViewById(R.id.nomen_view);
        mLeftPane = (TextView) itemView.findViewById(R.id.left_pane);
        mRightPane = (TextView) itemView.findViewById(R.id.right_pane);
    }

    public void bindData(DataMember dataMember) {
        mNomenView.setText(dataMember.nomenclature);
        mLeftPane.setText(String.format("%s\n%s\n%s", dataMember.stock, dataMember.supplier,
                dataMember.orderInfo));
        mRightPane.setText(String.format("%s\n%s", dataMember.getAmount(), dataMember.getSum()));
    }
}
