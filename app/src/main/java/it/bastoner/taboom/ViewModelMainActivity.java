package it.bastoner.taboom;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.bastoner.taboom.database.CardDAO;
import it.bastoner.taboom.database.CardEntity;
import it.bastoner.taboom.database.DatabaseTaboom;

public class ViewModelMainActivity extends AndroidViewModel {

    private static final String TAG = "ViewModelMainActivity";
    private static boolean cardListIsUpdatedWithDb = false;

    private LiveData<List<CardEntity>> cardList = new MutableLiveData<>();
    private final CardDAO cardDAO;
    private final ExecutorService executor;

    public ViewModelMainActivity(@NonNull Application application) {
        super(application);

        this.cardDAO = DatabaseTaboom.getDatabase(application).cardDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<CardEntity>> getAllCards() {

        Log.d(TAG, ">>GetAllCards()");

        if (!cardListIsUpdatedWithDb) {
            cardList = cardDAO.getAllCards();
            Log.d(TAG, ">>Loaded cards from db");
            cardListIsUpdatedWithDb = true;
        }

        return cardList;

    }

    public void insertCard(CardEntity card) {

        Log.d(TAG, ">>insertCard(): " + card);
        executor.execute(() -> {
            cardDAO.insertCard(card);
            cardListIsUpdatedWithDb = false;
        });

    }

    public void shuffle(List<CardEntity> list) {
        if (list != null) {
            Collections.shuffle(list);
            cardList = new MutableLiveData<>(list);
        } else {
            Log.d(TAG, ">>List is null");
        }
    }
}
