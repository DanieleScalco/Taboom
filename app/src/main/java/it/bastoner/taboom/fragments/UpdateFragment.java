package it.bastoner.taboom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.database.Card;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;

public class UpdateFragment extends BaseCardFragment {

    // TODO show total cards number

    public UpdateFragment(List<CardWithTags> cardList) {
        super(cardList);
    }

    @Override
    public void updateUI(List<CardWithTags> cardList, List<Tag> tagList) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update, container, false);
    }
}