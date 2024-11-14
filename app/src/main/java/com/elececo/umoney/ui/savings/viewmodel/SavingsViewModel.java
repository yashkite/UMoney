package com.elececo.umoney.ui.savings.viewmodel;

import android.content.Context;
import com.elececo.umoney.ui.base.BaseViewModel;

public class SavingsViewModel extends BaseViewModel {
    
    public SavingsViewModel(Context context) {
        super(context);
    }
    
    @Override
    protected String getTransactionType() {
        return "SAVINGS";
    }
}
