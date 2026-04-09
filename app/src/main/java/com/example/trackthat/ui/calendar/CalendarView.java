package com.example.trackthat.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CalendarView extends View {

    private Paint paint;
    private int cellSize = 100;
    private int columns = 7;

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        cellSize = width / columns;
        int rows = 6; // max Wochen pro Monat
        int height = cellSize * rows;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Testweise 30 Zellen zeichnen
        for (int i = 0; i < 30; i++) {
            int col = i % columns;
            int row = i / columns;
            int x = col * cellSize;
            int y = row * cellSize;

            // Zellhintergrund
            paint.setColor(0xFFEEEEEE);
            canvas.drawRect(x + 2, y + 2, x + cellSize - 2, y + cellSize - 2, paint);

            // Tageszahl
            paint.setColor(0xFF333333);
            paint.setTextSize(28);
            canvas.drawText(String.valueOf(i + 1), x + 10, y + 35, paint);
        }
    }
}