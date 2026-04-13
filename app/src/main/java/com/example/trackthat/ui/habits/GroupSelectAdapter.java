package com.example.trackthat.ui.habits;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackthat.model.Group;

import java.util.List;

public class GroupSelectAdapter extends RecyclerView.Adapter<GroupSelectAdapter.ViewHolder> {

    public interface OnGroupSelectedListener {
        void onSelected(String groupId);
    }

    private List<Group> groups;
    private String selectedGroupId;
    private OnGroupSelectedListener listener;

    public GroupSelectAdapter(List<Group> groups, String selectedGroupId, OnGroupSelectedListener listener) {
        this.groups = groups;
        this.selectedGroupId = selectedGroupId;
        this.listener = listener;
    }

    public void updateGroups(List<Group> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.textView.setText(group.getName());

        boolean isSelected = group.getId() != null && group.getId().equals(selectedGroupId);
        holder.itemView.setAlpha(isSelected ? 1.0f : 0.5f);
        holder.itemView.setBackgroundColor(isSelected ? 0xFFE1F5FE : 0x00000000);

        holder.itemView.setOnClickListener(v -> {
            selectedGroupId = group.getId();
            listener.onSelected(group.getId());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}