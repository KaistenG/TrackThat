package com.example.trackthat.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.trackthat.model.Habit;

import java.util.ArrayList;
import java.util.List;

public class DayPreviewView extends View {

    private Paint paint;
    private List<Habit> activeHabits = new ArrayList<>();

    public DayPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setActiveHabits(List<Habit> habits) {
        this.activeHabits = habits;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        int stripeWidth = 12;

        for (Habit habit : activeHabits) {
            paint.setColor(habit.getColor());
            switch (habit.getVisualType()) {
                case "VERTICAL":
                    // wird dynamisch nebeneinander gezeichnet
                    int vIndex = activeHabits.indexOf(habit);
                    int vX = vIndex * (stripeWidth + 4) + 4;
                    canvas.drawRect(vX, 0, vX + stripeWidth, h, paint);
                    break;
                case "HORIZONTAL":
                    int hIndex = activeHabits.indexOf(habit);
                    int hY = hIndex * (stripeWidth + 4) + 4;
                    canvas.drawRect(0, hY, w, hY + stripeWidth, paint);
                    break;
                case "BORDER":
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(6);
                    canvas.drawRect(3, 3, w - 3, h - 3, paint);
                    paint.setStyle(Paint.Style.FILL);
                    break;
            }
        }
    }
}