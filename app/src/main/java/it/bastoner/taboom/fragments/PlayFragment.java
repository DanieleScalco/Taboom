package it.bastoner.taboom.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import it.bastoner.taboom.MainActivity;
import it.bastoner.taboom.R;
import it.bastoner.taboom.adapter.RecyclerViewAdapterPlay;
import it.bastoner.taboom.animations.Animations;
import it.bastoner.taboom.database.CardWithTags;
import it.bastoner.taboom.database.Tag;
import it.bastoner.taboom.filters.MinMaxFilter;
import it.bastoner.taboom.utilities.Utilities;
import it.bastoner.taboom.viewModel.ViewModelMainActivity;

public class PlayFragment extends BaseCardFragment {

    private static final String TAG = "PlayFragment";
    private static boolean fragmentIsActive;
    public static boolean appJustOpened;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private TextView timerTextView;
    private Button playPauseButton;
    private Button resetButton;
    private CountDownTimer countDownTimer;
    private long startTimeInMillis;
    private boolean timerIsRunning;
    private long timeLeftInMillis;

    private MediaPlayer endTimerSound;
    private MediaPlayer clearSound;

    private Button plusButtonA;
    private Button minusButtonA;
    private Button plusButtonB;
    private Button minusButtonB;
    private TextView scoreATextView;
    private TextView scoreBTextView;
    private int scoreA;
    private int scoreB;
    private Runnable runnable;
    private Handler handler = new Handler();

    private EditText teamA;
    private EditText teamB;

    private Button shuffleButton;

    private SharedPreferences sharedPreferences;
    private int positionRecyclerCard;


    public PlayFragment(List<CardWithTags> cardList, List<Tag> tagList) {
        super(cardList, tagList);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Log.d(TAG, ">>OnCreateView()");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, ">>OnViewCreated()");

        sharedPreferences = getActivity()
                            .getSharedPreferences(Utilities.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (appJustOpened) {
            Toast.makeText(getContext(), R.string.welcome, Toast.LENGTH_LONG).show();
            appJustOpened = false;
        }

        setRecyclerView();

        setViewModel();

        setTimer();

        setDialogModifyTimer();

        setScoreButtons();

        setTeamNames();

        setShuffleButton();

    }

    private void setViewModel() {

        Log.d(TAG, ">>SetViewModel");

        // Needed causa viewModel persist even when Fragment is destroyed
        fragmentIsActive = true;

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelMainActivity.class);
        viewModel.getAllCards().observe(requireActivity(), cardListLoaded -> {

            if (fragmentIsActive) {
                cardList = cardListLoaded;
                initializeRecyclerCardList();
                updateUI();
                Log.d(TAG, ">>CardList updated:" + cardList);
            }

        });

        viewModel.getAllTags().observe(requireActivity(), tagListLoaded -> {

            if (fragmentIsActive) {
                tagList = tagListLoaded;
                //initializeRecyclerCardList();
                //updateUI(); Not needed
                Log.d(TAG, ">>TagList updated:" + tagList);
            }
        });

    }

    private void updateUI() {
        Log.d(TAG, ">>UpdateUI()");
        RecyclerViewAdapterPlay adapter = (RecyclerViewAdapterPlay) recyclerView.getAdapter();
        boolean shouldShuffle = sharedPreferences.getBoolean(Utilities.SHOULD_SHUFFLE, false);
        if (shouldShuffle) {
            shuffle(MainActivity.recyclerCardList);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Utilities.SHOULD_SHUFFLE);
            editor.commit();
        }
        adapter.setCardList(Utilities.getCards(MainActivity.recyclerCardList));
        adapter.notifyDataSetChanged();
    }

    private void setDialogModifyTimer() {

        timerTextView.setOnClickListener(view -> {

            LayoutInflater layoutInflater = getLayoutInflater();
            View viewTimer = layoutInflater.inflate(R.layout.dialog_modify_timer, null);
            EditText minutesEdit = viewTimer.findViewById(R.id.modify_minutes);
            EditText secondsEdit = viewTimer.findViewById(R.id.modify_seconds);
            secondsEdit.setFilters(new InputFilter[] {new MinMaxFilter(0, 59)});
            int minutes = (int) (startTimeInMillis / 1000 / 60);
            int seconds = (int) (startTimeInMillis / 1000 % 60);
            String minutesString = String.format(Locale.getDefault(), "%d", minutes);
            String secondsString = String.format(Locale.getDefault(), "%02d", seconds);
            minutesEdit.setText(minutesString);
            secondsEdit.setText(secondsString);

            resetTimer();

            getTimerDialog(viewTimer, minutesEdit, secondsEdit).show();

        });
    }

    private AlertDialog getTimerDialog(View viewTimer, EditText minutesEdit, EditText secondsEdit) {
        return new AlertDialog.Builder(getContext())
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
                .create();
    }

    private void setTimer() {

        startTimeInMillis = sharedPreferences.getLong(Utilities.TIMER, 60000);
        timeLeftInMillis = startTimeInMillis;
        timerIsRunning = false;

        timerTextView = getView().findViewById(R.id.timer);
        playPauseButton = getView().findViewById(R.id.play);
        resetButton = getView().findViewById(R.id.reset);

        endTimerSound = MediaPlayer.create(getContext(), R.raw.bomb);


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

    private void setRecyclerView() {

        Log.d(TAG, ">>SetRecyclerView()");

        recyclerView = getView().findViewById(R.id.recycler_view);

        // Recycler use a COPY of cardList
        if (MainActivity.recyclerCardList == null) {
            MainActivity.recyclerCardList = new ArrayList<>(cardList);
        }

        RecyclerViewAdapterPlay recyclerViewAdapterPlay = new RecyclerViewAdapterPlay(Utilities.getCards(MainActivity.recyclerCardList));
        layoutManager = new GridLayoutManager(getContext(), 1,
                                                        RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Set the helper to make recycler view not to show half items
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(recyclerViewAdapterPlay);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View actualView = snapHelper.findSnapView(layoutManager);

                    // Check if there are no cards
                    if (actualView != null) {
                        positionRecyclerCard = layoutManager.getPosition(actualView);
                        //Log.d(TAG, ">>RecyclerPosition: " + positionRecyclerCard);
                    }
                }
            }

        });

        positionRecyclerCard = sharedPreferences.getInt(Utilities.RECYCLER_CARD_POSITION, 0);
        layoutManager.scrollToPosition(positionRecyclerCard);

    }

    private void initializeRecyclerCardList() {

        Log.d(TAG, ">>InitializeRecyclerCardList()");

        MainActivity.recyclerCardList = new ArrayList<>();

        Log.d(TAG, ">>TagListChosen: " + MainActivity.recyclerTagList);

        if (MainActivity.recyclerTagList.isEmpty()) {
            MainActivity.recyclerCardList = cardList;
        } else {

            for (CardWithTags c : cardList) {
                for (Tag t1 : c.getTagList()) {
                    for (Tag t2 : MainActivity.recyclerTagList) {
                        if (t1.getTag().equalsIgnoreCase(t2.getTag()) && !MainActivity.recyclerCardList.contains(c))
                            MainActivity.recyclerCardList.add(c);
                    }
                }
            }

        }

        Log.d(TAG, ">>Cards: " + MainActivity.recyclerCardList);
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
                playPauseButton.setTooltipText(getResources().getString(R.string.play_tooltip));

            }
        };

        countDownTimer.start();
        timerIsRunning = true;
        playPauseButton.setBackgroundResource(R.drawable.pause_button);
        playPauseButton.setTooltipText(getResources().getString(R.string.pause_tooltip));

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
        playPauseButton.setTooltipText(getResources().getString(R.string.play_tooltip));

    }

    private void resetTimer() {

        // Or it will generate Null Pointer Exception
        if (countDownTimer != null)
            countDownTimer.cancel();
        timerIsRunning = false;
        timeLeftInMillis = startTimeInMillis;
        playPauseButton.setBackgroundResource(R.drawable.play_button);
        playPauseButton.setTooltipText(getResources().getString(R.string.play_tooltip));

        updateCountDownText();
    }

    private void sendDialogData(int minutes, int seconds) {

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

        scoreA = sharedPreferences.getInt(Utilities.TEAM_A_SCORE, 0);
        scoreB = sharedPreferences.getInt(Utilities.TEAM_B_SCORE, 0);
        scoreATextView.setText(String.format(Locale.getDefault(), "%02d", scoreA));
        scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", scoreB));

        // OnClick()
        minusButtonA.setOnClickListener(view -> {
            Animations.doReduceIncreaseAnimation(view, null);
            if (scoreA > 0 )
                scoreATextView.setText(String.format(Locale.getDefault(), "%02d", --scoreA));
        });

        plusButtonA.setOnClickListener(view -> {
            Animations.doReduceIncreaseAnimation(view, null);
            if (scoreA >= 99) {
                scoreA = 0;
                scoreATextView.setText(String.format(Locale.getDefault(), "%02d", scoreA));
            } else
                scoreATextView.setText(String.format(Locale.getDefault(), "%02d", ++scoreA));
        });

        minusButtonB.setOnClickListener(view -> {
            Animations.doReduceIncreaseAnimation(view, null);
            if (scoreB > 0)
                scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", --scoreB));
        });

        plusButtonB.setOnClickListener(view -> {
            Animations.doReduceIncreaseAnimation(view, null);
            if (scoreB >= 99) {
                scoreB = 0;
                scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", scoreB));
            } else
                scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", ++scoreB));
        });

        // OnLongClick
        minusButtonA.setOnLongClickListener(view -> {

            Animations.doReduceAnimation(view, null);

            runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, ">>Called");
                    if (!view.isPressed()) {
                        Animations.doIncreaseAnimation(view, null);
                        return;
                    }
                    if (scoreA > 0 )
                        scoreATextView.setText(String.format(Locale.getDefault(), "%02d", --scoreA));

                    handler.postDelayed(runnable, 100);
                }
            };

            handler.post(runnable);
            return true;
        });

        plusButtonA.setOnLongClickListener(view -> {

            Animations.doReduceAnimation(view, null);

            runnable = new Runnable() {
                @Override
                public void run() {
                    if (!view.isPressed()) {
                        Animations.doIncreaseAnimation(view, null);
                        return;
                    }
                    if (scoreA >= 99) {
                        scoreA = 0;
                        scoreATextView.setText(String.format(Locale.getDefault(), "%02d", scoreA));
                    } else
                        scoreATextView.setText(String.format(Locale.getDefault(), "%02d", ++scoreA));

                    handler.postDelayed(runnable, 100);
                }
            };

            handler.post(runnable);
            return true;
        });

        minusButtonB.setOnLongClickListener(view -> {

            Animations.doReduceAnimation(view, null);

            runnable = new Runnable() {
                @Override
                public void run() {
                    if (!view.isPressed()) {
                        Animations.doIncreaseAnimation(view, null);
                        return;
                    }
                    if (scoreB > 0 )
                        scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", --scoreB));

                    handler.postDelayed(runnable, 100);
                }
            };

            handler.post(runnable);
            return true;
        });

        plusButtonB.setOnLongClickListener(view -> {

            Animations.doReduceAnimation(view, null);

            runnable = new Runnable() {
                @Override
                public void run() {
                    if (!view.isPressed()) {
                        Animations.doIncreaseAnimation(view, null);
                        return;
                    }
                    if (scoreB >= 99) {
                        scoreB = 0;
                        scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", scoreB));
                    } else
                        scoreBTextView.setText(String.format(Locale.getDefault(), "%02d", ++scoreB));

                    handler.postDelayed(runnable, 100);
                }
            };

            handler.post(runnable);
            return true;
        });

    }

    private void setTeamNames() {

        teamA = getView().findViewById(R.id.team_a_name);
        teamB = getView().findViewById(R.id.team_b_name);

        String nameA = sharedPreferences.getString(Utilities.TEAM_A_NAME, getResources().getString(R.string.team_a_name));
        String nameB = sharedPreferences.getString(Utilities.TEAM_B_NAME, getResources().getString(R.string.team_b_name));
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
        clearSound = MediaPlayer.create(getContext(), R.raw.clear_button);

        shuffleButton.setOnClickListener(view -> {

            Animations.doSpinReduceIncreaseAnimation(view, null);
            clearSound.start();
            shuffle(MainActivity.recyclerCardList);
            updateUI();
            Toast.makeText(getContext(), R.string.card_shuffled, Toast.LENGTH_SHORT).show();

        });
    }

    public void shuffle(List<CardWithTags> list) {
        if (list != null) {
            Collections.shuffle(list);
            if (layoutManager != null)
                layoutManager.scrollToPosition(0);
            Log.d(TAG, ">>List shuffled: " + list);
        } else {
            Log.d(TAG, ">>List is not shuffled cause is null");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, ">>OnStop()");

        // Stop timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
            Log.d(TAG, ">>Timer canceled");
        }

        // Save the state of the PlayFragment
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Utilities.TEAM_A_NAME, teamA.getText().toString());
        editor.putString(Utilities.TEAM_B_NAME, teamB.getText().toString());
        editor.putInt(Utilities.TEAM_A_SCORE, scoreA);
        editor.putInt(Utilities.TEAM_B_SCORE, scoreB);
        editor.putLong(Utilities.TIMER, startTimeInMillis);
        editor.putInt(Utilities.RECYCLER_CARD_POSITION, positionRecyclerCard);
        editor.commit();

        fragmentIsActive = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, ">>OnDestroy()");

    }

}