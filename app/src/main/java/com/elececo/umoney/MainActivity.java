package com.elececo.umoney;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    GoogleSignInClient mGoogleSignInClient;

    BottomNavigationView bottomNavigationView;
    MenuItem mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.Dashboard);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        signOut();

    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Toast.makeText(MainActivity.this, "User SignOut", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    Dashboard Dashboard = new Dashboard();
    Needs Needs = new Needs();
    Wants Wants = new Wants();
    Savings Savings = new Savings();




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, Dashboard).commit();
                return true;

            case R.id.Needs:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, Needs).commit();
                return true;

            case R.id.Wants:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, Wants).commit();
                return true;

            case R.id.Savings:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, Savings).commit();
                return true;
        }
        return false;
    }


}