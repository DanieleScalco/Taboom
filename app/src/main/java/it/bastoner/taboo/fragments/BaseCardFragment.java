package it.bastoner.taboo.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.bastoner.taboo.R;
import it.bastoner.taboo.objects.Card;

public abstract class BaseCardFragment extends Fragment {

    protected List<Card> cardList;

    public BaseCardFragment(List<Card> cardList) {
        this.cardList = cardList;
    }
}