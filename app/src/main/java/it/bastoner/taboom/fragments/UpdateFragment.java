package it.bastoner.taboom.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.HashSet;
import java.util.List;

import it.bastoner.taboom.MainActivity;
import it.bastoner.taboom.R;
import it.bastoner.taboom.utilities.Utilities;
import it.bastoner.taboom.viewModel.ViewModelMainActivity;
import it.bastoner.taboom.adapter.RecyclerViewAdapterUpdate;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;

public class UpdateFragment extends BaseCardFragment {

    private static final String TAG = "UpdateFragment";
    private static boolean fragmentIsActive;

    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    public UpdateFragment(List<CardWithTags> cardList, List<Tag> tagList) {
        super(cardList, tagList);
    }

    public void updateUI(List<CardWithTags> cardListWithTags, List<Tag> tagList) {

        RecyclerViewAdapterUpdate adapter = (RecyclerViewAdapterUpdate) recyclerView.getAdapter();

        if (adapter != null && cardListWithTags != null && tagList != null) {

            Log.d(TAG, ">>Update, cards: " + cardListWithTags);
            Log.d(TAG, ">>Update, tags: " + tagList);
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

        sharedPreferences = getActivity()
                .getSharedPreferences(Utilities.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        setRecyclerView();

        setViewModel();

    }

    private void setViewModel() {

        Log.d(TAG, ">>SetViewModel()");

        fragmentIsActive = true;

        viewModel.getAllCards().observe(requireActivity(), cardListLoaded -> {
            if (fragmentIsActive) {
                cardList = cardListLoaded;
                Log.d(TAG, ">>CardList updated:" + cardList);
                updateUI(cardList, tagList);
            }
        });

        viewModel.getAllTags().observe(requireActivity(), tagListLoaded -> {
            if (fragmentIsActive) {
                tagList = tagListLoaded;
                Log.d(TAG, ">>TagList updated:" + tagList);
                updateUI(cardList, tagList);
            }
        });

    }

    private void setRecyclerView() {

        Log.d(TAG, ">>SetRecyclerView()");
        recyclerView = getView().findViewById(R.id.recycler_view_list);

        RecyclerViewAdapterUpdate recyclerViewAdapter = new RecyclerViewAdapterUpdate(cardList, tagList,
                                                                getContext(), viewModel, sharedPreferences);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                                                            RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "OnDestroy()");

        fragmentIsActive = false;
        Log.d(TAG, ">>SelectedTags: " + sharedPreferences.getStringSet(Utilities.SELECTED_TAGS, new HashSet<>()));
        Log.d(TAG, ">>MainActivityRecyclerTagList: " + MainActivity.recyclerTagList);

    }
}