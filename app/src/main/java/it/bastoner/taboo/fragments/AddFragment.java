package it.bastoner.taboo.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.bastoner.taboo.R;
import it.bastoner.taboo.objects.Card;


public class AddFragment extends BaseCardFragment {

    public AddFragment(List<Card> cardList) {
        super(cardList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }
}