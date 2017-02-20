package com.flicker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.flicker.details.DetailFragment;
import com.flicker.fragments.SearchFragment;

import java.util.List;

public class FlickerActivity extends AppCompatActivity implements FlickerView {

    private SearchView searchText;

    private FlickerPresenter presenter = new FlickerPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flicker);

        searchText = (SearchView) findViewById(R.id.search_text);

        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchText.getWindowToken(), 0);

                presenter.findImages(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        replaceFragment(new SearchFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.subscribe(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        presenter.unSubscribe();
    }

    @Override
    public void displayImages(List<Photo> photos) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof SearchFragment) {
            ((SearchFragment) fragment).displayImages(photos);
        }
    }

    @Override
    public void displayErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commitNow();
    }

    public void showDetailsFragment(DetailFragment fragment) {
        searchText.setVisibility(View.GONE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        searchText.setVisibility(View.VISIBLE);
    }
}
