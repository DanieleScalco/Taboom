package it.bastoner.taboom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
    // TODO animation bottomNav

    private static final String TAG = "MainActivity";

    private List<CardWithTags> cardList;
    private List<Tag> tagList;
    private ViewModelMainActivity viewModel;
    private BottomNavigationView bottomNav;
    private BaseCardFragment selectedFragment;
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

        loadCardList();

        setBottomNavigation();

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

            Log.d(TAG, ">>BottomNav item selected");

            // By using switch we can easily get the selected fragment by using there id.
            switch (item.getItemId()) {
                case R.id.add_nav:
                    selectedFragment = new AddFragment(cardList);
                    break;
                case R.id.play_nav:
                    selectedFragment = new PlayFragment(cardList);
                    break;
                case R.id.update_nav:
                    selectedFragment = new UpdateFragment(cardList);
                    break;
                default:
                    selectedFragment = new PlayFragment(cardList);
                    break;
            }
            // It will help to replace the
            // one fragment to other.
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (oldIdFragment != -1) {
                if (oldIdFragment == R.id.add_nav && oldIdFragment != item.getItemId()) {
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right_to_left,
                                                            R.anim.slide_out_from_right_to_left);
                } else if (oldIdFragment == R.id.update_nav && oldIdFragment != item.getItemId()) {
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left_to_right,
                                                            R.anim.slide_out_from_left_to_right);
                } else if (oldIdFragment == R.id.play_nav && item.getItemId() == R.id.add_nav) {
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left_to_right,
                                                            R.anim.slide_out_from_left_to_right);
                } else if (oldIdFragment == R.id.play_nav && item.getItemId() == R.id.update_nav){
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right_to_left,
                                                            R.anim.slide_out_from_right_to_left);
                }
            }

            fragmentTransaction.replace(R.id.fragment_container, selectedFragment)
                               .commit();

            oldIdFragment = item.getItemId();

            return true;
        });

        // As soon as the application opens the play fragment should be shown to the user
        bottomNav.setSelectedItemId(R.id.play_nav);
    }

    private void loadCardList() {

        Log.d(TAG, ">>Load cardList()");

        cardList = new ArrayList<>();
        tagList = new ArrayList<>();

        viewModel = new ViewModelProvider(this).get(ViewModelMainActivity.class);

        checkIfDatabaseAlreadyExist();

        viewModel.getAllCards().observe(this, cardListLoaded -> {

            cardList = cardListLoaded;

            Log.d(TAG, ">>Total cards found: " + cardList.size());

            // If cardList not loaded before creation of BottomNav update Fragment list
            if (bottomNav != null && selectedFragment != null) {
                selectedFragment.updateUI(cardList, tagList);
            }
        });

        viewModel.getAllTags().observe(this, tagListLoaded -> {

            tagList = tagListLoaded;

            Log.d(TAG, ">>Total tags found: " + tagList.size());

            // If cardList not loaded before creation of BottomNav update Fragment list
            if (bottomNav != null && selectedFragment != null) {
                selectedFragment.updateUI(cardList, tagList);
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

        // Remove all the old shared preferences, no need to preserve
        // data between different application starts
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}