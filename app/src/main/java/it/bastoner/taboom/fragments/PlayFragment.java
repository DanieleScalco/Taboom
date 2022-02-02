package it.bastoner.taboom.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.List;
import java.util.Locale;

import it.bastoner.taboom.Constants;
import it.bastoner.taboom.R;
import it.bastoner.taboom.ViewModelMainActivity;
import it.bastoner.taboom.adapter.RecyclerViewAdapter;
import it.bastoner.taboom.database.CardEntity;
import it.bastoner.taboom.filters.MinMaxFilter;
import it.bastoner.taboom.listeners.MyDialogListener;


public class PlayFragment extends BaseCardFragment implements MyDialogListener {

    // TODO show total cards number; Limit text lenght teamname

    private static final String TAG = "PlayFragment";

    private RecyclerView recyclerView;

    private TextView timerTextView;
    private Button playPauseButton;
    private Button resetButton;
    private CountDownTimer countDownTimer;
    private long startTimeInMillis;
    private boolean timerIsRunning;
    private long timeLeftInMillis;

    private MediaPlayer endTimerSound;

    private Button plusButtonA;
    private Button minusButtonA;
    private Button plusButtonB;
    private Button minusButtonB;
    private TextView scoreATextView;
    private TextView scoreBTextView;
    private int scoreA;
    private int scoreB;

    private EditText teamA;
    private EditText teamB;

    private Button shuffleButton;

    private SharedPreferences sharedPreferences;

    private int position;

    public PlayFragment(List<CardEntity> cardList) {
        super(cardList);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, ">>OnCreateView()");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, ">>OnViewCreated()");

        sharedPreferences = getActivity()
                            .getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        setRecyclerView();

        setViewModel();

        setTimerSound();

        setTimer();

        setDialogModifyTimer();

        setScoreButtons();

        setTeamNames();

        setShuffleButton();

    }

    private void setViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModelMainActivity.class);
        viewModel.getAllCards().observe(getActivity(), cardList -> {
            updateUI(cardList);
        });
    }

    @Override
    public void updateUI(List<CardEntity> cardList) {

        RecyclerViewAdapter adapter = (RecyclerViewAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            this.cardList = cardList;
            adapter.setCardList(cardList);
            adapter.notifyDataSetChanged();
            Log.d(TAG, ">>Update, Total cards found: " + adapter.getItemCount());
        } else {
            Log.d(TAG, ">>Update, Adapter is null");
        }


    }

    private void setDialogModifyTimer() {

        timerTextView.setOnClickListener(view -> {

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

            resetTimer();

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

        });
    }

    private void setTimer() {

        startTimeInMillis = sharedPreferences.getLong(Constants.TIMER, 60000);
        timeLeftInMillis = startTimeInMillis;
        timerIsRunning = false;
        timerTextView = getView().findViewById(R.id.timer);
        playPauseButton = getView().findViewById(R.id.play);
        resetButton = getView().findViewById(R.id.reset);

        playPauseButton.setOnClickListener(view -> {
            if (timerIsRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        resetButton.setOnClickListener(view -> resetTimer());

        updateCountDownText();
    }

    private void setTimerSound() {
        if (endTimerSound == null) {
            endTimerSound = MediaPlayer.create(getContext(), R.raw.bomb);
        }
    }

    private void setRecyclerView() {

        recyclerView = getView().findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(cardList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1,
                                                        RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Set the helper to make recycler view not to show half items
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View actualView = snapHelper.findSnapView(layoutManager);

                    // Check if there are no cards
                    if (actualView != null) {
                        position = layoutManager.getPosition(actualView);
                        Log.d(TAG, ">>RecyclerPosition: " + position);
                    }
                }
            }

        });

        position = sharedPreferences.getInt(Constants.RECYCLER_CARD_POSITION, 0);
        layoutManager.scrollToPosition(position);

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
                playPauseButton.setBackgroundResource(R.drawable.play_button);
            }
        };

        countDownTimer.start();
        timerIsRunning = true;
        playPauseButton.setBackgroundResource(R.drawable.pause_button);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, ">>Destroy()");

        // Stop timer
        if (countDownTimer != null)
            countDownTimer.cancel();

        // Save the state of the PlayFragment
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.TEAM_A_NAME, teamA.getText().toString());
        editor.putString(Constants.TEAM_B_NAME, teamB.getText().toString());
        editor.putInt(Constants.TEAM_A_SCORE, scoreA);
        editor.putInt(Constants.TEAM_B_SCORE, scoreB);
        editor.putLong(Constants.TIMER, startTimeInMillis);
        editor.putInt(Constants.RECYCLER_CARD_POSITION, position);
        editor.commit();

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
        playPauseButton.setBackgroundResource(R.drawable.play_button);

    }

    private void resetTimer() {

        // Or it will generate Null Pointer Exception
        if (countDownTimer != null)
            countDownTimer.cancel();
        timerIsRunning = false;
        timeLeftInMillis = startTimeInMillis;
        playPauseButton.setBackgroundResource(R.drawable.play_button);
        updateCountDownText();
    }

    public void sendDialogData(int minutes, int seconds) {

        startTimeInMillis = (long) (minutes * 60 * 1000 + seconds * 1000);
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
    }

    private void setScoreButtons() {

        minusButtonA = getView().findViewById(R.id.team_a_minus);
        plusButtonA = getView().findViewById(R.id.team_a_plus);
        minusButtonB = getView().findViewById(R.id.team_b_minus);
        plusButtonB = getView().findViewById(R.id.team_b_plus);
        scoreATextView = getView().findViewById(R.id.team_a_score);
        scoreBTextView = getView().findViewById(R.id.team_b_score);

        scoreA = sharedPreferences.getInt(Constants.TEAM_A_SCORE, 0);
        scoreB = sharedPreferences.getInt(Constants.TEAM_B_SCORE, 0);
        scoreATextView.setText(String.format(Locale.getDefault(), "%02d", scoreA));
        scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", scoreB));



        minusButtonA.setOnClickListener(view -> {
            if (scoreA > 0 )
                scoreATextView.setText(String.format(Locale.getDefault(), "%02d", --scoreA));
        });

        plusButtonA.setOnClickListener(view -> {
            if (scoreA >= 99) {
                scoreA = 0;
                scoreATextView.setText(String.format(Locale.getDefault(), "%02d", scoreA));
            } else
                scoreATextView.setText(String.format(Locale.getDefault(), "%02d", ++scoreA));
        });

        minusButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scoreB > 0)
                    scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", --scoreB));
            }
        });

        plusButtonB.setOnClickListener(view -> {
            if (scoreB >= 99) {
                scoreB = 0;
                scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", scoreB));
            } else
                scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", ++scoreB));
        });

    }

    private void setTeamNames() {

        teamA = getView().findViewById(R.id.team_a_name);
        teamB = getView().findViewById(R.id.team_b_name);

        String nameA = sharedPreferences.getString(Constants.TEAM_A_NAME, getResources().getString(R.string.team_a_name));
        String nameB = sharedPreferences.getString(Constants.TEAM_B_NAME, getResources().getString(R.string.team_a_name));
        teamA.setText(nameA);
        teamB.setText(nameB);

        teamA.setOnFocusChangeListener((view, hasFocus) -> {

            // If loses focus check if team name is not empty
            if (!hasFocus) {
                if (teamA.getText().toString().length() == 0)
                    teamA.setText(R.string.team_a_name);
            }
        });

        teamB.setOnFocusChangeListener((view, hasFocus) -> {

            // If loses focus check if team name is not empty
            if (!hasFocus) {
                if (teamB.getText().toString().length() == 0)
                    teamB.setText(R.string.team_b_name);
            }
        });

    }

    private void setShuffleButton() {

        shuffleButton = getView().findViewById(R.id.shuffle);
        shuffleButton.setOnClickListener(view -> {
            viewModel.shuffle(cardList);
            updateUI(cardList);
            Toast.makeText(getContext(), R.string.card_shuffled, Toast.LENGTH_SHORT).show();
        });
    }
}