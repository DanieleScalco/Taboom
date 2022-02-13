package it.bastoner.taboom.utilities;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.bastoner.taboom.database.Card;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;

public class Utilities {

    public static final String SHARED_PREFERENCES = "taboom_shared_preferences";
    public static final String TEAM_A_NAME = "team_a_name";
    public static final String TEAM_B_NAME = "team_b_name";
    public static final String TIMER = "timer";
    public static final String TEAM_A_SCORE = "team_a_score";
    public static final String TEAM_B_SCORE = "team_b_score";
    public static final String RECYCLER_CARD_POSITION = "recycler_card_position";
    public static final String SELECTED_TAGS = "selected_tags";
    public static final String SHOULD_SHUFFLE = "should_shuffle";

    // Following datas are also defined in res/values/integers.xml
    public static final int MAX_TAG_LENGTH = 17;
    public static final int MAX_TITLE_LENGTH = 18;
    public static final int MAX_TABOO_LENGTH = 20;
    public static final int MAX_TEAM_NAME_LENGTH = 11;

    public static List<Card> getCards(List<CardWithTags> cardWithTagsList) {

        List<Card> list = new ArrayList<>();

        if (cardWithTagsList != null)
            for (CardWithTags c : cardWithTagsList) {
                Card card = c.getCard();
                list.add(card);
            }
        return list;
    }

    public static void logCardsAndTags(String tag, List<CardWithTags> cardWithTagsList, List<Tag> tagList) {
        Log.d(tag, ">>LogCardsAndTags()");
        Log.d(tag, ">>Cards.size() = " + cardWithTagsList.size());
        Log.d(tag, ">>CardList = " + cardWithTagsList);
        Log.d(tag, ">>Tags.size() = " + tagList.size());
        Log.d(tag, ">>TagList = " + tagList);
    }

}
