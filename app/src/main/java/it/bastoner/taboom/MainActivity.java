package it.bastoner.taboom;

import android.content.Context;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.bastoner.taboom.database.Card;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.DatabaseTaboom;
import it.bastoner.taboom.database.Tag;
import it.bastoner.taboom.fragments.AddFragment;
import it.bastoner.taboom.fragments.BaseCardFragment;
import it.bastoner.taboom.fragments.PlayFragment;
import it.bastoner.taboom.fragments.UpdateFragment;

public class MainActivity extends AppCompatActivity {

    // TODO add font
    // TODO check for max listSize;

    private static final String TAG = "MainActivity";

    private List<CardWithTags> cardList;
    private List<Tag> tagList;
    private ViewModelMainActivity viewModel;
    private BottomNavigationView bottomNav;
    private long oldIdFragment = -1;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, ">>MainActivity created()");

        // No night mode support
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        deleteOldDatas();

        setBottomNavigation();

        firstLoadCardList();


    }

    @Override
    public void onBackPressed() {

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
        super.onBackPressed();
    }

    private void setBottomNavigation() {

        bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {

            int idItem = item.getItemId();

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

        ConstraintLayout fragmentContainer = findViewById(R.id.fragment_container);
        ConstraintLayout progressBarContainer = findViewById(R.id.loading_container);
        fragmentContainer.setVisibility(View.GONE);
        progressBarContainer.setVisibility(View.VISIBLE);

        Log.d(TAG, ">>FirstLoadCardList()");

        viewModel = new ViewModelProvider(this).get(ViewModelMainActivity.class);

        checkIfDatabaseAlreadyExist();

        viewModel.getAllCards().observe(this, cardListLoaded -> {

            cardList = cardListLoaded;
            Log.d(TAG, ">>Total cards found: " + cardList.size());

            if (tagList != null) {
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
            if (cardList!= null) {
                progressBarContainer.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
                bottomNav.setSelectedItemId(R.id.play_nav);
            }
        });

    }

    private void checkIfDatabaseAlreadyExist() {
        File databaseFile = new File(getDatabasePath(DatabaseTaboom.DATABASE_NAME).getAbsolutePath());
        if (!databaseFile.exists())
            createDatabase();
        else
            Log.d(TAG, ">>Database found");
    }

    private void createDatabase() {

        Card c1 = new Card("Ventriloquo", "Parlare", "Bocca",
                "Muovere", "Ugola", "Labbra");
        Card c2 = new Card("Paperino","Qui", "Quo",
                "Qua", "Topolino", "Fumetto");

        Tag tag = new Tag(getString(R.string.default_tag));
        List<Tag> tagList = new ArrayList<>();
        tagList.add(tag);

        CardWithTags card1 = new CardWithTags(c1, tagList);
        CardWithTags card2 = new CardWithTags(c2, tagList);

        viewModel.insertCard(card1);
        viewModel.insertCard(card2);

        Log.d(TAG, ">>Database created");
    }

    private void deleteOldDatas() {

        Log.d(TAG, ">>DeleteOldDatas()");

        // Remove all the old shared preferences, no need to preserve
        // data between different application starts
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}