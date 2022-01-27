package it.bastoner.taboo;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import it.bastoner.taboo.fragments.AddFragment;
import it.bastoner.taboo.fragments.PlayFragment;
import it.bastoner.taboo.fragments.UpdateFragment;
import it.bastoner.taboo.objects.Card;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private List<Card> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadCardList();

        // Setting bottomNavigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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

        // as soon as the application opens the play fragment should be shown to the user
        bottomNav.setSelectedItemId(R.id.play_nav);
        // To change fragment
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlayFragment()).commit();

    }

    private void loadCardList() {
        // Setting cardsList
        cardList = new ArrayList<Card>();
        cardList.add(new Card("Ventriloquo", new String[]{"Parlare", "Bocca", "Muovere", "Ugola", "Labbra"}));
        cardList.add(new Card("Paperino", new String[]{"Qui", "Quo", "Qua", "Topolino", "Fumetto"}));
        Log.d(TAG, "Lista creata");
    }

}