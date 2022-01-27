package it.bastoner.taboom.fragments;

import androidx.fragment.app.Fragment;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.objects.Card;

public abstract class BaseCardFragment extends Fragment {

    protected List<Card> cardList;

    public BaseCardFragment(List<Card> cardList) {
        this.cardList = cardList;
    }
}