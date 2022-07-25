package com.elececo.umoney;

import com.google.firebase.Timestamp;

public class TransactionCard {

    String With, Description, Amount, Tag, GivenOrTaken;
    Timestamp Timestamp;

    public TransactionCard() {
    }


    public TransactionCard(String amount, String with, String description, String tag, String givenOrTaken, Timestamp timestamp) {
        Amount = amount;
        With = with;
        Description = description;
        Tag = tag;
        GivenOrTaken = givenOrTaken;
        Timestamp = timestamp;

    }

    public com.google.firebase.Timestamp getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        Timestamp = timestamp;
    }

    public String getGivenOrTaken() {
        return GivenOrTaken;
    }

    public void setGivenOrTaken(String givenOrTaken) {
        GivenOrTaken = givenOrTaken;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

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