package it.bastoner.taboom.adapter;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.bastoner.taboom.MainActivity;
import it.bastoner.taboom.R;
import it.bastoner.taboom.animations.Animations;
import it.bastoner.taboom.database.Card;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;
import it.bastoner.taboom.utilities.CardGroup;
import it.bastoner.taboom.utilities.Utilities;
import it.bastoner.taboom.viewModel.ViewModelMainActivity;

public class RecyclerViewAdapterUpdateExpandable extends ExpandableRecyclerViewAdapter<RecyclerViewAdapterUpdateExpandable.CardGroupViewHolder, RecyclerViewAdapterUpdateExpandable.CardViewHolder> {

    private static final String TAG = "RecyclerViewAdapterUpdateExpandable";

    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final ViewModelMainActivity viewModel;
    private Set<String> selectedTags;
    private boolean allCardIsChecked;
    private int numberOfSelected;
    private List<CardWithTags> cardList;
    private List<Tag> tagList;

    public RecyclerViewAdapterUpdateExpandable(List<? extends ExpandableGroup> groups, Context context,
                                               SharedPreferences sharedPreferences, ViewModelMainActivity viewModel,
                                               List<CardWithTags> cardList, List<Tag> tagList) {
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
        this.cardList = cardList;
        this.tagList = tagList;
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
        CardWithTags cardWithTags = ((CardGroup) group).getItems().get(childIndex);
        Card card = cardWithTags.getCard();
        List<Tag> tagListCard = cardWithTags.getTagList();
        holder.chosenTags = tagListCard;
        holder.textViewCardName.setText(card.getTitle());
        holder.titleEditText.setText(card.getTitle());
        holder.taboo1EditText.setText(card.getTabooWord1());
        holder.taboo2EditText.setText(card.getTabooWord2());
        holder.taboo3EditText.setText(card.getTabooWord3());
        holder.taboo4EditText.setText(card.getTabooWord4());
        holder.taboo5EditText.setText(card.getTabooWord5());

        holder.saveButton.setOnClickListener(view -> {

            String title = holder.titleEditText.getText().toString();
            String taboo1 = holder.taboo1EditText.getText().toString();
            String taboo2 = holder.taboo2EditText.getText().toString();
            String taboo3 = holder.taboo3EditText.getText().toString();
            String taboo4 = holder.taboo4EditText.getText().toString();
            String taboo5 = holder.taboo5EditText.getText().toString();

            Card newCard = new Card(title, taboo1, taboo2, taboo3, taboo4, taboo5);
            newCard.setIdCard(card.getIdCard());

            // Check if new title already exists
            for (CardWithTags c: this.cardList) {
                if (!c.getCard().getTitle().equalsIgnoreCase(card.getTitle()) && c.getCard().getTitle().equalsIgnoreCase(title)) {
                    Toast.makeText(context, R.string.title_already_exists, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            cardWithTags.setCard(newCard);
            cardWithTags.setTagList(holder.chosenTags != null ? holder.chosenTags : new ArrayList<>());
            Log.d(TAG, ">>New cwt: " + cardWithTags);

            Animator.AnimatorListener listener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    viewModel.updateCWT(cardWithTags);
                    Toast.makeText(context, R.string.card_updated, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            };

            Animations.doReduceIncreaseAnimation(view, listener);

        });

        holder.tagButton.setOnClickListener(view -> {

            holder.chosenTags = new ArrayList<>(tagListCard);


            Animator.AnimatorListener listener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) { }

                @Override
                public void onAnimationEnd(Animator animator) {
                    setRecyclerViewDialog(holder.chosenTags, tagList, holder).show();
                }

                @Override
                public void onAnimationCancel(Animator animator) { }

                @Override
                public void onAnimationRepeat(Animator animator) { }
            };

            Animations.doReduceIncreaseAnimation(view, listener);

            Log.d(TAG, ">>Tags of " + card.getTitle() + ": " + holder.chosenTags);

        });

        holder.textViewCardName.setOnClickListener(view -> {
            openCloseView(holder.layoutCardUpdate);
            Log.d(TAG, ">>Toggle card: " + ((TextView) view).getText());
        });

        holder.clearButton.setOnClickListener(view -> {
            AlertDialog dialogDeleteCard;
                if (((CardGroup) group).getTag() != null)
                    dialogDeleteCard = getDialogDeleteCardOrTag(card, ((CardGroup) group).getTag());
                else
                    dialogDeleteCard = getDialogDeleteCard(card);
                dialogDeleteCard.show();

            });
    }

    private AlertDialog getDialogDeleteCard(Card card) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.delete_card)
                .setMessage(context.getResources().getString(R.string.delete_card_tag_dialog_message_1)
                        + " \"" + card.getTitle() + "\"?")
                .setPositiveButton(R.string.delete_card, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.deleteCard(card);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);
                        editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                        editor.commit();
                        Log.d(TAG, ">>Delete card: " + card.getTitle());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
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

                        viewModel.deleteCard(card);
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

                        viewModel.removeTagFromCard(card, tag);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);
                        editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                        editor.commit();
                        Log.d(TAG, ">>Remove tag \"" + tag.getTag() + "\" from card \""
                                + card.getTitle() + "\"");

                    }
                })
                .setNeutralButton(R.string.cancel, null)
                .create();
    }

    private AlertDialog setRecyclerViewDialog(List<Tag> chosenTags, List<Tag> tagList, CardViewHolder holder) {

        // 1) Create dialog
        View dialogTags = LayoutInflater.from(context).inflate(R.layout.dialog_tags, null);
        Button newTagButton = dialogTags.findViewById(R.id.new_tag_button_dialog);
        EditText newTagEditText = dialogTags.findViewById(R.id.insert_new_tag);

        newTagButton.setOnClickListener(v -> {

            Animations.doReduceIncreaseAnimation(v, null);
            addTag(newTagEditText, holder);
        });

        AlertDialog dialogTagCard = getDialogTags(dialogTags, holder);

        // 2) Set RecyclerView
        holder.recyclerView = dialogTags.findViewById(R.id.recycler_view_tags);
        holder.tagsRecycler = new ArrayList<>(tagList);
        holder.recyclerViewAdapter = new RecyclerViewAdapterAdd(holder.tagsRecycler, new ArrayList<>(chosenTags));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,
                RecyclerView.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);

        holder.recyclerView.setAdapter(holder.recyclerViewAdapter);

        return  dialogTagCard;
    }

    private void addTag(EditText newTagEditText, CardViewHolder holder) {

        Tag newTag = new Tag(newTagEditText.getText().toString());

        if (newTag.getTag().isEmpty()) {
            Toast.makeText(context, R.string.tag_name_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (newTag.getTag().equalsIgnoreCase(context.getResources().getString(R.string.all_cards_tag))) {
            Toast.makeText(context, R.string.invalid_tag_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utilities.tagAlreadyExists(newTag, holder.tagsRecycler)) {
            Toast.makeText(context, context.getResources().getString(R.string.tag_already_exist)
                            + " \"" + newTag.getTag()
                            + "\" "+ context.getResources().getString(R.string.tag_already_exists_2),
                    Toast.LENGTH_LONG).show();
        } else {
            newTagEditText.setText("");
            holder.tagsRecycler.add(newTag);
            holder.chosenTags.add(newTag);
            Log.d(TAG, ">>chosenTags: " + holder.chosenTags);
            holder.recyclerViewAdapter.setTagList(new ArrayList<>(holder.tagsRecycler));
            holder.recyclerViewAdapter.setTagListChosen(new ArrayList<>(holder.chosenTags));
            holder.recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void openCloseView(View view/*, String tagName*/) {

        if (view.getVisibility() == View.GONE) {

            view.setVisibility(View.VISIBLE);

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
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });

            view.startAnimation(animation);
        }
    }

    @Override
    public void onBindGroupViewHolder(CardGroupViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.tagName.setText(group.getTitle());
        holder.numberOfItems.setText(String.valueOf(group.getItems().size()));

        for (TextView textView : Arrays.asList(holder.numberOfItems, holder.tagName)) {
            textView.setOnClickListener(view -> {
                Log.d(TAG, ">>Toggle Tag " + group.getTitle());
                toggleGroup(group);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isGroupExpanded(group)) {
                    editor.putBoolean(group.getTitle(), true);
                } else {
                    editor.remove(group.getTitle());
                }
                editor.commit();
            });
        }


        if (((CardGroup) group).getTag() == null) {

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
            holder.clearTag.setVisibility(View.VISIBLE);

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

            holder.tagName.setOnLongClickListener( view -> {
                View viewDialogTag = LayoutInflater.from(context).inflate(R.layout.dialog_modify_tag, (ViewGroup) null);
                EditText tagNameEditText = viewDialogTag.findViewById(R.id.tag_name_dialog);
                tagNameEditText.setText(((CardGroup) group).getTag().getTag());

                // In this way the dialog is created only one time
                AlertDialog dialogTag = getDialogUpdateTag(((CardGroup) group).getTag(), viewDialogTag, tagNameEditText);
                dialogTag.show();
                return true;
            });

            holder.clearTag.setOnClickListener( view -> {
                AlertDialog dialogDeleteTag = getDialogDeleteTag(((CardGroup) group).getTag());
                dialogDeleteTag.show();
            });
        }
    }

    private AlertDialog getDialogTags(View view, CardViewHolder holder) {
        return new AlertDialog.Builder(context)
                .setView(view)
                .setTitle(R.string.title_choose_tag)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        holder.updateChosenTags();
                    }
                })
                .create();
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
                            viewModel.updateTag(tag);

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
                        viewModel.deleteTag(tag);
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
                .setNegativeButton(R.string.cancel, null)
                .create();
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
        private final EditText titleEditText;
        private final EditText taboo1EditText;
        private final EditText taboo2EditText;
        private final EditText taboo3EditText;
        private final EditText taboo4EditText;
        private final EditText taboo5EditText;
        private final Button saveButton;
        private final Button tagButton;
        private final ConstraintLayout layoutCardUpdate;
        private RecyclerView recyclerView;
        private List<Tag> tagsRecycler;
        private RecyclerViewAdapterAdd recyclerViewAdapter;
        public List<Tag> chosenTags;

        public CardViewHolder(View itemView) {
            super(itemView);
            textViewCardName = itemView.findViewById(R.id.card_name);
            textViewCardName.setAutoSizeTextTypeUniformWithConfiguration(14, 18, 2, TypedValue.COMPLEX_UNIT_SP);
            clearButton = itemView.findViewById(R.id.clear_card);
            titleEditText = itemView.findViewById(R.id.update_title_edit_text);
            taboo1EditText = itemView.findViewById(R.id.update_taboo_1_edit_text);
            taboo2EditText = itemView.findViewById(R.id.update_taboo_2_edit_text);
            taboo3EditText = itemView.findViewById(R.id.update_taboo_3_edit_text);
            taboo4EditText = itemView.findViewById(R.id.update_taboo_4_edit_text);
            taboo5EditText = itemView.findViewById(R.id.update_taboo_5_edit_text);
            saveButton = itemView.findViewById(R.id.save_button);
            tagButton = itemView.findViewById(R.id.tag_button);
            layoutCardUpdate = itemView.findViewById(R.id.layout_update_card);

        }

        public void updateChosenTags() {
            chosenTags = recyclerViewAdapter.getTagListChosen();
            Toast.makeText(context, context.getResources().getString(R.string.number_of_tags_chosen_1)
                    + " " + chosenTags.size() + " "
                    + context.getResources().getString(R.string.number_of_tags_chosen_2), Toast.LENGTH_SHORT).show();
            Log.d(TAG, ">>ChosenTags: " + chosenTags);
        }

    }
}

