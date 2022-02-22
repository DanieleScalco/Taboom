package it.bastoner.taboom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;
import it.bastoner.taboom.fragments.AddFragment;
import it.bastoner.taboom.fragments.BaseCardFragment;
import it.bastoner.taboom.fragments.PlayFragment;
import it.bastoner.taboom.fragments.UpdateFragment;
import it.bastoner.taboom.utilities.Utilities;
import it.bastoner.taboom.viewModel.ViewModelMainActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static boolean appJustOpened;
    public static List<CardWithTags> recyclerCardList; // Cards in play
    public static boolean recyclerCardIsChanged = true;
    public static List<Tag> recyclerTagList = new ArrayList<>(); // Tags chosen by user

    private List<CardWithTags> cardList;
    private List<Tag> tagList;
    private ViewModelMainActivity viewModel;
    private BottomNavigationView bottomNav;
    private int oldIdFragment = -1;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Log.d(TAG, ">>OnCreate()");

        // No night mode support
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // Need cause if user exits application isn't closed for real
        appJustOpened = true;
        PlayFragment.appJustOpened = true;

        deleteOldData();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Utilities.SHOULD_SHUFFLE, true);
        editor.commit();

        setBottomNavigation();

        firstLoadCardList();


    }

    @Override
    public void onBackPressed() {

        //Log.d(TAG, ">>OnBackPressed()");

        // Remove focus from editText
        if (bottomNav.getSelectedItemId() == R.id.play_nav) {
            View teamA = findViewById(R.id.team_a_name);
            View teamB = findViewById(R.id.team_b_name);
            if (teamA.hasFocus() || teamB.hasFocus()) {
                teamA.clearFocus();
                teamB.clearFocus();
                return;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.warning)
                .setMessage(R.string.want_to_exit)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void setBottomNavigation() {

        bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {

            int idItem = item.getItemId();


            if (idItem == oldIdFragment) {
                Log.d(TAG, ">>BottomNav item reselected: " + getResources().getResourceEntryName(idItem));
                return true;
            } else
                Log.d(TAG, ">>BottomNav item selected: " + getResources().getResourceEntryName(idItem));

            // By using switch we can easily get the selected fragment by using the id.
            BaseCardFragment selectedFragment;
            switch (idItem) {
                case R.id.add_nav:
                    selectedFragment = new AddFragment(cardList, tagList);
                    break;
                case R.id.play_nav:
                    selectedFragment = new PlayFragment(cardList, tagList);
                    break;
                case R.id.update_nav:
                    selectedFragment = new UpdateFragment(cardList, tagList);
                    break;
                default:
                    selectedFragment = new PlayFragment(cardList, tagList);
                    break;
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            // It will help to replace the
            // one fragment to other.

            if (oldIdFragment != -1) {
                if (oldIdFragment == R.id.add_nav && oldIdFragment != idItem) {
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right_to_left,
                                                            R.anim.slide_out_from_right_to_left);
                } else if (oldIdFragment == R.id.update_nav && oldIdFragment != idItem) {
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left_to_right,
                                                            R.anim.slide_out_from_left_to_right);
                } else if (oldIdFragment == R.id.play_nav && idItem == R.id.add_nav) {
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left_to_right,
                                                            R.anim.slide_out_from_left_to_right);
                } else if (oldIdFragment == R.id.play_nav && idItem == R.id.update_nav){
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right_to_left,
                                                            R.anim.slide_out_from_right_to_left);
                }
            }

            fragmentTransaction.replace(R.id.fragment_container, selectedFragment)
                               .commit();

            oldIdFragment = idItem;

            return true;
        });

    }

    private void firstLoadCardList() {

        //Log.d(TAG, ">>FirstLoadCardList()");

        ConstraintLayout fragmentContainer = findViewById(R.id.fragment_container);
        ConstraintLayout progressBarContainer = findViewById(R.id.loading_container);
        fragmentContainer.setVisibility(View.GONE);
        progressBarContainer.setVisibility(View.VISIBLE);

        viewModel = new ViewModelProvider(this).get(ViewModelMainActivity.class);

        viewModel.getAllCards().observe(this, cardListLoaded -> {

            cardList = cardListLoaded;

            Log.d(TAG, ">>Total cards found: " + cardList.size());

            if (tagList != null && appJustOpened) {
                appJustOpened = false;
                progressBarContainer.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
                bottomNav.setSelectedItemId(R.id.play_nav);
            }
        });

        viewModel.getAllTags().observe(this, tagListLoaded -> {

            tagList = tagListLoaded;
            Log.d(TAG, ">>Total tags found: " + tagList.size());

            // If tagList not loaded before creation of BottomNav update Fragment list
            // and cardList already loaded
            if (cardList!= null && appJustOpened) {
                appJustOpened = false;
                progressBarContainer.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
                bottomNav.setSelectedItemId(R.id.play_nav);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d(TAG, ">>OnPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d(TAG, ">>OnStop()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d(TAG, ">>OnResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, ">>OnDestroy()");

        // Need cause if user exits application isn't closed for real
        viewModel.reloadAll();
    }

    private void deleteOldData() {

        Log.d(TAG, ">>DeleteOldData()");

        // Remove all the old shared preferences, no need to preserve
        // data between different application starts
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}