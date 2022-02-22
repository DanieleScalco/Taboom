package it.bastoner.taboom.utilities;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;

public class CardGroup extends ExpandableGroup<CardWithTags> {

    private Tag tag;

    public CardGroup(String title, Tag tag, List<CardWithTags> items) {
        super(title, items);
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }
}
