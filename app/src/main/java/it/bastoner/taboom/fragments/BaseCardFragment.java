package it.bastoner.taboom.fragments;

import androidx.fragment.app.Fragment;

import java.util.List;

import it.bastoner.taboom.ViewModelMainActivity;
import it.bastoner.taboom.database.CardEntity;

public abstract class BaseCardFragment extends Fragment {

    protected List<CardEntity> cardList;
    protected ViewModelMainActivity viewModel;

    public BaseCardFragment(List<CardEntity> cardList) {
        this.cardList = cardList;
    }

    public abstract void updateUI(List<CardEntity> cardList);
}