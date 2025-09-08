package de.pbma.moa.airhockey;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

public class CustomButton extends AppCompatButton {
    public CustomButton(@NonNull Context context) {
        super(context);

    }
    public CustomButton(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_down));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_up));
        }
        return super.onTouchEvent(event);
    }


}
