package com.example.trackthat.ui.habits;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class HabitDragCallback extends ItemTouchHelper.Callback {

    public interface OnItemMovedListener {
        void onItemMoved(int fromPosition, int toPosition);
        void onDragFinished();
    }

    private OnItemMovedListener listener;

    public HabitDragCallback(OnItemMovedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {
        // Nur Habits verschieben, keine Gruppenüberschriften
        if (viewHolder instanceof HabitGroupAdapter.HeaderViewHolder) {
            return makeMovementFlags(0, 0);
        }
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        // Nicht auf Gruppenüberschriften ziehen
        if (target instanceof HabitGroupAdapter.HeaderViewHolder) return false;

        listener.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        listener.onDragFinished();
    }
}