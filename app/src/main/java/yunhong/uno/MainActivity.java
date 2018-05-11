package yunhong.uno;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import model.Card;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //define colors ane their number
    private static int[] colors = {Color.RED, Color.BLUE,Color.GREEN,Color.YELLOW};
    private static int colorsNum = 4;

    //define cards in deck
    private ArrayList<Card> deckCards = new ArrayList<Card>();
    private ArrayList<Card> userCards = new ArrayList<Card>();
    private ArrayList<Card> computerCards = new ArrayList<Card>();

    //define initial card numbers for user and computer
    private static int initialCardNum = 7;

    //define the current pile card
    private Card currentPile;

    //define a variable to define win or not
    private boolean isWin = false;
    //detect user's or computer's turn
    private boolean userTurn = true;


    //declare buttons on screen
    Button pickUpBtn, pileBtn,restartBtn;
    TextView oppoRemCardsTxt, deckRemCardsTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pickUpBtn = (Button) findViewById(R.id.pickUpBtn);
        pileBtn = (Button) findViewById(R.id.pileBtn);
        restartBtn = (Button) findViewById(R.id.restartBtn);
        oppoRemCardsTxt = (TextView)findViewById(R.id.oppRemNumText);
        deckRemCardsTxt = (TextView)findViewById(R.id.deckRemNumText);

        initStatus();

        pileBtn.setClickable(false);
        setPileBtn();

        pickUpBtn.setClickable(true);
        pickUpBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                    pickUp();
                }
            });

        restartBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                restart();
            }
        });

        setTexts();

        userTurn();
    }

    //initial the original 80 uno cards and put all of these to the deck with a random order
    public void initialDeckCards()
    {
        //there is a copy of cards
        for(int duplicate = 0; duplicate < 2; duplicate++)
        {
            for(int number = 0; number < 10; number++)
            {
                for(int color = 0; color < colorsNum; color++)
                {
                    Card newCard = new Card(number, colors[color]);
                    deckCards.add(newCard);
                }
            }
        }

        //random the result of previous ordered cards
        Collections.shuffle(deckCards);
    }

    //initial user or computer cards, get the first numbers of required cards from the deck
    public ArrayList<Card> initialPlayerCards()
    {
        ArrayList<Card> initialCards = new ArrayList<Card>();
        for(int num = 0; num < initialCardNum; num++)
        {
            Card currentCard = new Card(deckCards.get(0).getCardValue(),deckCards.get(0).getCardColor());
            initialCards.add(currentCard);
            deckCards.remove(0);
        }
        return initialCards;
    }


    //initial the start status, assume user get cards first
    public void initStatus()
    {
        initialDeckCards();
        this.userCards = initialPlayerCards();
        this.computerCards = initialPlayerCards();
        Card pileCard = new Card(deckCards.get(0).getCardValue(),deckCards.get(0).getCardColor());
        //the next card on the top of the deck is regarded as the pile
        this.currentPile = pileCard;
        setPileBtn();
        deckCards.remove(0);
        setTexts();
    }

    //all cards can be played by users
    public ArrayList<Card> cardsCanBePlayed(Card cardInPile, ArrayList<Card> playerCard)
    {
        ArrayList<Card> cardsCanUse = new ArrayList<Card>();
        for(Card card:playerCard)
        {
            if((card.getCardColor() == cardInPile.getCardColor()) || (card.getCardValue() == cardInPile.getCardValue()))
            {
                cardsCanUse.add(card);
            }
        }
        return cardsCanUse;
    }

    //declare how to pick up card from the pile by clicking button or picking from computer
    public void pickUp()
    {
        if(deckCards.size() > 0)
        {
            Card topCard = new Card(deckCards.get(0).getCardValue(), deckCards.get(0).getCardColor());
            //detect who did this action, user or computer
            if(userTurn)
            {
                userCards.add(topCard);
                deckCards.remove(0);
                userTurn = false;
                pickUpBtn.setClickable(false);
                computerTurn();
            }
            else
            {
                computerCards.add(topCard);
                deckCards.remove(0);
                userTurn = true;
                pickUpBtn.setClickable(true);
                userTurn();
            }
            setTexts();
        }
        else
        {
            Toast.makeText(this, getString(R.string.noMoreCard),Toast.LENGTH_LONG).show();
            restart();
        }
    }



    //generate a random number
    public int random(int bound)
    {
        Random rand = new Random();
        return rand.nextInt(bound);
    }

    //user's turn to play
    public void userTurn()
    {
        updateButtonsList();
        setTexts();
    }

    //computer's turn
    public void computerTurn()
    {
        ArrayList<Card> cardsCanBePlayed = cardsCanBePlayed(currentPile,computerCards);
        if(cardsCanBePlayed.size() >0)
        {
            //the probability of computer to select picking up
            int probability = random(cardsCanBePlayed.size());

            Card newPile = new Card(cardsCanBePlayed.get(probability).getCardValue(), cardsCanBePlayed.get(probability).getCardColor());
            this.currentPile = newPile;
            computerCards.remove(cardsCanBePlayed.get(probability));
            setPileBtn();
            pickUpBtn.setClickable(true);

        }
        else
        {
            pickUp();
            pickUpBtn.setClickable(true);
        }
        winCheck();
        userTurn = true;
        if(!isWin)
        {
            userTurn();
        }
    }


    //dynamic initial buttons in the liner layout, one card in user's list represents one button
    public void updateButtonsList()
    {
        //get all cards can be clicked firstly
        ArrayList<Card> canUseCards = cardsCanBePlayed(currentPile, userCards);

        //define this applies into the layout
        int j = 0;
        //define the layout which used to contain all buttons
        RelativeLayout ll = (RelativeLayout)findViewById(R.id.buttonlayout);
        //delete all items contained in the layout firstly and then recreate all buttons
        if(((RelativeLayout) ll).getChildCount() > 0)
            ((RelativeLayout) ll).removeAllViews();


        for(int i = 0; i < userCards.size(); i++)
        {
            Card nowCard = new Card(userCards.get(i).getCardValue(),userCards.get(i).getCardColor());

            //define the layout of buttons, every line has 7 buttons
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(100, 100);
            lp.leftMargin = 10 + 110 * (i%7);
            lp.topMargin = 30 + 105*j;
            if ((i+1)%7 == 0) {
                j++;
            }

            Button btn = new Button(this);
            btn.setId(i);
            final int id_ = btn.getId();
            btn.setText(nowCard.getCardValue() + "");
            btn.setBackgroundColor(nowCard.getCardColor());
            ll.addView(btn, lp);

            btn.setTag(nowCard);
            btn.setClickable(false);

            //set if the button can be viewed by the user
            if(userCards.contains(nowCard))
            {
                btn.setVisibility(View.VISIBLE);
            }
            else
            {
                btn.setVisibility(View.GONE);
                btn.clearAnimation();
            }

            //detect whether the button can be clicked
            if(canUseCards.contains(nowCard) && (userTurn))
            {
                btn.setClickable(true);
                btn.setOnClickListener(MainActivity.this);
            }
            else
            {
                btn.setClickable(false);
            }
        }

    }

    //detect if user or computer has won the game
    public void winCheck()
    {
        if((userTurn) && (userCards.size() == 0))
        {
            this.isWin = true;
            Toast.makeText(this, getString(R.string.userWin),Toast.LENGTH_LONG).show();
            restart();
        }
        else if ((!userTurn) && (computerCards.size() == 0))
        {
            this.isWin = true;
            Toast.makeText(this, getString(R.string.computerWin),Toast.LENGTH_LONG).show();
            restart();
        }
    }



    //set the text and color of the pile button
    public void setPileBtn()
    {
        pileBtn.setText(currentPile.getCardValue() + "");
        pileBtn.setBackgroundColor(currentPile.getCardColor());
    }

    //set texts of screen
    public void setTexts()
    {
        oppoRemCardsTxt.setText("has " + computerCards.size() + " cards remaining");
        deckRemCardsTxt.setText(deckCards.size() + " cards remaining in deck");
    }

    @Override
    public void onClick(View view) {
        Card currentCard = (Card) view.getTag();
        currentPile = new Card(currentCard.getCardValue(),currentCard.getCardColor());
        setPileBtn();
        userCards.remove(currentCard);
        view.setVisibility(View.GONE);
        view.clearAnimation();
        view.setClickable(false);
        winCheck();
        userTurn = false;
        updateButtonsList();

        if(!isWin)
        {
            computerTurn();
        }

    }

    public void restart()
    {
        finish();
        startActivity(getIntent());
    }
}
