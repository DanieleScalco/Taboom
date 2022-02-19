package it.bastoner.taboom.utilities;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

public class MyCreateDocument extends ActivityResultContracts.CreateDocument {

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String input) {
        Intent intent = super.createIntent(context, input);
        intent.setType("text/plain")
              .addCategory(Intent.CATEGORY_OPENABLE);
        return  intent;
    }

}
