package it.bastoner.taboom.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.viewModel.ViewModelMainActivity;
import it.bastoner.taboom.database.Card;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;

public class RecyclerViewAdapterUpdate extends RecyclerView.Adapter<RecyclerViewAdapterUpdate.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapterUpdate";

    private List<CardWithTags> cardList;
    private List<Tag> tagList;
    private Context context;
    private ViewModelMainActivity viewModelFragment;


    public RecyclerViewAdapterUpdate(List<CardWithTags> cardList, List<Tag> tagList, Context context,
                                     ViewModelMainActivity viewModelFragment) {
        this.cardList = cardList;
        this.tagList = tagList;
        this.context = context;
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

            // All the cards
            holder.numberOfItems.setText(String.valueOf(cardList.size()));
            holder.tagName.setText(R.string.all_cards_tag);
            holder.clearTag.setVisibility(View.GONE);
            initializeLayoutCards(cardList,null,  holder, true);

        } else {

            // Single TAG
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

            View viewDialogTag = LayoutInflater.from(context).inflate(R.layout.dialog_modify_tag, (ViewGroup) null);
            EditText tagNameEditText = viewDialogTag.findViewById(R.id.tag_name_dialog);
            tagNameEditText.setText(tag.getTag());

            // In this way the dialog is created only one time
            AlertDialog dialogTag = getDialogUpdateTag(tag, viewDialogTag, tagNameEditText);
            AlertDialog dialogDeleteTag = getDialogDeleteTag(tag);

            holder.tagName.setOnLongClickListener( view -> {
                dialogTag.show();
                return true;
            });

            holder.clearTag.setOnClickListener( view -> dialogDeleteTag.show());

        }



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
                            tag.setTag(newTag);
                            viewModelFragment.updateTag(tag);
                            Log.d(TAG, ">>Update TAG: " + tag.getTag() + "->" + newTag);
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
                        Log.d(TAG, ">>Delete card: " + card.getTitle());
                    }
                })
                .setNegativeButton(R.string.remove_tag, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO
                        viewModelFragment.removeTagFromCard(card, tag);
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
            textViewCardName.setText(card.getTitle());

            textViewCardName.setOnClickListener(view -> {
                //TODO update card
            });

            AlertDialog dialogDeleteCard;
            if (!isAllCards)
                dialogDeleteCard = getDialogDeleteCardOrTag(card, tag);
            else
                dialogDeleteCard = getDialogDeleteCard(card);

            clearButton.setOnClickListener(view -> dialogDeleteCard.show());

            // Set the click on both number of items and tag name
            holder.tagName.setOnClickListener(view -> {
                openCloseCardList(holder);
            });
            holder.numberOfItems.setOnClickListener(view -> {
                openCloseCardList(holder);
            });

            holder.linearLayout.addView(cardView);

        }

    }

    private void openCloseCardList(ViewHolder holder) {

        if (!holder.isOpen) {

            holder.linearLayout.setVisibility(View.VISIBLE);

            AnimationSet animation = new AnimationSet(true);
            Animation animationAlpha = new AlphaAnimation(0, 1);
            animation.addAnimation(animationAlpha);
            Animation animationTranslate = new TranslateAnimation(0, 0, -holder.linearLayout.getHeight(), 0);
            animation.addAnimation(animationTranslate);
            Animation animationY = new ScaleAnimation(1, 1, 0, 1);
            animationY.setInterpolator(new LinearInterpolator());
            animation.addAnimation(animationY);
            animation.setDuration(250);

            holder.linearLayout.startAnimation(animation);
            holder.isOpen = true;

        } else {

            AnimationSet animation = new AnimationSet(true);
            Animation animationAlpha = new AlphaAnimation(1, 0);
            animation.addAnimation(animationAlpha);
            Animation animationTranslate = new TranslateAnimation(0, 0, 0, -holder.linearLayout.getHeight());
            animation.addAnimation(animationTranslate);
            Animation animationY = new ScaleAnimation(1, 1, 1, 0);
            animationY.setInterpolator(new LinearInterpolator());
            animation.addAnimation(animationY);
            animation.setDuration(250);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    holder.linearLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            holder.linearLayout.startAnimation(animation);
            holder.isOpen = false;
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
        private boolean isOpen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            numberOfItems = itemView.findViewById(R.id.number_of_items);
            tagName = itemView.findViewById(R.id.tag_name);
            clearTag = itemView.findViewById(R.id.clear_tag);
            checkBox = itemView.findViewById(R.id.check_box);
            linearLayout = itemView.findViewById(R.id.layout_cards);
            isOpen = false;

        }

    }

    public void setCardList(List<CardWithTags> cardList) {
        this.cardList = cardList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }
}
