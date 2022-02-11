package it.bastoner.taboom.adapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.database.Card;
import it.bastoner.taboom.database.Tag;


public class RecyclerViewAdapterAdd extends RecyclerView.Adapter<RecyclerViewAdapterAdd.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapterAdd";

    private List<Tag> tagList;

    public RecyclerViewAdapterAdd(List<Tag> tagList) {
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tagTextView.setText(tagList.get(position).getTag());
    }

    @Override
    public int getItemCount() {
        if (tagList == null)
            return 0;
        return tagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "ViewHolder";

        private final Chip tagTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tagTextView = itemView.findViewById(R.id.tag_name_recycler);

        }
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }
}
