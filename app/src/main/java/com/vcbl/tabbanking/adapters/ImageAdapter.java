package com.vcbl.tabbanking.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vcbl.tabbanking.R;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private final String[] name;
    private final int[] image;

    public ImageAdapter(Context context, String[] name, int[] image) {
        this.context = context;
        this.name = name;
        this.image = image;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = new View(context);
            assert inflater != null;
            grid = inflater.inflate(R.layout.grid_item_layout, null);
            CardView cardView = grid.findViewById(R.id.cardView);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_enter);
            //cardView.setAnimation(animation);
            TextView textView = grid.findViewById(R.id.grid_text);
            ImageView imageView = grid.findViewById(R.id.grid_image);
            textView.setText(name[position]);
            imageView.setImageResource(image[position]);
        } else {
            grid = convertView;
        }
        return grid;
    }
}