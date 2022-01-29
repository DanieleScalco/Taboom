package it.bastoner.taboom;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.bastoner.taboom.database.CardDAO;
import it.bastoner.taboom.database.CardEntity;
import it.bastoner.taboom.database.DatabaseTaboom;
import it.bastoner.taboom.fragments.AddFragment;
import it.bastoner.taboom.fragments.BaseCardFragment;
import it.bastoner.taboom.fragments.PlayFragment;
import it.bastoner.taboom.fragments.UpdateFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private List<CardEntity> cardList;
    private boolean cardListIsUpdated;
    private ViewModelMainActivity viewModel;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, ">>MainActivity created");

        loadCardList();


        // Setting bottomNavigation
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

    }

    private void loadCardList() {

        Log.d(TAG, ">>load cardList");

        // TODO setting cards first access
        // Setting cardsList
/*
        CardEntity c1 = new CardEntity("Ventriloquo", "Parlare", "Bocca",
                                    "Muovere", "Ugola", "Labbra");
        CardEntity c2 = new CardEntity("Paperino","Qui", "Quo",
                                "Qua", "Topolino", "Fumetto");
        cardDAO.insertCard(c1);
        cardDAO.insertCard(c2);
*/

        viewModel = new ViewModelProvider(this).get(ViewModelMainActivity.class);
        viewModel.initializeDB(getApplicationContext());
        viewModel.getCardList().observe(this, cardList -> {
            this.cardList = cardList;
            // As soon as the application opens the play fragment should be shown to the user
            bottomNav.setSelectedItemId(R.id.play_nav);
        });

        Log.d(TAG, ">>CardList: " + cardList);


    }

}