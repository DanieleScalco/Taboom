package it.bastoner.taboom.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;

import it.bastoner.taboom.MainActivity;
import it.bastoner.taboom.R;
import it.bastoner.taboom.adapter.RecyclerViewAdapterUpdate;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;
import it.bastoner.taboom.utilities.MyCreateDocument;
import it.bastoner.taboom.utilities.Utilities;
import it.bastoner.taboom.viewModel.ViewModelMainActivity;

public class UpdateFragment extends BaseCardFragment {

    private static final String TAG = "UpdateFragment";
    private static boolean fragmentIsActive;

    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;

    ActivityResultLauncher<String> activityResultLauncherSave;
    ActivityResultLauncher<String[]> activityResultLauncherUpload;
    String fileContent = "";


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

        toolbar = getActivity().findViewById(R.id.toolbar_update);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        activityResultLauncherSave = getActivityResultLauncherSave();
        activityResultLauncherUpload = getActivityResultLauncherUpload();

    }

    private ActivityResultLauncher<String> getActivityResultLauncherSave() {
        return registerForActivityResult(
                new MyCreateDocument(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            try {
                                OutputStream outputStream = getActivity().getContentResolver().openOutputStream(result);
                                outputStream.write("Test".getBytes());
                                outputStream.flush();
                                outputStream.close();
                                Toast.makeText(getContext(), R.string.file_saved, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d(TAG, ">>Failed to save file");
                            }
                        } else {
                                Toast.makeText(getContext(), R.string.operation_canceled, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private ActivityResultLauncher<String[]> getActivityResultLauncherUpload() {
        return registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {

                            try(InputStream inputStream = getActivity().getContentResolver().openInputStream(result);
                                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {
                                bufferedReader.lines().forEach(line -> {
                                    fileContent += line;
                                });
                                Log.d(TAG, ">>:" + fileContent);
                                uploadCards();
                                inputStreamReader.close();
                                bufferedReader.close();
                                Toast.makeText(getContext(), R.string.cards_uploaded, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d(TAG, ">>Failed to open file");
                            }

                        } else {
                            Toast.makeText(getContext(), R.string.operation_canceled, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadCards() {
        // TODO
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.update_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        AlertDialog dialogInfo = getDialogInfo();

        switch (item.getItemId()) {
            case (R.id.info):
                dialogInfo.show();
                return true;
            case (R.id.save):
                activityResultLauncherSave.launch("Cards.txt");
                return true;
            case (R.id.upload):
                activityResultLauncherUpload.launch(new String[]{"text/plain"});
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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

    private AlertDialog getDialogInfo() {
        return new AlertDialog.Builder(getContext())
                .setMessage(R.string.info_message_update)
                .setTitle(R.string.info)
                .setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_baseline_info_24_red))
                .create();
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