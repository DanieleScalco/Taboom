package it.bastoner.taboom.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.objects.Card;

public class UpdateFragment extends BaseCardFragment {

    public UpdateFragment(List<Card> cardList) {
        super(cardList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update, container, false);
    }
}