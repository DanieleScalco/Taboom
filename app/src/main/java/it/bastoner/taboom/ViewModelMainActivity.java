package it.bastoner.taboom;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.bastoner.taboom.database.CardDAO;
import it.bastoner.taboom.database.CardEntity;
import it.bastoner.taboom.database.DatabaseTaboom;

public class ViewModelMainActivity extends ViewModel {

    private static final String TAG = "ViewModelMainActivity";
    private LiveData<List<CardEntity>> cardList = new MutableLiveData<>();
    private CardDAO cardDAO;

    public void initializeDB(Context applicationContext) {
        this.cardDAO = DatabaseTaboom.getDatabase(applicationContext).cardDao();
        this.cardList = cardDAO.getAll();
    }

    public LiveData<List<CardEntity>> getCardList() {
        if (cardDAO == null)
            Log.d(TAG, "CardDAO is null, should call initializeDB() first");
        return cardList;
    }

}
