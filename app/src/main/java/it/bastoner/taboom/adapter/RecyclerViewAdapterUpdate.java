package it.bastoner.taboom.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.bastoner.taboom.MainActivity;
import it.bastoner.taboom.R;
import it.bastoner.taboom.animations.Animations;
import it.bastoner.taboom.database.Card;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;
import it.bastoner.taboom.utilities.Utilities;
import it.bastoner.taboom.viewModel.ViewModelMainActivity;

// TODO close all inner views item
public class RecyclerViewAdapterUpdate extends RecyclerView.Adapter<RecyclerViewAdapterUpdate.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapterUpdate";

    private List<CardWithTags> cardList;
    private List<Tag> tagList;
    private final Context context;
    private final ViewModelMainActivity viewModelFragment;
    private boolean allCardIsChecked;
    private int numberOfSelected;
    private final SharedPreferences sharedPreferences;
    private Set<String> selectedTags;


    public RecyclerViewAdapterUpdate(List<CardWithTags> cardList, List<Tag> tagList, Context context,
                                     ViewModelMainActivity viewModelFragment, SharedPreferences sharedPreferences) {
        this.cardList = cardList;
        this.tagList = tagList;
        this.context = context;
        this.viewModelFragment = viewModelFragment;
        this.sharedPreferences = sharedPreferences;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_update, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position == 0) {

            // All the cards
            holder.numberOfItems.setText(String.valueOf(cardList.size()));
            holder.tagName.setText(R.string.all_cards_tag);
            holder.clearTag.setVisibility(View.GONE); // Can't delete all cards
            holder.checkBox.setChecked(allCardIsChecked);
            holder.isOpen = sharedPreferences.getBoolean(context.getResources().getString(R.string.all_cards_tag),
                                                        false);
            holder.checkBox.setOnClickListener(view -> {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);

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

            initializeLayoutCards(cardList,null,  holder, true);

        } else {

            // Single TAG after all cards
            Tag tag = tagList.get(position - 1);

            List<CardWithTags> listOfSingleTag = new ArrayList<>();
            for (CardWithTags cwt: cardList) {
                for (Tag t: cwt.getTagList()) {
                    if (t.getTag().equals(tag.getTag()))
                        listOfSingleTag.add(cwt);
                }
            }

            initializeLayoutCards(listOfSingleTag, tag, holder, false);

            holder.numberOfItems.setText(String.valueOf(listOfSingleTag.size()));
            holder.tagName.setText(tag.getTag());
            holder.clearTag.setVisibility(View.VISIBLE);
            holder.isOpen = sharedPreferences.getBoolean(tag.getTag(), false);

            if (allCardIsChecked)
                holder.checkBox.setChecked(false);
            else {
                holder.checkBox.setChecked(false);
                for (String s: selectedTags) {
                    if (s.equalsIgnoreCase(tag.getTag()))
                        holder.checkBox.setChecked(true);
                }
            }
            holder.checkBox.setOnClickListener(view -> {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);

                if (holder.checkBox.isChecked()) {
                    MainActivity.recyclerTagList.add(tag);
                    selectedTags = sharedPreferences.getStringSet(Utilities.SELECTED_TAGS, new HashSet<>());
                    selectedTags.add(tag.getTag());
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
                        MainActivity.recyclerTagList.removeIf(t -> t.getTag().equalsIgnoreCase(tag.getTag()));
                        selectedTags = sharedPreferences.getStringSet(Utilities.SELECTED_TAGS, new HashSet<>());
                        selectedTags.remove(tag.getTag());
                        editor.putStringSet(Utilities.SELECTED_TAGS, selectedTags);
                        Log.d(TAG, ">>Tags selected: " + MainActivity.recyclerTagList);
                    }
                }

                editor.commit();

            });

            View viewDialogTag = LayoutInflater.from(context).inflate(R.layout.dialog_modify_tag, (ViewGroup) null);
            EditText tagNameEditText = viewDialogTag.findViewById(R.id.tag_name_dialog);
            tagNameEditText.setText(tag.getTag());

            // In this way the dialog is created only one time
            AlertDialog dialogTag = getDialogUpdateTag(tag, viewDialogTag, tagNameEditText);
            AlertDialog dialogDeleteTag = getDialogDeleteTag(tag);

            holder.tagName.setOnLongClickListener( view -> {
                // Change tag name only if closed
                if (holder.linearLayout.getVisibility() == View.GONE)
                    dialogTag.show();
                return true;
            });

            holder.clearTag.setOnClickListener( view -> dialogDeleteTag.show());

        }

        holder.linearLayout.setVisibility(holder.isOpen? View.VISIBLE : View.GONE);

    }

    private AlertDialog getDialogUpdateTag(Tag tag, View viewDialogTag, EditText tagNameEditText) {
        return new AlertDialog.Builder(context)
                .setView(viewDialogTag)
                .setTitle(R.string.title_modify_tag)
                .setPositiveButton(R.string.modify, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String newTag = tagNameEditText.getText().toString();
                        if (!newTag.isEmpty() && !newTag.equalsIgnoreCase(tag.getTag())) {

                            // If tag was selected update selectedTags and MainActivityRecyclerCardList
                            // with new name
                            boolean removed = false;
                            for (Tag t: MainActivity.recyclerTagList) {
                                if (t.getTag().equalsIgnoreCase(tag.getTag())) {
                                    MainActivity.recyclerTagList.remove(t);
                                    removed = true;
                                }
                            }
                            selectedTags = sharedPreferences.getStringSet(Utilities.SELECTED_TAGS, new HashSet<>());
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            for (String s: selectedTags) {
                                if (s.equalsIgnoreCase(tag.getTag())) {
                                    selectedTags.remove(s);
                                    selectedTags.add(newTag);
                                    editor.putStringSet(Utilities.SELECTED_TAGS, selectedTags);
                                }
                            }

                            editor.commit();
                            Log.d(TAG, ">>Update TAG: " + tag.getTag() + "->" + newTag);
                            tag.setTag(newTag);
                            if (removed)
                                MainActivity.recyclerTagList.add(tag);
                            viewModelFragment.updateTag(tag);
                        }
                    }
                })
                .create();
    }

    private AlertDialog getDialogDeleteTag(Tag tag) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.title_delete_tag)
                .setMessage(context.getResources().getString(R.string.delete_tag_dialog_message)
                            + " \"" + tag.getTag() + "\"")
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModelFragment.deleteTag(tag);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        selectedTags = sharedPreferences.getStringSet(Utilities.SELECTED_TAGS, new HashSet<>());
                        selectedTags.removeIf(string -> string.equalsIgnoreCase(tag.getTag()));
                        MainActivity.recyclerTagList.removeIf(t -> tag.getTag().equalsIgnoreCase(t.getTag()));
                        editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);
                        editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                        editor.commit();
                        Log.d(TAG, ">>Delete TAG: " + tag.getTag());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }

    private AlertDialog getDialogDeleteCardOrTag(Card card, Tag tag) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.delete)
                .setMessage(context.getResources().getString(R.string.delete_card_tag_dialog_message_1)
                            + " \"" + card.getTitle() + "\" "
                            + context.getResources().getString(R.string.delete_card_tag_dialog_message_2)
                            + " \"" + tag.getTag() + "\""
                            + context.getResources().getString(R.string.delete_card_tag_dialog_message_3))
                .setPositiveButton(R.string.delete_card, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        viewModelFragment.deleteCard(card);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);
                        editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                        editor.commit();
                        Log.d(TAG, ">>Delete card: " + card.getTitle());

                    }
                })
                .setNegativeButton(R.string.remove_tag, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        viewModelFragment.removeTagFromCard(card, tag);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);
                        editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                        editor.commit();
                        Log.d(TAG, ">>Remove tag \"" + tag.getTag() + "\" from card \""
                                + card.getTitle() + "\"");

                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }

    private AlertDialog getDialogDeleteCard(Card card) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.delete_card)
                .setMessage(context.getResources().getString(R.string.delete_card_tag_dialog_message_1)
                        + " \"" + card.getTitle() + "\"?")
                .setPositiveButton(R.string.delete_card, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModelFragment.deleteCard(card);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);
                        editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                        editor.commit();
                        Log.d(TAG, ">>Delete card: " + card.getTitle());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }

    private void initializeLayoutCards(List<CardWithTags> cardList, Tag tag, ViewHolder holder, boolean isAllCards) {

        // Or it will add already added cards
        holder.linearLayout.removeAllViews();

        for (CardWithTags cwt : cardList) {

            Card card = cwt.getCard();

            View cardView = LayoutInflater.from(context).inflate(R.layout.card_update, holder.linearLayout, false);
            TextView textViewCardName = cardView.findViewById(R.id.card_name);
            Button clearButton = cardView.findViewById(R.id.clear_card);
            ConstraintLayout layoutCardUpdate = cardView.findViewById(R.id.layout_update_card);
            EditText titleEditText = cardView.findViewById(R.id.update_title_edit_text);
            EditText taboo1EditText = cardView.findViewById(R.id.update_taboo_1_edit_text);
            EditText taboo2EditText = cardView.findViewById(R.id.update_taboo_2_edit_text);
            EditText taboo3EditText = cardView.findViewById(R.id.update_taboo_3_edit_text);
            EditText taboo4EditText = cardView.findViewById(R.id.update_taboo_4_edit_text);
            EditText taboo5EditText = cardView.findViewById(R.id.update_taboo_5_edit_text);
            Button saveButton = cardView.findViewById(R.id.save_button);
            Button tagButton = cardView.findViewById(R.id.tag_button);

            textViewCardName.setText(card.getTitle());
            titleEditText.setText(card.getTitle());
            taboo1EditText.setText(card.getTabooWord1());
            taboo2EditText.setText(card.getTabooWord2());
            taboo3EditText.setText(card.getTabooWord3());
            taboo4EditText.setText(card.getTabooWord4());
            taboo5EditText.setText(card.getTabooWord5());

            saveButton.setOnClickListener(view -> {

                Animations.doReduceIncreaseAnimation(view);

                String title = titleEditText.getText().toString();
                String taboo1 = taboo1EditText.getText().toString();
                String taboo2 = taboo2EditText.getText().toString();
                String taboo3 = taboo3EditText.getText().toString();
                String taboo4 = taboo4EditText.getText().toString();
                String taboo5 = taboo5EditText.getText().toString();

                if (title.equalsIgnoreCase(card.getTitle()) &&
                    taboo1.equalsIgnoreCase(card.getTabooWord1()) &&
                    taboo2.equalsIgnoreCase(card.getTabooWord2()) &&
                    taboo3.equalsIgnoreCase(card.getTabooWord3()) &&
                    taboo4.equalsIgnoreCase(card.getTabooWord4()) &&
                    taboo5.equalsIgnoreCase(card.getTabooWord5())) {
                    Log.d(TAG, ">>Change something before saving card");
                    return;
                }

                Card newCard = new Card(title, taboo1, taboo2, taboo3, taboo4, taboo5);
                newCard.setIdCard(card.getIdCard());

                // Check if new title already exists
                for (CardWithTags c: this.cardList) {
                    if (!c.getCard().getTitle().equalsIgnoreCase(card.getTitle()) && c.getCard().getTitle().equalsIgnoreCase(title)) {
                        Toast.makeText(context, R.string.title_already_exists, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                cwt.setCard(newCard);
                Log.d(TAG, ">>New cwt: " + cwt);
                viewModelFragment.updateCWT(cwt);
                Toast.makeText(context, R.string.card_updated, Toast.LENGTH_SHORT).show();
            });

            tagButton.setOnClickListener(view -> {

                Animations.doReduceIncreaseAnimation(view);
            });

            textViewCardName.setOnClickListener(view -> {
                openCloseView(layoutCardUpdate, null);
            });

            AlertDialog dialogDeleteCard;
            if (!isAllCards)
                dialogDeleteCard = getDialogDeleteCardOrTag(card, tag);
            else
                dialogDeleteCard = getDialogDeleteCard(card);

            clearButton.setOnClickListener(view -> dialogDeleteCard.show());

            String tagName;
            tagName = (tag != null) ? tag.getTag() : context.getResources().getString(R.string.all_cards_tag);
            // Set the click on both number of items and tag name
            holder.tagName.setOnClickListener(view -> {
                openCloseView(holder.linearLayout, tagName);
            });
            holder.numberOfItems.setOnClickListener(view -> {
                openCloseView(holder.linearLayout, tagName);
            });

            holder.linearLayout.addView(cardView);

        }

    }

    private void openCloseView(View view, String tagName) {

        if (view.getVisibility() == View.GONE) {

            view.setVisibility(View.VISIBLE);
            if (tagName != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(tagName, true);
                editor.commit();
            }

            AnimationSet animation = new AnimationSet(true);
            Animation animationAlpha = new AlphaAnimation(0, 1);
            animation.addAnimation(animationAlpha);
            Animation animationTranslate = new TranslateAnimation(0, 0, -view.getHeight(), 0);
            animation.addAnimation(animationTranslate);
            Animation animationY = new ScaleAnimation(1, 1, 0, 1);
            animationY.setInterpolator(new LinearInterpolator());
            animation.addAnimation(animationY);
            animation.setDuration(200);

            view.startAnimation(animation);

        } else {

            AnimationSet animation = new AnimationSet(true);
            Animation animationAlpha = new AlphaAnimation(1, 0);
            animation.addAnimation(animationAlpha);
            Animation animationTranslate = new TranslateAnimation(0, 0, 0, -view.getHeight());
            animation.addAnimation(animationTranslate);
            Animation animationY = new ScaleAnimation(1, 1, 1, 0);
            animationY.setInterpolator(new LinearInterpolator());
            animation.addAnimation(animationY);
            animation.setDuration(200);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                    if (tagName != null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(tagName);
                        editor.commit();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            view.startAnimation(animation);
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
        private final LinearLayout linearLayout;
        private boolean isOpen = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            numberOfItems = itemView.findViewById(R.id.number_of_items);
            tagName = itemView.findViewById(R.id.tag_name);
            clearTag = itemView.findViewById(R.id.clear_tag);
            checkBox = itemView.findViewById(R.id.check_box);
            linearLayout = itemView.findViewById(R.id.layout_cards);

        }

    }

    public void setCardList(List<CardWithTags> cardList) {
        this.cardList = cardList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }
}
