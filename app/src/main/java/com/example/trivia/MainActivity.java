package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.Model.Question;
import com.example.trivia.Model.Score;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextView;
    private TextView questionCounterTextView;
//    private ImageButton previousButton;
    private Button trueButton;
    private Button falseButton;
    private TextView highScoreTextView;
//    private ImageButton nextButton;
    private TextView scoreText;
    private int currentCounterIndex = 0;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;

    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();
        prefs = new Prefs(MainActivity.this);

        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
//        nextButton = findViewById(R.id.next_button);
       highScoreTextView = findViewById(R.id.highest_score);
        scoreText = findViewById(R.id.score_text);
//        previousButton = findViewById(R.id.prev_button);
        questionCounterTextView = findViewById(R.id.counter_text);
        questionTextView = findViewById(R.id.question_textView);
//        previousButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
//        nextButton.setOnClickListener(this);

        scoreText.setText(String.valueOf(MessageFormat.format("Current Score:{0}", score.getScore())));


        currentQuestionIndex = prefs.getState();

        highScoreTextView.setText(String.valueOf(MessageFormat.format("Highest Score: {0}", prefs.getHighScore())));
        questionList= new QuestionBank().getQuestion(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextView.setText(questionArrayList.get(currentCounterIndex).getAnswer());
                questionCounterTextView.setText(MessageFormat.format("{0}/{1}", currentQuestionIndex, questionArrayList.size()));

//                Log.d("Inside", "processFinished: " + questionArrayList);

            }
        });
       // Log.d("Hello", "onCreate: " + questionList);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.prev_button:
//                if(currentQuestionIndex > 0) {
//                    currentQuestionIndex = (currentQuestionIndex - 1 ) % questionList.size();
//                    updateQuestions();
//                }
//            break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestions();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestions();
                break;
//            case R.id.next_button:
//              goNext();
//                break;


        }

    }

    private void checkAnswer(boolean userChooseCorrect) {

        Boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if(userChooseCorrect == answerIsTrue) {
            fadeView();
            addPoints();
            toastMessageId = R.string.true_answer;

        }else {
            shakeAnimation();
            deductPoints();
            toastMessageId = R.string.wrong_answer;
        }
        Toast.makeText(MainActivity.this,toastMessageId,Toast.LENGTH_SHORT).show();
    }


    private void addPoints () {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreText.setText(String.valueOf(score.getScore()));
        scoreText.setText(String.valueOf(MessageFormat.format("Current Score:{0}", score.getScore())));
        Log.d("Score", "addPoints: " + score.getScore());
    }
    private void deductPoints () {
        scoreCounter -= 100;
        if(scoreCounter > 0) {
            score.setScore(scoreCounter);
            scoreText.setText(String.valueOf(score.getScore()));
            scoreText.setText(String.valueOf(MessageFormat.format("Current Score:{0}", score.getScore())));
        }else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreText.setText(String.valueOf(MessageFormat.format("Current Score:{0}", score.getScore())));
            Log.d("Deduct Score", "deductPoints: " + score.getScore());
        }
//        Log.d("Score", "deductPoints: " + score.getScore());

    }
    private void updateQuestions() {

        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(MessageFormat.format("{0}/{1}", currentQuestionIndex, questionList.size()));
    }
    private void fadeView() {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f , 0.0f);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    private void shakeAnimation () {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shakeanimator);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
               goNext();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void goNext() {
        currentQuestionIndex = (currentQuestionIndex + 1 ) % questionList.size();
        updateQuestions();

    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}