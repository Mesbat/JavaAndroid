package etna.capitalsante.view;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import etna.capitalsante.Adapter.ListDocumentsAdapter;
import etna.capitalsante.Interceptors.HeaderInterceptor;
import etna.capitalsante.R;
import etna.capitalsante.model.Datum;
import etna.capitalsante.model.DocumentLister;
import etna.capitalsante.model.Login;
import etna.capitalsante.presenter.CS_API;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DocumentFilter extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Login UserInformations;
    FloatingActionButton CS_Import;
    RecyclerView rv;
    ConstraintLayout LoadingBar;
    private Retrofit retrofit;
    private ListDocumentsAdapter ListeDocs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_filter);
        rv = (RecyclerView) findViewById(R.id.RecyclerView911);
        GridLayoutManager layoutManager = new GridLayoutManager(rv.getContext(), 3);
        rv.setLayoutManager(layoutManager);
        ListeDocs = new ListDocumentsAdapter(this);
        rv.setAdapter(ListeDocs);
        rv.setHasFixedSize(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        UserInformations = (Login) getIntent().getExtras().getSerializable("User");
        LoadingBar = (ConstraintLayout) findViewById(R.id.LoadingScreen);
        CS_Import = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(SwitchImport);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new HeaderInterceptor(this));
        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://apicapitalsante.herokuapp.com/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DocumentListing();
    }

    View.OnClickListener SwitchImport = new View.OnClickListener() {
        public void onClick(View v) {
            overridePendingTransition(R.anim.slide_left, R.anim.def);
            Intent i = new Intent(v.getContext(), AddFile.class);
            startActivity(i);
        }
    };

    public void DocumentListing() {
        LoadingBar.setVisibility(View.VISIBLE);
        CS_API service = retrofit.create(CS_API.class);
        Call<DocumentLister> Docs = service.GetDocuments(UserInformations.getToken());

        Docs.enqueue(new Callback<DocumentLister>() {
            @Override
            public void onResponse(Call<DocumentLister> call, Response<DocumentLister> response) {
                if (response.code() >= 200 && response.code() <= 300) {
                    DocumentLister Docs = response.body();
                    ListeDocs.clear();
                    ListeDocs.AddListDocument(Docs.getData());
                    LoadingBar.setVisibility(View.INVISIBLE);
                } else {
                    try {
                        JSONObject message = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), message.get("message").toString(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<DocumentLister> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DocumentListing();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {
            overridePendingTransition(R.anim.fade_in, R.anim.def);
            Intent i = new Intent(getApplicationContext(), Consultation.class);
            startActivity(i);
        } else if (id == R.id.nav_gallery) {
        } else if (id == R.id.nav_manage) {
            overridePendingTransition(R.anim.fade_in, R.anim.def);
            Intent i = new Intent(getApplicationContext(), Doctros.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
