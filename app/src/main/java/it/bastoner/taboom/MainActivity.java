package it.bastoner.taboom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.List;

import it.bastoner.taboom.database.CardEntity;
import it.bastoner.taboom.database.DatabaseTaboom;
import it.bastoner.taboom.fragments.AddFragment;
import it.bastoner.taboom.fragments.BaseCardFragment;
import it.bastoner.taboom.fragments.PlayFragment;
import it.bastoner.taboom.fragments.UpdateFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private List<CardEntity> cardList;
    private ViewModelMainActivity viewModel;
    private BottomNavigationView bottomNav;
    private BaseCardFragment selectedFragment;

    private SharedPreferences sharedPreferences;

    // TODO add sounds to buttons
    // TODO add labels to clickable objects
    // TODO check for system text size

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, ">>MainActivity created()");

        // No night mode support
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

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

        //TODO ANIMATION

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
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        });

        // As soon as the application opens the play fragment should be shown to the user
        bottomNav.setSelectedItemId(R.id.play_nav);
    }

    private void loadCardList() {

        Log.d(TAG, ">>Load cardList()");

        viewModel = new ViewModelProvider(this).get(ViewModelMainActivity.class);

        checkIfDatabaseAlreadyExist();

        viewModel.getAllCards().observe(this, cardList -> {

            this.cardList = cardList;

            Log.d(TAG, ">>Total cards found: " + cardList.size());

            // If cardList not loaded before creation of BottoNav update Fragment list
            if (bottomNav != null && selectedFragment != null) {
                selectedFragment.updateUI(cardList);
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
        CardEntity c1 = new CardEntity("Ventriloquo", "Parlare", "Bocca",
                "Muovere", "Ugola", "Labbra", null);
        CardEntity c2 = new CardEntity("Paperino","Qui", "Quo",
                "Qua", "Topolino", "Fumetto", null);

        viewModel.insertCard(c1);
        viewModel.insertCard(c2);

        Log.d(TAG, ">>Database created");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // When activity got destroy remove all the shared preferences, no need to preserve
        // data between different application start
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }
}