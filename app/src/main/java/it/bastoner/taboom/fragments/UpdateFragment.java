package it.bastoner.taboom.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.Utilities;
import it.bastoner.taboom.ViewModelMainActivity;
import it.bastoner.taboom.adapter.RecyclerViewAdapter;
import it.bastoner.taboom.adapter.RecyclerViewAdapterUpdate;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;

public class UpdateFragment extends BaseCardFragment {

    // TODO show total cards number

    private static final String TAG = "UpdateFragment";

    private RecyclerView recyclerView;

    public UpdateFragment(List<CardWithTags> cardList, List<Tag> tagList) {
        super(cardList, tagList);
    }

    @Override
    public void updateUI(List<CardWithTags> cardList, List<Tag> tagList) {

        // TODO
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

        setViewModel();

        setRecyclerView();
    }

    private void setViewModel() {

        Log.d(TAG, ">>SetViewModel");

        viewModel = new ViewModelProvider(this).get(ViewModelMainActivity.class);
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

        recyclerView = getView().findViewById(R.id.recycler_view_list);

        RecyclerViewAdapterUpdate recyclerViewAdapter = new RecyclerViewAdapterUpdate(cardList, tagList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        /* Set the helper to make recycler view not to show half items
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);*/

        recyclerView.setAdapter(recyclerViewAdapter);
/*
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View actualView = snapHelper.findSnapView(layoutManager);

                    // Check if there are no cards
                    if (actualView != null) {
                        positionRecyclerCard = layoutManager.getPosition(actualView);
                        Log.d(TAG, ">>RecyclerPosition: " + positionRecyclerCard);
                    }
                }
            }

        });

        positionRecyclerCard = sharedPreferences.getInt(Utilities.RECYCLER_CARD_POSITION, 0);
        layoutManager.scrollToPosition(positionRecyclerCard);*/

    }
}