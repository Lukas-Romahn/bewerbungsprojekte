package de.pbma.moa.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PukView extends View {

    private Puk puk;
    private Paint farbe;

    public PukView(Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);
        puk= new Puk(100,100,50);
        farbe.setStyle(Paint.Style.FILL);
        farbe.setColor(Color.RED);



    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if(puk!=null){

            canvas.drawCircle(puk.getX(),puk.getY(),puk.getRadius(),farbe);

        }

    }

    public void update(){
        if(puk!=null){
            puk.move();
            invalidate();
        }
    }
}