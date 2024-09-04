package com.elececo.umoney;

import com.google.firebase.Timestamp;

public class TransactionCard {

    String With, Description, Amount, Tag, GivenOrTaken, DocID;
    Timestamp Timestamp;

    public TransactionCard() {
    }

    public TransactionCard(String with, String description, String amount, String tag, String givenOrTaken, String docID, com.google.firebase.Timestamp timestamp) {
        With = with;
        Description = description;
        Amount = amount;
        Tag = tag;
        GivenOrTaken = givenOrTaken;
        DocID = docID;
        Timestamp = timestamp;
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

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getGivenOrTaken() {
        return GivenOrTaken;
    }

    public void setGivenOrTaken(String givenOrTaken) {
        GivenOrTaken = givenOrTaken;
    }

    public String getDocID() {
        return DocID;
    }

    public void setDocID(String docID) {
        DocID = docID;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        Timestamp = timestamp;
    }

}