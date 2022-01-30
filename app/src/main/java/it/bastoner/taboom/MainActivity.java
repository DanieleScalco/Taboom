package it.bastoner.taboom;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.util.List;

import it.bastoner.taboom.database.CardEntity;
import it.bastoner.taboom.database.DatabaseTaboom;
import it.bastoner.taboom.fragments.AddFragment;
import it.bastoner.taboom.fragments.PlayFragment;
import it.bastoner.taboom.fragments.UpdateFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private List<CardEntity> cardList;
    private boolean cardListIsUpdated;
    private ViewModelMainActivity viewModel;
    private BottomNavigationView bottomNav;

    // TODO add sounds to buttons
    // TODO add labels tu clickable objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, ">>MainActivity created()");

        loadCardList();

        setBottomNavigation();

    }

    private void setBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            //TODO ANIMATION

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Log.d(TAG, ">>BottomNav item selected");

                // By using switch we can easily get the selected fragment by using there id.
                Fragment selectedFragment = null;
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
                }
                // It will help to replace the
                // one fragment to other.
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

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

}