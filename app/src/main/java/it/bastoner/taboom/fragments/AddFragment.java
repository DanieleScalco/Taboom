package it.bastoner.taboom.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import it.bastoner.taboom.adapter.RecyclerViewAdapterUpdate;
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

    private RecyclerView recyclerView;

    private MediaPlayer clearSound;

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
            Animations.doReduceIncreaseAnimation(view);
            addCard();
        });

        clearButton.setOnClickListener(view -> {
            clearSound.start();
            Animations.doReduceIncreaseAnimation(view);
            resetView();
        });

        addTags.setOnClickListener(view -> {
            View dialogTags = LayoutInflater.from(getContext()).inflate(R.layout.dialog_tags, null);
            setRecyclerViewDialog(dialogTags);
            getDialogTags(dialogTags).show();
        });

    }

    private void setRecyclerViewDialog(View dialogTags) {

        recyclerView = dialogTags.findViewById(R.id.recycler_view_tags);
        RecyclerViewAdapterAdd recyclerViewAdapter = new RecyclerViewAdapterAdd(tagList);
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
        Log.d(TAG, ">>Card: " + card);

        List<Tag> tagList = new ArrayList<>();

        CardWithTags cardWithTags = new CardWithTags(card, tagList);

        if (card.getTitle() == null || card.getTitle().isEmpty()) {
            Toast.makeText(getContext(), R.string.card_title_needed, Toast.LENGTH_SHORT).show();
        } else {

            if (cardAlreadyExists(card)) {
                Toast.makeText(getContext(), "La carta \"" + titleEditText.getText().toString()
                                + "\" "+ getResources().getString(R.string.card_already_exists),
                                Toast.LENGTH_LONG).show();
            } else {

                viewModel.insertCard(cardWithTags);
                Toast.makeText(getContext(), R.string.card_added, Toast.LENGTH_LONG).show();

                resetView();
            }

        }
    }

    private boolean cardAlreadyExists(Card card) {

        for (Card c : Utilities.getCards(cardList)) {
            if (c.getTitle().equalsIgnoreCase(card.getTitle()))
                return true;
        }
        return false;

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

                        setTags(tagList);
                    }
                })
                .setNegativeButton(R.string.button_add_tag, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }

    private void setTags(List<Tag> tagList) {
        // TODO
    }
}
