package it.bastoner.taboom.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.List;
import java.util.Locale;

import it.bastoner.taboom.R;
import it.bastoner.taboom.ViewModelMainActivity;
import it.bastoner.taboom.adapter.RecyclerViewAdapter;
import it.bastoner.taboom.database.CardEntity;
import it.bastoner.taboom.filters.MinMaxFilter;
import it.bastoner.taboom.listeners.MyDialogListener;


public class PlayFragment extends BaseCardFragment implements MyDialogListener {

    private static final String TAG = "PlayFragment";

    private RecyclerView recyclerView;

    private TextView timerTextView;
    private Button playPauseButton;
    private Button resetButton;
    private CountDownTimer countDownTimer;
    private long startTimeInMillis;
    private boolean timerIsRunning;
    private long timeLeftInMillis = startTimeInMillis;

    private MediaPlayer endTimerSound;

    public PlayFragment(List<CardEntity> cardList) {
        super(cardList);
        //Log.d(TAG, "PlayFragment created");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, ">>OnCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, ">>OnViewCreated");

        setRecyclerView();

        setViewModel();

        setTimerSound();

        setTimer();

        setDialogModifyTimer();

    }

    private void setViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModelMainActivity.class);
        viewModel.getCardList().observe(getActivity(), cardList -> {
            updateUI(cardList);
        });
    }

    @Override
    public void updateUI(List<CardEntity> cardList) {

        RecyclerViewAdapter adapter = (RecyclerViewAdapter) recyclerView.getAdapter();
        if (adapter != null) {
                adapter.setCardList(cardList);
                adapter.notifyDataSetChanged();
                Log.d(TAG, ">>Update, number of items: " + adapter.getItemCount());
        } else {
            Log.d(TAG, "Adapter is null");
        }
    }

    private void setDialogModifyTimer() {

        timerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = getLayoutInflater();
                View viewTimer = layoutInflater.inflate(R.layout.dialog_modify_timer, null);
                EditText minutesEdit = viewTimer.findViewById(R.id.modify_minutes);
                EditText secondsEdit = viewTimer.findViewById(R.id.modify_seconds);
                secondsEdit.setFilters(new InputFilter[] {new MinMaxFilter(0, 60)});
                int minutes = (int) (startTimeInMillis / 1000 / 60);
                int seconds = (int) (startTimeInMillis / 1000 % 60);
                String minutesString = String.format(Locale.getDefault(), "%d", minutes);
                String secondsString = String.format(Locale.getDefault(), "%02d", seconds);
                minutesEdit.setText(minutesString);
                secondsEdit.setText(secondsString);

                new AlertDialog.Builder(getContext())
                               .setView(viewTimer)
                               .setTitle(R.string.title_modify_timer)
                               .setPositiveButton(R.string.modify, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {

                                       String minutesString = String.valueOf(minutesEdit.getText());
                                       String secondsString = String.valueOf(secondsEdit.getText());
                                       // Check if the user deleted the input
                                       int minutes = Integer.parseInt(!minutesString.equals("") ? minutesString : "0");
                                       int seconds = Integer.parseInt(!secondsString.equals("") ? secondsString : "0");
                                       sendDialogData(minutes, seconds);
                                   }
                               })
                               .create()
                               .show();

            }
        });
    }

    private void setTimer() {

        startTimeInMillis = 60000;
        timeLeftInMillis = startTimeInMillis;
        timerIsRunning = false;
        timerTextView = getView().findViewById(R.id.timer);
        playPauseButton = getView().findViewById(R.id.play);
        resetButton = getView().findViewById(R.id.reset);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerIsRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });

        updateCountDownText();
    }

    private void setTimerSound() {
        if (endTimerSound == null) {
            endTimerSound = MediaPlayer.create(getContext(), R.raw.end_timer_sound_default);
        }
    }

    private void setRecyclerView() {
        // Log.d(TAG, "PlayFragment view creata");
        recyclerView = getView().findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(cardList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1,
                                                        RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Set the helper to make recycler view not to show half items
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void startTimer() {

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {

                endTimerSound.start();
                timerIsRunning = false;
                timeLeftInMillis = startTimeInMillis;
                updateCountDownText();
                playPauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            }
        };

        countDownTimer.start();
        timerIsRunning = true;
        playPauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24);
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000 / 60);
        int seconds = (int) (timeLeftInMillis / 1000 % 60);
        String timeLeftString = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftString);
    }

    private void pauseTimer() {

        // Or it will generate Null Pointer Exception for sanity sake
        if (countDownTimer != null)
            countDownTimer.cancel();
        timerIsRunning = false;
        playPauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);

    }

    private void resetTimer() {

        // Or it will generate Null Pointer Exception
        if (countDownTimer != null)
            countDownTimer.cancel();
        timerIsRunning = false;
        timeLeftInMillis = startTimeInMillis;
        playPauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        updateCountDownText();
    }

    public void sendDialogData(int minutes, int seconds) {

        startTimeInMillis = minutes * 60 * 1000 + seconds * 1000;
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
    }


}