package com.elececo.umoney;

public class TransactionCard{

    ;
    String With, Description, Amount;

public TransactionCard(){}

    public TransactionCard(String amount, String with, String description) {
        Amount = amount;
        With = with;
        Description = description;
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