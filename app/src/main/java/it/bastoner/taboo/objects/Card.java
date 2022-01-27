package it.bastoner.taboo.objects;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Card {

    private static final String TAG = "Card";
    private static final int TABOO_WORDS_NUMBER = 5;

    private String title;
    private List<String> tabooWords;

    public Card(String title, String[] tabooWords) {
        this.title = title;
        if (tabooWords.length > TABOO_WORDS_NUMBER)
            this.tabooWords = new ArrayList<String>(TABOO_WORDS_NUMBER);
        else
            this.tabooWords = Arrays.asList(tabooWords);
        Log.d(TAG, "Empty card created");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTabooWords() {
        return tabooWords;
    }

    public void setTabooWords(List<String> tabooWords) {
        this.tabooWords = tabooWords;
    }

    public void modifyTabooWord(int index, String newWord) {
        if (index >= TABOO_WORDS_NUMBER) {
            Log.d(TAG, "Error index");
            return;
        }
        if (newWord != null && !newWord.isEmpty())
            this.tabooWords.set(index, newWord);
        else
            Log.d(TAG, "Trying to add null or empty word");
    }

    @Override
    public String toString() {
        return "Card{" +
                "title='" + title + '\'' +
                ", tabooWords=" + tabooWords +
                '}';
    }
}