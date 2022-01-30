package it.bastoner.taboom.database;

import android.content.res.Resources;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import it.bastoner.taboom.R;

@Entity
public class CardEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "taboo_word_1")
    private String tabooWord1;

    @ColumnInfo(name = "taboo_word_2")
    private String tabooWord2;

    @ColumnInfo(name = "taboo_word_3")
    private String tabooWord3;

    @ColumnInfo(name = "taboo_word_4")
    private String tabooWord4;

    @ColumnInfo(name = "taboo_word_5")
    private String tabooWord5;

    @ColumnInfo(name = "list_name")
    private String listName;

    public CardEntity(String title, String tabooWord1, String tabooWord2, String tabooWord3,
                      String tabooWord4, String tabooWord5, String listName) {

        String emptyTabooWord = "---------"; // R.string.no_taboo_word

        this.title = title;
        if (tabooWord1 == null || tabooWord1.length() == 0)
            this.tabooWord1 = emptyTabooWord;
        else
            this.tabooWord1 = tabooWord1;
        if (tabooWord2 == null || tabooWord2.length() == 0)
            this.tabooWord2 = emptyTabooWord;
        else
            this.tabooWord2 = tabooWord2;
        if (tabooWord3 == null || tabooWord3.length() == 0)
            this.tabooWord3 = emptyTabooWord;
        else
            this.tabooWord3 = tabooWord3;
        if (tabooWord4 == null || tabooWord4.length() == 0)
            this.tabooWord4 = emptyTabooWord;
        else
            this.tabooWord4 = tabooWord4;
        if (tabooWord5 == null || tabooWord5.length() == 0)
            this.tabooWord5 = emptyTabooWord;
        else
            this.tabooWord5 = tabooWord5;
        if (listName != null && !listName.isEmpty())
            this.listName = listName;
        else
            this.listName = "Lista base"; // R.string.default_list_name only with context
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTabooWord1() {
        return tabooWord1;
    }

    public void setTabooWord1(String tabooWord1) {
        this.tabooWord1 = tabooWord1;
    }

    public String getTabooWord2() {
        return tabooWord2;
    }

    public void setTabooWord2(String tabooWord2) {
        this.tabooWord2 = tabooWord2;
    }

    public String getTabooWord3() {
        return tabooWord3;
    }

    public void setTabooWord3(String tabooWord3) {
        this.tabooWord3 = tabooWord3;
    }

    public String getTabooWord4() {
        return tabooWord4;
    }

    public void setTabooWord4(String tabooWord4) {
        this.tabooWord4 = tabooWord4;
    }

    public String getTabooWord5() {
        return tabooWord5;
    }

    public void setTabooWord5(String tabooWord5) {
        this.tabooWord5 = tabooWord5;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    @Override
    public String toString() {
        return "CardEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tabooWord1='" + tabooWord1 + '\'' +
                ", tabooWord2='" + tabooWord2 + '\'' +
                ", tabooWord3='" + tabooWord3 + '\'' +
                ", tabooWord4='" + tabooWord4 + '\'' +
                ", tabooWord5='" + tabooWord5 + '\'' +
                ", listName='" + listName + '\'' +
                '}';
    }
}
