package it.bastoner.taboom.fragments;

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

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.ViewModelMainActivity;
import it.bastoner.taboom.animations.Animations;
import it.bastoner.taboom.database.CardEntity;


public class AddFragment extends BaseCardFragment {

    // TODO fixed lenght of editTexts

    private static final String TAG = "AddFragment";

    private EditText titleEditText;
    private EditText taboo1EditText;
    private EditText taboo2EditText;
    private EditText taboo3EditText;
    private EditText taboo4EditText;
    private EditText taboo5EditText;
    private EditText listNameEditText;

    private Button addCardButton;
    private Button clearButton;

    private MediaPlayer clearSound;

    public AddFragment(List<CardEntity> cardList) {
        super(cardList);
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

        View cardIncluded = getActivity().findViewById(R.id.card_included);
        titleEditText = (EditText) cardIncluded.findViewById(R.id.title);
        taboo1EditText = (EditText) cardIncluded.findViewById(R.id.taboo_word_1);
        taboo2EditText = (EditText) cardIncluded.findViewById(R.id.taboo_word_2);
        taboo3EditText = (EditText) cardIncluded.findViewById(R.id.taboo_word_3);
        taboo4EditText = (EditText) cardIncluded.findViewById(R.id.taboo_word_4);
        taboo5EditText = (EditText) cardIncluded.findViewById(R.id.taboo_word_5);
        listNameEditText = getActivity().findViewById(R.id.list_name);
        addCardButton = getActivity().findViewById(R.id.add_card);
        clearButton = getActivity().findViewById(R.id.clear);

        titleEditText.setHint(R.string.default_title_card);
        taboo1EditText.setHint(R.string.default_taboo);
        taboo2EditText.setHint(R.string.default_taboo);
        taboo3EditText.setHint(R.string.default_taboo);
        taboo4EditText.setHint(R.string.default_taboo);
        taboo5EditText.setHint(R.string.default_taboo);
        listNameEditText.setHint(R.string.default_list_name);

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
    }

    private void setViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModelMainActivity.class);
        viewModel.getAllCards().observe(getActivity(), cardList -> {
            this.cardList = cardList;
            Log.d(TAG, ">>List initialized. Number of items: " + cardList.size());
            updateUI(cardList);
        });
    }

    @Override
    public void updateUI(List<CardEntity> cardList) {

       // Nothing to do here
    }

    private void addCard() {
        CardEntity card = new CardEntity(titleEditText.getText().toString(),
                                        taboo1EditText.getText().toString(),
                                        taboo2EditText.getText().toString(),
                                        taboo3EditText.getText().toString(),
                                        taboo4EditText.getText().toString(),
                                        taboo5EditText.getText().toString(),
                                        listNameEditText.getText().toString());
        Log.d(TAG, ">>Card: " + card);

        if (card.getTitle() == null || card.getTitle().isEmpty()) {
            Toast.makeText(getContext(), R.string.card_title_needed, Toast.LENGTH_SHORT).show();
        } else {

            if (cardAlreadyExists(card)) {
                Toast.makeText(getContext(), "La carta \"" + titleEditText.getText().toString()
                                + "\" "+ getResources().getString(R.string.card_already_exists),
                                Toast.LENGTH_LONG).show();
            } else {

                viewModel.insertCard(card);
                Toast.makeText(getContext(), R.string.card_added, Toast.LENGTH_LONG).show();

                resetView();
            }

        }
    }

    private boolean cardAlreadyExists(CardEntity card) {

        for (CardEntity c : cardList) {
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
}
