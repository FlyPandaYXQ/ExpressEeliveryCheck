package com.example.expresseeliverycheck.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.model.SmsModel;
import com.example.expresseeliverycheck.until.AlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author FlyPanda@若曦
 */
public class GetSmsAdapter extends BaseListAdapter<SmsModel> {
    private LayoutInflater mLayoutInflater;
    private AlertDialog alertDialog;

    public GetSmsAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public SmsModel getItem(int position) {
        return getDataList().get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_getsms, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((ViewHolder) holder).item_getsms_date.setText(getItem(position).getSmsDate());
        ((ViewHolder) holder).item_getsms_title.setText(getItem(position).getSmsTitle());
        ((ViewHolder) holder).item_getsms_body.setText(getItem(position).getBody());
        ((ViewHolder) holder).item_getsms_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new AlertDialog(mContext, getItem(position).getSmsTitle(), getItem(position).getBody(), "", "知道了", new alertDialogClick());
                alertDialog.show();
            }
        });
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_getsms_title)
        protected TextView item_getsms_title;
        @BindView(R.id.item_getsms_body)
        protected TextView item_getsms_body;
        @BindView(R.id.item_getsms_date)
        protected TextView item_getsms_date;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class alertDialogClick implements AlertDialog.OnDialogButtonClickListener {
        @Override
        public void onDialogButtonClick(boolean isPositive) {
            alertDialog.dismiss();
        }
    }
}
