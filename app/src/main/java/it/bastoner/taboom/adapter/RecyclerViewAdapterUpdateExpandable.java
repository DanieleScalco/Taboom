package it.bastoner.taboom.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandCollapseController;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.bastoner.taboom.MainActivity;
import it.bastoner.taboom.R;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;
import it.bastoner.taboom.utilities.CardGroup;
import it.bastoner.taboom.utilities.Utilities;
import it.bastoner.taboom.viewModel.ViewModelMainActivity;

public class RecyclerViewAdapterUpdateExpandable extends ExpandableRecyclerViewAdapter<RecyclerViewAdapterUpdateExpandable.CardGroupViewHolder, RecyclerViewAdapterUpdateExpandable.CardViewHolder> {

    private static final String TAG = "RecyclerViewAdapterUpdateExpandable";

    private Context context;
    private SharedPreferences sharedPreferences;
    private ViewModelMainActivity viewModel;
    private Set<String> selectedTags;
    private boolean allCardIsChecked;
    private int numberOfSelected;

    public RecyclerViewAdapterUpdateExpandable(List<? extends ExpandableGroup> groups, Context context,
                                               SharedPreferences sharedPreferences, ViewModelMainActivity viewModel) {
        super(groups);

        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.viewModel = viewModel;
        selectedTags = sharedPreferences.getStringSet(Utilities.SELECTED_TAGS, new HashSet<>());
        if (selectedTags.isEmpty()) {
            this.allCardIsChecked = true;
            this.numberOfSelected = 1;
        }
        else {
            this.allCardIsChecked = false;
            this.numberOfSelected = selectedTags.size();
        }
    }


    @Override
    public CardGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_update, parent, false);
        return new RecyclerViewAdapterUpdateExpandable.CardGroupViewHolder(view);
    }

    @Override
    public CardViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_update, parent, false);
        return new RecyclerViewAdapterUpdateExpandable.CardViewHolder(view);

    }

    @Override
    public void onBindChildViewHolder(CardViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final CardWithTags cardWithTags = ((CardGroup) group).getItems().get(childIndex);
        holder.textViewCardName.setText(cardWithTags.getCard().getTitle());
    }

    @Override
    public void onBindGroupViewHolder(CardGroupViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.tagName.setText(group.getTitle());
        holder.numberOfItems.setText(String.valueOf(group.getItems().size()));

        if (flatPosition == 0) {

            // If all cards
            holder.clearTag.setVisibility(View.GONE);
            holder.checkBox.setChecked(allCardIsChecked);
            holder.checkBox.setOnClickListener(view -> {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);
                MainActivity.recyclerCardIsChanged = true;


                if (holder.checkBox.isChecked()) {

                    MainActivity.recyclerTagList.clear();
                    editor.putStringSet(Utilities.SELECTED_TAGS, new HashSet<>());
                    Log.d(TAG, ">>Tags selected: " + MainActivity.recyclerTagList);

                    numberOfSelected = 1;
                    if (!allCardIsChecked) {
                        allCardIsChecked = true;
                        notifyDataSetChanged();
                    }

                } else {
                    // Can't deselect all cards, at least one group chosen
                    holder.checkBox.setChecked(true);
                    Toast.makeText(context, R.string.min_one_tag, Toast.LENGTH_SHORT).show();
                }

                editor.commit();

            });

        } else {

            // Tag group
            if (allCardIsChecked)
                holder.checkBox.setChecked(false);
            else {
                holder.checkBox.setChecked(false);
                for (String s: selectedTags) {
                    if (s.equalsIgnoreCase(group.getTitle()))
                        holder.checkBox.setChecked(true);
                }
            }
            holder.checkBox.setOnClickListener(view -> {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);
                MainActivity.recyclerCardIsChanged = true;


                if (holder.checkBox.isChecked()) {
                    MainActivity.recyclerTagList.add(((CardGroup) group).getTag());
                    selectedTags = sharedPreferences.getStringSet(Utilities.SELECTED_TAGS, new HashSet<>());
                    selectedTags.add(group.getTitle());
                    editor.putStringSet(Utilities.SELECTED_TAGS, selectedTags);
                    Log.d(TAG, ">>Tags selected: " + MainActivity.recyclerTagList);

                    if (allCardIsChecked) {
                        allCardIsChecked = false;
                        notifyDataSetChanged();
                    } else {
                        ++numberOfSelected;
                    }
                } else {
                    if (numberOfSelected == 1) {
                        Toast.makeText(context, R.string.min_one_tag, Toast.LENGTH_SHORT).show();
                        holder.checkBox.setChecked(true);
                    }
                    else {
                        --numberOfSelected;
                        MainActivity.recyclerTagList.removeIf(t -> t.getTag().equalsIgnoreCase(group.getTitle()));
                        selectedTags = sharedPreferences.getStringSet(Utilities.SELECTED_TAGS, new HashSet<>());
                        selectedTags.remove(group.getTitle());
                        editor.putStringSet(Utilities.SELECTED_TAGS, selectedTags);
                        Log.d(TAG, ">>Tags selected: " + MainActivity.recyclerTagList);
                    }
                }

                editor.commit();

            });
        }
    }

    public class CardGroupViewHolder extends GroupViewHolder {

        private final TextView numberOfItems;
        private final TextView tagName;
        private final Button clearTag;
        private final CheckBox checkBox;

        public CardGroupViewHolder(View itemView) {
            super(itemView);

            numberOfItems = itemView.findViewById(R.id.number_of_items);
            tagName = itemView.findViewById(R.id.tag_name);
            clearTag = itemView.findViewById(R.id.clear_tag);
            checkBox = itemView.findViewById(R.id.check_box);
        }

    }

    public class CardViewHolder extends ChildViewHolder {

        private final TextView textViewCardName;
        private final Button clearButton;

        public CardViewHolder(View itemView) {
            super(itemView);
            textViewCardName = itemView.findViewById(R.id.card_name);
            clearButton = itemView.findViewById(R.id.clear_card);        }

    }
}

