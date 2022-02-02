package it.bastoner.taboom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.database.CardEntity;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private List<CardEntity> cardList;

    public RecyclerViewAdapter(List<CardEntity> cardList) {
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
        holder.title.setText(cardList.get(position).getTitle());
        holder.tabooWord1.setText(cardList.get(position).getTabooWord1());
        holder.tabooWord2.setText(cardList.get(position).getTabooWord2());
        holder.tabooWord3.setText(cardList.get(position).getTabooWord3());
        holder.tabooWord4.setText(cardList.get(position).getTabooWord4());
        holder.tabooWord5.setText(cardList.get(position).getTabooWord5());
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
            title.setFocusable(false);
            tabooWord1 = itemView.findViewById(R.id.taboo_word_1);
            tabooWord1.setFocusable(false);
            tabooWord2 = itemView.findViewById(R.id.taboo_word_2);
            tabooWord2.setFocusable(false);
            tabooWord3 = itemView.findViewById(R.id.taboo_word_3);
            tabooWord3.setFocusable(false);
            tabooWord4 = itemView.findViewById(R.id.taboo_word_4);
            tabooWord4.setFocusable(false);
            tabooWord5 = itemView.findViewById(R.id.taboo_word_5);
            tabooWord5.setFocusable(false);

        }
    }

    public void setCardList(List<CardEntity> cardList) {
        this.cardList = cardList;
    }
}
