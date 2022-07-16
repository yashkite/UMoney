package com.elececo.umoney;

public class TransactionCard {

    String With, Description, Amount, Tag, GivenOrTaken;

    public TransactionCard() {
    }


    public TransactionCard(String amount, String with, String description, String tag, String givenOrTaken) {
        Amount = amount;
        With = with;
        Description = description;
        Tag = tag;
        GivenOrTaken = givenOrTaken;
               
    }

    public  String getGivenOrTaken() {   return GivenOrTaken;  }

    public void setGivenOrTaken(String givenOrTaken) { GivenOrTaken = givenOrTaken; }

    public String getTag() { return Tag; }

    public void setTag(String tag) { Tag = tag; }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getWith() {
        return With;
    }

    public void setWith(String with) {
        With = with;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}