package com.example.expresseeliverycheck.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.adapter.GetSmsAdapter;
import com.example.expresseeliverycheck.model.SmsModel;
import com.example.expresseeliverycheck.until.ImageUtil;
import com.example.expresseeliverycheck.until.SmsReadUntil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GetSmsListView extends BaseListView<SmsModel> {
    private List<SmsModel> list = new ArrayList();
    private final String ExpressName = "韵达，天天快递，佳吉快递，百世物流，联邦快递，德邦物流，华强物流，中铁物流，百世汇通，中铁快运，E速宝，信丰物流，顺丰速运，申通快递，龙邦速递，天地华宇，快捷快递，邮政国内小包，黑猫宅急便，新邦物流，卡行天下，安能物流，贝海国际速递，佳吉快运，能达速递，优速快递 ，增益速递，国通快递，邮政小包，圆通速递，圆通快递，中通快递，EMS经济快递，EMS，德邦快递，凡宇速递，联昊通，全峰快递，全一快递，城市100，广东EMS，速尔，燕文上海，燕文深圳，燕文义乌，飞远(爱彼西)配送，宅急送，中国邮政，微快递，菜鸟驿站";
    private int flag;
    private ImageView imageView;

    public GetSmsListView(RecyclerView recyclerView, RefreshLayout refreshLayout, Context context, int flag, ImageView imageView) {
        super(recyclerView, refreshLayout, context);
        this.flag = flag;
        this.imageView = imageView;
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ImageUtil.dp2px(mContext, 4)));
    }

    @Override
    public void setAdapter() {
        getData();
        adapter = new GetSmsAdapter(mContext);
        adapter.setDataList(list);
    }


    @Override
    public void onRefreshing() {
        list = new ArrayList<>();
        getData();
        onUpdate(list);
    }

    @Override
    public void onLoadingMore() {

    }

    @Override
    public void onListItemClick(View view, int position) {
    }

    @Override
    public void onListItemLongClick(View view, int position) {

    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = space;
            }
        }
    }

    //获取数据
    private void getData() {
        SmsReadUntil smsReadUntil = new SmsReadUntil(1, getActivity());
        List<HashMap<String, String>> smsHashMap = smsReadUntil.getSmsInPhone();
        dataProcess(smsHashMap);
    }

    // 数据处理
    private void dataProcess(List<HashMap<String, String>> dataList) {
        list = new ArrayList();
        List<SmsModel> list1 = new ArrayList();
        String[] strings = ExpressName.split("，");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String nowDate = dateFormat.format(new Date(System.currentTimeMillis()));
        for (int i = 0; i < dataList.size(); i++) {
            SmsModel smsModel = new SmsModel();
            String smsDate = dataList.get(i).get("strDate").toString();
            String smsBody = dataList.get(i).get("strbody").toString();
            String index = dataList.get(i).get("index").toString();
            String str1 = "";

            if (flag == 0 && nowDate.equals(smsDate)) {
                if (smsBody.indexOf("】") != -1) {
                    str1 = smsBody.substring(smsBody.indexOf("】") + 1, smsBody.length());//截取】之后的字符串-
                }
                for (String s : strings) {
                    if (smsBody.indexOf(s) != -1) {
                        if (("韵达").equals(s)) {
                            s = "韵达快递";
                        }
                        smsModel.setSmsTitle(s);
                        smsModel.setSmsDate(smsDate);
                        smsModel.setBody(str1);
                        smsModel.setIndex(index);
                        //去重

                        if(list1.size()!=0&&i>0){
                            if (!list1.get(list1.size()-1).getIndex().equals(index)){
                                list1.add(smsModel);
                            }
                        } else if (list1.size()==0){
                            list1.add(smsModel);
                        }
                    }
                }
            } else if (flag == 1 && !nowDate.equals(smsDate)) {
                if (smsBody.indexOf("】") != -1) {
                    str1 = smsBody.substring(smsBody.indexOf("】") + 1, smsBody.length());//截取】之后的字符串-
                }
                for (String s : strings) {
                    if (smsBody.indexOf(s) != -1) {
                        if (("韵达").equals(s)) {
                            s = "韵达快递";
                        }
                        //如果短信内容包含 expressName 赋值
                        smsModel.setSmsTitle(s);
                        smsModel.setSmsDate(smsDate);
                        smsModel.setBody(str1);
                        smsModel.setIndex(index);
                        if(list1.size()!=0&&i>0){
                            if (!list1.get(list1.size()-1).getIndex().equals(index)){
                                list1.add(smsModel);
                            }
                        } else if (list1.size()==0){
                            list1.add(smsModel);
                        }
                    }
                }
            }
        }
        list.addAll(list1);
        if (imageView!=null){
            imageView.setVisibility(View.GONE);
            if (list.size()==0){
                imageView.setVisibility(View.VISIBLE);
            }
        }

    }
}
