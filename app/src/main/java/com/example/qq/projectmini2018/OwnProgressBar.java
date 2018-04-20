package com.example.qq.projectmini2018;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Sarayut on 13/4/2561.
 */

public class OwnProgressBar extends View {

    Handler handler;

    public OwnProgressBar(Context context) {
        super(context);
    }

    public OwnProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        handler = new Handler();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
    int delay = 0, delay1 = 0, delay2 = 0, delay3 = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#79ff4d"));

        canvas.save();
        canvas.drawArc(100, 43, 150, 93, 0, 0 + delay, true, paint);
        canvas.restore();

        if (delay <= 360) {
            delay += 10;
        }
        if (delay > 360) {
            canvas.save();
            paint.setColor(Color.parseColor("#39e600"));
            canvas.drawArc(200, 43, 250, 93, 0, 0 + delay1, true, paint);
            canvas.restore();

            if (delay1 <= 360) {
                delay1 += 10;
            }
            if (delay1 > 360) {
                canvas.save();
                paint.setColor(Color.parseColor("#2db300"));
                canvas.drawArc(300, 43, 350, 93, 0, 0 + delay2, true, paint);
                canvas.restore();

                if (delay2 <= 360) {
                    delay2 += 10;
                }
                if (delay2 > 360) {
                    canvas.save();
                    paint.setColor(Color.parseColor("#208000"));
                    canvas.drawArc(400, 43, 450, 93, 0, 0 + delay3, true, paint);
                    canvas.restore();

                    if (delay3 <= 360) {
                        delay3 += 10;
                    }
                }
            }
        }
        handler.postDelayed(runnable, 5);
    }
}
