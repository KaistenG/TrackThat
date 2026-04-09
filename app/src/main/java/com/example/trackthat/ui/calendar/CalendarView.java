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
    private final int columns = 7;
    private final String[] dayLabels = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
    private int headerHeight;

    private int year;
    private int month;

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);

        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        cellSize = width / columns;
        headerHeight = cellSize / 2;
        int rows = 6;
        int height = headerHeight + cellSize * rows;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Wochentag-Header zeichnen
        paint.setTextSize(26);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        for (int i = 0; i < columns; i++) {
            int x = i * cellSize;
            paint.setColor(0xFF888888);
            float textWidth = paint.measureText(dayLabels[i]);
            canvas.drawText(dayLabels[i], x + (cellSize - textWidth) / 2, headerHeight - 8, paint);
        }

        // Kalender-Zellen zeichnen
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        firstDayOfWeek = (firstDayOfWeek + 5) % 7;

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            int index = firstDayOfWeek + day - 1;
            int col = index % columns;
            int row = index / columns;
            int x = col * cellSize;
            int y = headerHeight + row * cellSize;

            // Zellhintergrund
            paint.setColor(0xFFEEEEEE);
            paint.setTypeface(Typeface.DEFAULT);
            canvas.drawRect(x + 2, y + 2, x + cellSize - 2, y + cellSize - 2, paint);

            // Tageszahl oben rechts
            paint.setColor(0xFF333333);
            paint.setTextSize(24);
            String dayStr = String.valueOf(day);
            float textWidth = paint.measureText(dayStr);
            canvas.drawText(dayStr, x + cellSize - textWidth - 8, y + 28, paint);
        }
    }

    public void nextMonth() {
        if (month == 11) { month = 0; year++; }
        else { month++; }
        invalidate();
    }

    public void previousMonth() {
        if (month == 0) { month = 11; year--; }
        else { month--; }
        invalidate();
    }

    public int getYear() { return year; }
    public int getMonth() { return month; }
}