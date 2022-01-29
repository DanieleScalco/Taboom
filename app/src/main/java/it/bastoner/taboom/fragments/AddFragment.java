package it.bastoner.taboom.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.database.CardEntity;


public class AddFragment extends BaseCardFragment {

    public AddFragment(List<CardEntity> cardList) {
        super(cardList);
    }

    @Override
    public void updateUI(List<CardEntity> cardList) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }
}