package com.elececo.umoney;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class TransactionUnderApprovalListview extends AppCompatActivity {


        ListView list;

        String [] Date ={
                "01-01-2022","02-01-2022",
                "03-01-2022","04-01-2022",
                "05-01-2022",
        };

        String[] Paid_To ={
                "A","B",
                "C","D",
                "E",
        };

        String [] Amount={
                "$ 100", "$ 2000", "$ 5000", "$ 100", "$ 1500"
        };
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transaction_under_approval_listview);

            TransactionApproveCard adapter=new TransactionApproveCard(this, Date, Paid_To,Amount);
            list=(ListView)findViewById(R.id.TA_Listview);
            list.setAdapter(adapter);

        }
}