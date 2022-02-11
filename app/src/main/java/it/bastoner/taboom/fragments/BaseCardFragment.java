package it.bastoner.taboom.fragments;

import androidx.fragment.app.Fragment;

import java.util.List;

import it.bastoner.taboom.viewModel.ViewModelMainActivity;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;

public abstract class BaseCardFragment extends Fragment {

    protected List<CardWithTags> cardList;
    protected List<Tag> tagList;
    protected ViewModelMainActivity viewModel;

    public BaseCardFragment(List<CardWithTags> cardList, List<Tag> tagList) {
        this.cardList = cardList;
        this.tagList = tagList;
    }

}