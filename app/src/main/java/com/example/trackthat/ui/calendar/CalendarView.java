package com.example.trackthat.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class CalendarView extends View {

    private Paint paint;
    private int cellSize;
    private int columns = 7;

    private int year;
    private int month; // 0-basiert (0 = Januar)

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);

        // Aktuellen Monat setzen
        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        cellSize = width / columns;
        int rows = 6;
        int height = cellSize * rows;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);

        // Welcher Wochentag ist der 1. des Monats? (Montag = 0)
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        firstDayOfWeek = (firstDayOfWeek + 5) % 7; // Umrechnen: Montag als Start

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            int index = firstDayOfWeek + day - 1;
            int col = index % columns;
            int row = index / columns;
            int x = col * cellSize;
            int y = row * cellSize;

            // Zellhintergrund
            paint.setColor(0xFFEEEEEE);
            canvas.drawRect(x + 2, y + 2, x + cellSize - 2, y + cellSize - 2, paint);

            // Tageszahl
            paint.setColor(0xFF333333);
            paint.setTextSize(28);
            paint.setTypeface(Typeface.DEFAULT);
            canvas.drawText(String.valueOf(day), x + 10, y + 35, paint);
        }
    }

    // Monat wechseln
    public void nextMonth() {
        if (month == 11) {
            month = 0;
            year++;
        } else {
            month++;
        }
        invalidate();
    }

    public void previousMonth() {
        if (month == 0) {
            month = 11;
            year--;
        } else {
            month--;
        }
        invalidate();
    }

    public int getYear() { return year; }
    public int getMonth() { return month; }
}