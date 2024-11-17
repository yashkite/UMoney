package com.elececo.umoney.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.elececo.umoney.R;
import com.elececo.umoney.BuildConfig;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupToolbar();
        setupVersionInfo();
        setupEmailSupport();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About");
    }

    private void setupVersionInfo() {
        TextView versionText = findViewById(R.id.app_version);
        String versionName = BuildConfig.VERSION_NAME;
        versionText.setText("Version " + versionName);
    }

    private void setupEmailSupport() {
        TextView emailSupport = findViewById(R.id.email_support);
        emailSupport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:contact@elececo.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "UMoney Support");
            startActivity(Intent.createChooser(intent, "Send Email"));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 