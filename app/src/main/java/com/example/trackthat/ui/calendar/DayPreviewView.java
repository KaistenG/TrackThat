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
    private List<Habit> allHabits = new ArrayList<>();

    public DayPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setActiveHabits(List<Habit> habits) {
        this.activeHabits = habits;
        invalidate();
    }

    public void setAllHabits(List<Habit> allHabits) {
        this.allHabits = allHabits;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        int stripeWidth = (int) (w * 0.06f);
        int verticalIndex = 0;
        int horizontalIndex = 0;

        for (Habit habit : allHabits) {
            boolean isActive = false;
            for (Habit active : activeHabits) {
                if (active.getId().equals(habit.getId())) {
                    isActive = true;
                    break;
                }
            }

            if (!isActive) {
                if (habit.getVisualType().equals("VERTICAL")) verticalIndex++;
                else if (habit.getVisualType().equals("HORIZONTAL")) horizontalIndex++;
                continue;
            }

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(habit.getColor());

            switch (habit.getVisualType()) {
                case "VERTICAL":
                    int vX = (int) (w * 0.10f * (verticalIndex + 1));
                    canvas.drawRect(vX, 0, vX + stripeWidth, h, paint);
                    verticalIndex++;
                    break;
                case "HORIZONTAL":
                    int hY = (int) (h * 0.10f * (horizontalIndex + 1));
                    canvas.drawRect(0, hY, w, hY + stripeWidth, paint);
                    horizontalIndex++;
                    break;
                case "BORDER":
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(habit.getColor());
                    paint.setStrokeWidth(12);
                    canvas.drawRect(4, 4, w - 4, h - 4, paint);
                    paint.setStyle(Paint.Style.FILL);
                    break;
            }
        }
    }
}