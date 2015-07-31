package com.pointr.pointr.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pointr.pointr.R;

public class DoubleText extends LinearLayout {
    private TextView txtName;
    private ImageView imageView;

    private float lastDeg;

    public DoubleText(Context context) {
        super(context);
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        layoutInflater.inflate(R.layout.double_text_view, this, true);

        this.txtName = (TextView) findViewById(R.id.txtNum);
        this.imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setNameText(String str) {
        this.txtName.setText(str);
    }


    public void rotateImage(float deg) {
        final RotateAnimation rotateAnim = new RotateAnimation(this.lastDeg, deg,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        lastDeg = deg;
        rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        imageView.startAnimation(rotateAnim);
    }
}
