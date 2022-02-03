package it.bastoner.taboom;

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

    public static List<Card> getCards(List<CardWithTags> cardWithTagsList) {

        List<Card> list = new ArrayList<>();

        if (cardWithTagsList != null)
            for (CardWithTags c : cardWithTagsList) {
                Card card = c.getCard();
                list.add(card);
            }
        return list;
    }

}
