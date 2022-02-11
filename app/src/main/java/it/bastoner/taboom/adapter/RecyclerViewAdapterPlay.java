package it.bastoner.taboom.adapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.database.Card;


public class RecyclerViewAdapterPlay extends RecyclerView.Adapter<RecyclerViewAdapterPlay.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<Card> cardList;

    public RecyclerViewAdapterPlay(List<Card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String titleString = cardList.get(position).getTitle();
        String taboo1String = cardList.get(position).getTabooWord1();
        String taboo2String = cardList.get(position).getTabooWord2();
        String taboo3String = cardList.get(position).getTabooWord3();
        String taboo4String = cardList.get(position).getTabooWord4();
        String taboo5String = cardList.get(position).getTabooWord5();

        holder.title.setText(titleString);
        holder.tabooWord1.setText(taboo1String);
        holder.tabooWord2.setText(taboo2String);
        holder.tabooWord3.setText(taboo3String);
        holder.tabooWord4.setText(taboo4String);
        holder.tabooWord5.setText(taboo5String);
    }

    @Override
    public int getItemCount() {
        if (cardList == null)
            return 0;
        return cardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "ViewHolder";

        private final TextView title;
        private final TextView tabooWord1;
        private final TextView tabooWord2;
        private final TextView tabooWord3;
        private final TextView tabooWord4;
        private final TextView tabooWord5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            title.setAutoSizeTextTypeUniformWithConfiguration(20, 28, 2, TypedValue.COMPLEX_UNIT_SP);
            tabooWord1 = itemView.findViewById(R.id.taboo_word_1);
            tabooWord1.setAutoSizeTextTypeUniformWithConfiguration(16, 24, 2, TypedValue.COMPLEX_UNIT_SP);
            tabooWord2 = itemView.findViewById(R.id.taboo_word_2);
            tabooWord2.setAutoSizeTextTypeUniformWithConfiguration(16, 24, 2, TypedValue.COMPLEX_UNIT_SP);
            tabooWord3 = itemView.findViewById(R.id.taboo_word_3);
            tabooWord3.setAutoSizeTextTypeUniformWithConfiguration(16, 24, 2, TypedValue.COMPLEX_UNIT_SP);
            tabooWord4 = itemView.findViewById(R.id.taboo_word_4);
            tabooWord4.setAutoSizeTextTypeUniformWithConfiguration(16, 24, 2, TypedValue.COMPLEX_UNIT_SP);
            tabooWord5 = itemView.findViewById(R.id.taboo_word_5);
            tabooWord5.setAutoSizeTextTypeUniformWithConfiguration(16, 24, 2, TypedValue.COMPLEX_UNIT_SP);

        }
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }
}
