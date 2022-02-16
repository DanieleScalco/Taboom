package it.bastoner.taboom.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.adapter.RecyclerViewAdapterAdd;
import it.bastoner.taboom.animations.Animations;
import it.bastoner.taboom.database.Card;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;
import it.bastoner.taboom.utilities.Utilities;
import it.bastoner.taboom.viewModel.ViewModelMainActivity;


public class AddFragment extends BaseCardFragment {

    private static final String TAG = "AddFragment";

    private EditText titleEditText;
    private EditText taboo1EditText;
    private EditText taboo2EditText;
    private EditText taboo3EditText;
    private EditText taboo4EditText;
    private EditText taboo5EditText;

    private Button addCardButton;
    private Button clearButton;
    private Button addTags;

    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private RecyclerViewAdapterAdd recyclerViewAdapter;

    private Button newTagButton;
    private EditText newTagEditText;

    private MediaPlayer clearSound;

    private List<Tag> chosenTags = new ArrayList<>();
    private List<Tag> tagsRecycler = new ArrayList<>();

    public AddFragment(List<CardWithTags> cardList, List<Tag> tagList) {
        super(cardList, tagList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, ">>OnCreateView()");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, ">>OnViewCreated()");

        sharedPreferences = getActivity()
                .getSharedPreferences(Utilities.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        setViewModel();

        setView();

    }

    private void setView() {

        Log.d(TAG, ">>SetView()");

        View cardIncluded = getActivity().findViewById(R.id.card_included);
        titleEditText = cardIncluded.findViewById(R.id.title);
        taboo1EditText = cardIncluded.findViewById(R.id.taboo_word_1);
        taboo2EditText = cardIncluded.findViewById(R.id.taboo_word_2);
        taboo3EditText = cardIncluded.findViewById(R.id.taboo_word_3);
        taboo4EditText = cardIncluded.findViewById(R.id.taboo_word_4);
        taboo5EditText = cardIncluded.findViewById(R.id.taboo_word_5);
        addCardButton = getActivity().findViewById(R.id.add_card);
        clearButton = getActivity().findViewById(R.id.clear);
        addTags = getActivity().findViewById(R.id.add_tag);

        titleEditText.setHint(R.string.default_title_card);
        taboo1EditText.setHint(R.string.default_taboo);
        taboo2EditText.setHint(R.string.default_taboo);
        taboo3EditText.setHint(R.string.default_taboo);
        taboo4EditText.setHint(R.string.default_taboo);
        taboo5EditText.setHint(R.string.default_taboo);

        clearSound = MediaPlayer.create(getContext(), R.raw.clear_button);

        addCardButton.setOnClickListener(view -> {
            Animations.doReduceIncreaseAnimation(view, null);
            addCard();
        });

        clearButton.setOnClickListener(view -> {
            clearSound.start();
            Animations.doReduceIncreaseAnimation(view, null);
            resetView();
        });

        View dialogTags = LayoutInflater.from(getContext()).inflate(R.layout.dialog_tags, null);
        setRecyclerViewDialog(dialogTags);

        newTagButton = dialogTags.findViewById(R.id.new_tag_button_dialog);
        newTagEditText = dialogTags.findViewById(R.id.insert_new_tag);
        newTagButton.setOnClickListener(view -> {

            Animations.doReduceIncreaseAnimation(view, null);
            addTag();
        });

        AlertDialog dialog = getDialogTags(dialogTags);

        addTags.setOnClickListener(view -> {
            dialog.show();
            Animations.doReduceIncreaseAnimation(view, null);
            Log.d(TAG, ">>Show dialog");
        });

    }

    private void addTag() {
        Tag newTag = new Tag(newTagEditText.getText().toString());

        if (newTag.getTag().isEmpty()) {
            Toast.makeText(getContext(), R.string.tag_name_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (newTag.getTag().equalsIgnoreCase(getResources().getString(R.string.all_cards_tag))) {
            Toast.makeText(getContext(), R.string.invalid_tag_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utilities.tagAlreadyExists(newTag, tagsRecycler)) {
            Toast.makeText(getContext(), getResources().getString(R.string.tag_already_exist)
                            + " \"" + newTag.getTag()
                            + "\" "+ getResources().getString(R.string.tag_already_exists_2),
                    Toast.LENGTH_LONG).show();
        } else {
            tagsRecycler.add(newTag);
            chosenTags.add(newTag);
            recyclerViewAdapter.setTagList(tagsRecycler);
            recyclerViewAdapter.setTagListChosen(chosenTags);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }


    private void setRecyclerViewDialog(View dialogTags) {

        recyclerView = dialogTags.findViewById(R.id.recycler_view_tags);
        tagsRecycler = new ArrayList<>(tagList);
        recyclerViewAdapter = new RecyclerViewAdapterAdd(tagsRecycler, chosenTags);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void setViewModel() {

        Log.d(TAG, ">>SetViewModel()");

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelMainActivity.class);
        viewModel.getAllCards().observe(requireActivity(), cardListLoaded -> {
            cardList = cardListLoaded;
            Log.d(TAG, ">>CardList updated:" + cardList);
            //updateUI(cardList, tagList);
        });

        viewModel.getAllTags().observe(requireActivity(), tagListLoaded -> {
            tagList = tagListLoaded;
            Log.d(TAG, ">>TagList updated:" + tagList);
            //updateUI(cardList, tagList);
        });

    }

    private void addCard() {

        Card card = new Card(titleEditText.getText().toString(),
                            taboo1EditText.getText().toString(),
                            taboo2EditText.getText().toString(),
                            taboo3EditText.getText().toString(),
                            taboo4EditText.getText().toString(),
                            taboo5EditText.getText().toString());

        CardWithTags cardWithTags = new CardWithTags(card, chosenTags);
        Log.d(TAG, ">>Card: " + card);
        Log.d(TAG, ">>Tags: " + chosenTags);

        if (card.getTitle() == null || card.getTitle().isEmpty()) {
            Toast.makeText(getContext(), R.string.card_title_needed, Toast.LENGTH_SHORT).show();
        } else {

            if (Utilities.cardAlreadyExists(card, cardList)) {
                Toast.makeText(getContext(), getResources().getString(R.string.card_already_exists_1) + " \"" + titleEditText.getText().toString()
                                + "\" "+ getResources().getString(R.string.card_already_exists_2),
                                Toast.LENGTH_LONG).show();
            } else {

                viewModel.insertCard(cardWithTags);
                Toast.makeText(getContext(), R.string.card_added, Toast.LENGTH_LONG).show();
                resetView();

                // Reset position of cardList in PlayFragment
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);
                editor.putInt(Utilities.RECYCLER_CARD_POSITION, 0);
                editor.commit();
            }

        }
    }

    private void resetView() {

        titleEditText.setHint(R.string.default_title_card);
        taboo1EditText.setHint(R.string.default_taboo);
        taboo2EditText.setHint(R.string.default_taboo);
        taboo3EditText.setHint(R.string.default_taboo);
        taboo4EditText.setHint(R.string.default_taboo);
        taboo5EditText.setHint(R.string.default_taboo);

        String emptyString = "";
        titleEditText.setText(emptyString);
        taboo1EditText.setText(emptyString);
        taboo2EditText.setText(emptyString);
        taboo3EditText.setText(emptyString);
        taboo4EditText.setText(emptyString);
        taboo5EditText.setText(emptyString);

    }

    private AlertDialog getDialogTags(View view) {
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle(R.string.title_choose_tag)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        chosenTags = recyclerViewAdapter.getTagListChosen();
                        Toast.makeText(getContext(), getResources().getString(R.string.number_of_tags_chosen_1)
                                + " " + chosenTags.size() + " "
                                + getResources().getString(R.string.number_of_tags_chosen_2), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, ">>ChosenTags: " + chosenTags);
                    }
                })
                .create();
    }

}
