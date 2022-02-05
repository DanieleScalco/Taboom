package it.bastoner.taboom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;


public class RecyclerViewAdapterUpdate extends RecyclerView.Adapter<RecyclerViewAdapterUpdate.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapterUpdate";

    private List<CardWithTags> cardList;
    private List<Tag> tagList;

    public RecyclerViewAdapterUpdate(List<CardWithTags> cardList, List<Tag> tagList) {
        this.cardList = cardList;
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_update, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position == 0) {
            holder.numberOfItems.setText("" + cardList.size());
            holder.tagName.setText("Tutte le carte");
        }
    }

    @Override
    public int getItemCount() {
        if (tagList == null)
            return 1;
        return tagList.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "ViewHolder";

        private final TextView numberOfItems;
        private final TextView tagName;
        private final Button clearTag;
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            numberOfItems = itemView.findViewById(R.id.number_of_items);
            tagName = itemView.findViewById(R.id.tag_name);
            clearTag = itemView.findViewById(R.id.clear_tag);
            checkBox = itemView.findViewById(R.id.check_box);

        }
    }

    public void setCardList(List<CardWithTags> cardList) {
        this.cardList = cardList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }
}
