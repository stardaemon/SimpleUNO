package model;

//a model class to define how to present uno card
public class Card {

    private int cardValue;
    private int cardColor;

    public Card(int value, int color)
    {
        this.cardValue = value;
        this.cardColor = color;
    }

    public int getCardValue() {
        return cardValue;
    }

    public int getCardColor() {
        return cardColor;
    }


    //override equals method to facility contains, remove and similar methods applied in main activity
    @Override
    public boolean equals (Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Card card = (Card) object;
            if (this.cardValue == card.getCardValue() && this.cardColor == card.getCardColor() ) {
                result = true;
            }
        }
        return result;
    }

}
