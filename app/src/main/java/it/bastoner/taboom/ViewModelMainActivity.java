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
import it.bastoner.taboom.database.CardTagCrossRef;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.DatabaseTaboom;
import it.bastoner.taboom.database.Tag;

public class ViewModelMainActivity extends AndroidViewModel {

    private static final String TAG = "ViewModelMainActivity";
    private static boolean cardListIsUpdatedWithDb = false;
    private static boolean tagListIsUpdatedWithDb = false;

    private LiveData<List<CardWithTags>> cardList = new MutableLiveData<>();
    private LiveData<List<Tag>> tagList = new MutableLiveData<>();
    private final CardDAO cardDAO;
    private final ExecutorService executor;

    public ViewModelMainActivity(@NonNull Application application) {
        super(application);

        this.cardDAO = DatabaseTaboom.getDatabase(application).cardDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<CardWithTags>> getAllCards() {

        Log.d(TAG, ">>GetAllCards()");

        if (!cardListIsUpdatedWithDb) {
            cardList = cardDAO.getAllCards();
            cardListIsUpdatedWithDb = true;
        }

        return cardList;

    }

    public LiveData<List<Tag>> getAllTags() {

        Log.d(TAG, ">>GetAllTags()");

        if (!tagListIsUpdatedWithDb) {
            tagList = cardDAO.getAllTags();
            tagListIsUpdatedWithDb = true;
        }

        return tagList;

    }

    public void insertCard(CardWithTags card) {

        Log.d(TAG, ">>InsertCard(): " + card);

        executor.execute(() -> {

            long idCard = cardDAO.insertCard(card.getCard());

            if (idCard >= 1) {
                cardListIsUpdatedWithDb = false;
                Log.d(TAG, ">>Inserted Card: " + card.getCard().getTitle());
            }

            for (Tag t: card.getTagList()) {
                long idTag = cardDAO.insertTag(t);

                CardTagCrossRef cardTagCrossRef = new CardTagCrossRef();
                cardTagCrossRef.idCard = idCard;

                if (idTag < 1) {
                    // If Tag already exist, retrieve that to get id to try insert cwt
                    idTag = cardDAO.getTag(t.getTag()).getIdTag();
                } else {
                    // New Tag
                    tagListIsUpdatedWithDb = false;
                    Log.d(TAG, ">>Inserted Tag: " + t.getTag());
                }

                cardTagCrossRef.idTag = idTag;
                long idCWT = cardDAO.insertCardWithTags(cardTagCrossRef);
                if (idCWT >= 1) {
                    Log.d(TAG, ">>Inserted CWT: [" + idCard + "," + idTag + "]");
                    // Tag linked to a card
                    cardListIsUpdatedWithDb = false;
                }
            }

        });

    }

    public void shuffle(List<CardWithTags> list) {
        if (list != null) {
            Collections.shuffle(list);
            cardList = new MutableLiveData<>(list);
            Log.d(TAG, ">>List shuffled: " + list);
        } else {
            Log.d(TAG, ">>List is null");
        }
    }

}
