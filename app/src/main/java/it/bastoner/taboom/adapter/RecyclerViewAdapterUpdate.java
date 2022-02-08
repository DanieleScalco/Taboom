package it.bastoner.taboom.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.ViewModelMainActivity;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;
import it.bastoner.taboom.fragments.BaseCardFragment;
import it.bastoner.taboom.fragments.UpdateFragment;


public class RecyclerViewAdapterUpdate extends RecyclerView.Adapter<RecyclerViewAdapterUpdate.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapterUpdate";

    private List<CardWithTags> cardList;
    private List<Tag> tagList;
    private Context context;
    private LayoutInflater layoutInflater;
    private ViewModelMainActivity viewModelFragment;


    public RecyclerViewAdapterUpdate(List<CardWithTags> cardList, List<Tag> tagList, Context context,
                                     LayoutInflater layoutInflater, ViewModelMainActivity viewModelFragment) {
        this.cardList = cardList;
        this.tagList = tagList;
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.viewModelFragment = viewModelFragment;

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
            holder.numberOfItems.setText(String.valueOf(cardList.size()));
            holder.tagName.setText(R.string.all_cards_tag);
            holder.clearTag.setVisibility(View.GONE);
        } else {

            Tag tag = tagList.get(position - 1);
            List<CardWithTags> listOfSingleTag = new ArrayList<>();

            for (CardWithTags cwt: cardList) {
                for (Tag t: cwt.getTagList()) {
                    if (t.getTag().equals(tag.getTag()))
                        listOfSingleTag.add(cwt);
                }
            }

            holder.numberOfItems.setText(String.valueOf(listOfSingleTag.size()));
            holder.tagName.setText(tag.getTag());

            View viewDialogTag = layoutInflater.inflate(R.layout.dialog_modify_tag, null);
            EditText tagNameEditText = viewDialogTag.findViewById(R.id.tag_name_dialog);
            tagNameEditText.setText(tag.getTag());

            // In this way the dialog is created only one time
            AlertDialog dialogTag = new AlertDialog.Builder(context)
                    .setView(viewDialogTag)
                    .setTitle(R.string.title_modify_tag)
                    .setPositiveButton(R.string.modify, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String newTag = tagNameEditText.getText().toString();
                            if (!newTag.isEmpty()&& !newTag.equalsIgnoreCase(tag.getTag())) {
                                tag.setTag(newTag);
                                viewModelFragment.updateTag(tag);
                                Log.d(TAG, ">>Update TAG: " + tag.getTag() +  "->" + newTag);
                            }
                        }
                    })
                    .create();

            AlertDialog dialogDestroy = new AlertDialog.Builder(context)
                    .setTitle(R.string.title_delete_tag)
                    .setMessage(R.string.destroy_dialog_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            viewModelFragment.deleteTag(tag);
                            Log.d(TAG, ">>Delete TAG: " + tag.getTag());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();

            holder.tagName.setOnLongClickListener( view -> {
                dialogTag.show();
                return true;
            });

            holder.clearTag.setOnClickListener( v -> dialogDestroy.show());
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
