package com.elececo.umoney.ui.wants.viewmodel;

import android.content.Context;
import com.elececo.umoney.ui.base.BaseViewModel;

public class WantsViewModel extends BaseViewModel {
    
    public WantsViewModel(Context context) {
        super(context);
    }
    
    @Override
    protected String getTransactionType() {
        return "WANTS";
    }
}
