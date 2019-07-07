package com.example.guoca.can_app.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Guoca on 2019/7/6.
 */

public class GuideAdapter extends PagerAdapter {
    Context context;
    List<View> listView;
    public GuideAdapter(Context context,List<View> listView){
        this.context=context;
        this.listView=listView;
    }

    @Override
    public int getCount() {
        return listView.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view=listView.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view=(View)object;
        container.removeView(view);
    }
}