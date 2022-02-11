package it.bastoner.taboom.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.ViewModelMainActivity;
import it.bastoner.taboom.adapter.RecyclerViewAdapterUpdate;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;

// TODO in updateUI a card less is logged from the list
public class UpdateFragment extends BaseCardFragment {

    private static final String TAG = "UpdateFragment";

    private RecyclerView recyclerView;

    public UpdateFragment(List<CardWithTags> cardList, List<Tag> tagList) {
        super(cardList, tagList);
    }

    public void updateUI(List<CardWithTags> cardListWithTags, List<Tag> tagList) {

        RecyclerViewAdapterUpdate adapter = (RecyclerViewAdapterUpdate) recyclerView.getAdapter();

        if (adapter != null && cardListWithTags != null && tagList != null) {

            adapter.setCardList(cardListWithTags);
            adapter.setTagList(tagList);
            adapter.notifyDataSetChanged();
            Log.d(TAG, ">>Update, Total cards: " + cardListWithTags.size());
            Log.d(TAG, ">>Update, Total tags: " + tagList.size());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelMainActivity.class);

        setRecyclerView();

        setViewModel();

    }

    private void setViewModel() {

        Log.d(TAG, ">>SetViewModel()");

        viewModel.getAllCards().observe(getActivity(), cardListLoaded -> {
            cardList = cardListLoaded;
            Log.d(TAG, ">>CardList updated:" + cardList);
            updateUI(cardList, tagList);
        });

        viewModel.getAllTags().observe(getActivity(), tagListLoaded -> {
            tagList = tagListLoaded;
            Log.d(TAG, ">>TagList updated:" + tagList);
            updateUI(cardList, tagList);
        });

    }

    private void setRecyclerView() {

        Log.d(TAG, ">>SetRecyclerView()");
        recyclerView = getView().findViewById(R.id.recycler_view_list);

        RecyclerViewAdapterUpdate recyclerViewAdapter = new RecyclerViewAdapterUpdate(cardList, tagList,
                                                                getContext(), viewModel);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                                                            RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(recyclerViewAdapter);

    }

}