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
        int stripeWidth = (int) (w * 0.05f);
        int verticalIndex = 0;
        int horizontalIndex = 0;

        // Erst alle vertikalen Streifen
        for (Habit habit : activeHabits) {
            if (!habit.getVisualType().equals("VERTICAL")) continue;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(habit.getColor());
            int vX = (int) (w * 0.15f * (verticalIndex + 1));
            canvas.drawRect(vX, 0, vX + stripeWidth, h, paint);
            verticalIndex++;
        }

        // Dann alle horizontalen Streifen
        for (Habit habit : activeHabits) {
            if (!habit.getVisualType().equals("HORIZONTAL")) continue;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(habit.getColor());
            int hY = (int) (h * 0.15f * (horizontalIndex + 1));
            canvas.drawRect(0, hY, w, hY + stripeWidth, paint);
            horizontalIndex++;
        }

        // Border immer zuletzt (oben drüber)
        for (Habit habit : activeHabits) {
            if (!habit.getVisualType().equals("BORDER")) continue;
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(habit.getColor());
            paint.setStrokeWidth(24);
            canvas.drawRect(4, 4, w - 4, h - 4, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }
}