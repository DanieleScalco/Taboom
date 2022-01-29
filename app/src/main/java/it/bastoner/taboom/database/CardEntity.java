package it.bastoner.taboom.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
    private String list_name;

    public CardEntity(String title, String tabooWord1, String tabooWord2,
                      String tabooWord3, String tabooWord4, String tabooWord5) {
        this.title = title;
        this.tabooWord1 = tabooWord1;
        this.tabooWord2 = tabooWord2;
        this.tabooWord3 = tabooWord3;
        this.tabooWord4 = tabooWord4;
        this.tabooWord5 = tabooWord5;
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

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }
}
