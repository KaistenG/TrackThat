package com.example.trackthat.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.MotionEvent;

import java.util.Calendar;

import com.example.trackthat.model.Habit;
import com.example.trackthat.model.HabitEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CalendarView extends View {

    private Paint paint;
    private int cellSize;
    private final int columns = 7;
    private final String[] dayLabels = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
    private int headerHeight;

    private int year;
    private int month;

    private List<Habit> habits = new ArrayList<>();
    private Map<String, List<Habit>> activeHabitsPerDay = new HashMap<>();

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
            Calendar today = Calendar.getInstance();
            boolean isToday = today.get(Calendar.YEAR) == year
                    && today.get(Calendar.MONTH) == month
                    && today.get(Calendar.DAY_OF_MONTH) == day;

            paint.setTypeface(Typeface.DEFAULT);
            paint.setColor(isToday ? 0xFFD1C4E9 : 0xFFEEEEEE);
            canvas.drawRect(x + 2, y + 2, x + cellSize - 2, y + cellSize - 2, paint);
            // Tageszahl oben rechts
            paint.setColor(0xFF333333);
            paint.setTextSize(24);
            String dayStr = String.valueOf(day);
            float textWidth = paint.measureText(dayStr);
            canvas.drawText(dayStr, x + cellSize - textWidth - 8, y + 28, paint);

            // Streifen zeichnen
            String dayKey = String.valueOf(day);
            if (activeHabitsPerDay.containsKey(dayKey)) {
                List<Habit> dayHabits = activeHabitsPerDay.get(dayKey);
                int stripeWidth = (int) (cellSize * 0.05f);
                int verticalIndex = 0;
                int horizontalIndex = 0;

                // Erst vertikale
                for (Habit habit : dayHabits) {
                    if (!habit.getVisualType().equals("VERTICAL")) continue;
                    paint.setColor(habit.getColor());
                    int vX = x + (int) (cellSize * 0.10f * (verticalIndex + 1));
                    canvas.drawRect(vX, y + 2, vX + stripeWidth, y + cellSize - 2, paint);
                    verticalIndex++;
                }

                // Dann horizontale
                for (Habit habit : dayHabits) {
                    if (!habit.getVisualType().equals("HORIZONTAL")) continue;
                    paint.setColor(habit.getColor());
                    int hY = y + (int) (cellSize * 0.10f * (horizontalIndex + 1));
                    canvas.drawRect(x + 2, hY, x + cellSize - 2, hY + stripeWidth, paint);
                    horizontalIndex++;
                }

                // Border zuletzt
                for (Habit habit : dayHabits) {
                    if (!habit.getVisualType().equals("BORDER")) continue;
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(habit.getColor());
                    paint.setStrokeWidth(6);
                    canvas.drawRect(x + 3, y + 3, x + cellSize - 3, y + cellSize - 3, paint);
                    paint.setStyle(Paint.Style.FILL);
                }
            }
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

    public interface OnDayClickListener {
        void onDayClick(int year, int month, int day);
    }

    private OnDayClickListener dayClickListener;

    public void setOnDayClickListener(OnDayClickListener listener) {
        this.dayClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY() - headerHeight;

            if (y < 0) return true; // Tipp auf Header ignorieren

            int col = x / cellSize;
            int row = y / cellSize;
            int index = row * columns + col;

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);
            int firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
            int day = index - firstDayOfWeek + 1;

            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (day >= 1 && day <= daysInMonth && dayClickListener != null) {
                dayClickListener.onDayClick(year, month, day);
            }
        }
        return true;
    }
    public void jumpToToday() {
        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
        invalidate();
    }

    public void setMonthData(List<Habit> habits, List<HabitEntry> entries) {
        this.habits = habits;
        activeHabitsPerDay.clear();

        for (HabitEntry entry : entries) {
            String day = entry.getDate().substring(8); // "YYYY-MM-DD" -> "DD"
            day = String.valueOf(Integer.parseInt(day)); // "09" -> "9"
            if (!activeHabitsPerDay.containsKey(day)) {
                activeHabitsPerDay.put(day, new ArrayList<>());
            }
            for (Habit habit : habits) {
                if (habit.getId().equals(entry.getHabitId())) {
                    activeHabitsPerDay.get(day).add(habit);
                }
            }
        }
        invalidate();
    }

}