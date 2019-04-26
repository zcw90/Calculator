package com.zcw.calculator.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zcw.calculator.R;

/**
 * 自定义按钮，按钮中包含文字和图片
 * Created by zcw on 2016/7/5.
 */
public class CustomButton extends LinearLayout {
    private TextView textView;
    private ImageView imageView;

    private RelativeLayout layout;

    public CustomButton(Context context) {
        this(context, null);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.custom_button, this, true);

        this.textView = findViewById(R.id.textView);
        this.imageView = findViewById(R.id.imageView);
        this.layout = findViewById(R.id.relativeLayout);
    }

    public TextView getTextView() {
        return textView;
    }

    /**
     * 设置按钮显示的文字
     * @param text
     */
    public void setText(String text) {
        textView.setText(text);
    }

    /**
     * 设置按钮图片
     * @param resourceID
     */
    public void setImageResource(int resourceID) {
        imageView.setImageResource(resourceID);
    }

    /**
     * 隐藏按钮中的图片
     */
    public void hideImage() {
        textView.setGravity(Gravity.CENTER);
        layout.setVisibility(GONE);
    }

    /**
     * 显示按钮中的图片
     */
    public void showImage() {
        textView.setGravity(Gravity.RIGHT | Gravity.CENTER);
        layout.setVisibility(VISIBLE);
    }
}
