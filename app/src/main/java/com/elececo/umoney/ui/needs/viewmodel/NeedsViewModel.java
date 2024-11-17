package com.elececo.umoney.ui.needs.viewmodel;

import android.content.Context;
import com.elececo.umoney.ui.base.BaseViewModel;

public class NeedsViewModel extends BaseViewModel {
    
    public NeedsViewModel(Context context) {
        super(context);
    }
    
    @Override
    protected String getTransactionType() {
        return "NEEDS";
    }
}
