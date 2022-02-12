package it.bastoner.taboom.adapter;

import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.database.Tag;


public class RecyclerViewAdapterAdd extends RecyclerView.Adapter<RecyclerViewAdapterAdd.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapterAdd";

    private List<Tag> tagList;
    private List<Tag> tagListChosen = new ArrayList<>();

    // TagList is a COPY of the TagList in ViewModel
    public RecyclerViewAdapterAdd(List<Tag> tagList, List tagListChosen) {
        this.tagList = tagList;
        this.tagListChosen = tagListChosen;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Tag actualTag = tagList.get(position);
        holder.tag = actualTag;
        if (actualTag.getIdTag() == -1) {
            holder.chip.setChecked(true);
        }
        if (holder.chip.isChecked() && !tagListChosen.contains(actualTag))
            tagListChosen.add(actualTag);
        holder.chip.setText(actualTag.getTag());
        holder.chip.setOnClickListener(view -> {
            if (holder.chip.isChecked()) {
                tagListChosen.add(actualTag);
            } else {
                tagListChosen.remove(actualTag);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (tagList == null)
            return 0;
        return tagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "ViewHolder";

        private final Chip chip;
        private Tag tag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chip = itemView.findViewById(R.id.tag_name_recycler);

        }
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public void addTag(Tag tag) {
        if (tagList != null)
            tagList.add(tag);
    }

    public List<Tag> getTagListChosen() {
        return tagListChosen;
    }
}
