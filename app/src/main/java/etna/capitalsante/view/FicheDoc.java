package etna.capitalsante.view;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;

import etna.capitalsante.Interceptors.HeaderInterceptor;
import etna.capitalsante.R;
import etna.capitalsante.model.Datum;
import etna.capitalsante.model.DeleteModel;
import etna.capitalsante.model.DocumentLister;
import etna.capitalsante.model.Login;
import etna.capitalsante.presenter.CS_API;
import etna.capitalsante.presenter.DownloadFile;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FicheDoc extends AppCompatActivity {

    private Datum Data;
    private ImageView CS_icon;
    private TextView CS_Filename;
    private TextView CS_Date;
    private TextView CS_Structure;
    private TextView CS_Doctor;
    private TextView CS_Nature;
    private TextView CS_Note;
    private Button CS_Delete;
    private Button CS_Download;
    private Retrofit retrofit;
    private Context context;

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.def);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_doc);

        context = this;
        Data = (Datum) getIntent().getExtras().getSerializable("Datum");
        CS_icon = (ImageView) findViewById(R.id.Icon);
        CS_Filename = (TextView) findViewById(R.id.Filename);
        CS_Date = (TextView) findViewById(R.id.Date);
        CS_Structure = (TextView) findViewById(R.id.Structure);
        CS_Doctor = (TextView) findViewById(R.id.Doctor);
        CS_Nature = (TextView) findViewById(R.id.Nature);
        CS_Note = (TextView) findViewById(R.id.Note);
        CS_Delete = (Button) findViewById(R.id.Delete);
        CS_Download = (Button) findViewById(R.id.Download);

        int id = getResources().getIdentifier(Data.getExtension().substring(1), "drawable", getPackageName());
        if (id != 0)
            CS_icon.setImageResource(id);
        else
            Glide.with(this)
                    .load("http://icons.iconarchive.com/icons/zhoolego/material/512/Filetype-Docs-icon.png")
                    .centerCrop()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(CS_icon);
        CS_Filename.setText("Document : " + Data.getName());
        CS_Date.setText("Date : " + Data.getFileDate());
        CS_Structure.setText("Structure : " + Data.getFromStructure());
        CS_Doctor.setText("Docteur : " + Data.getFromDoctor());
        CS_Nature.setText("Nature du document : " + Data.getType());
        CS_Note.setText("Note : " + Data.getNote());
        CS_Delete.setOnClickListener(DeleteFunc);
        CS_Download.setOnClickListener(DownloadFunc);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new HeaderInterceptor(this));
        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://apicapitalsante.herokuapp.com/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void DeleteDocument() {
        CS_API service = retrofit.create(CS_API.class);
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString("Token", new String());
        Call<DeleteModel> Docs = service.DeleteDocument(token, Data.getId());

        Docs.enqueue(new Callback<DeleteModel>() {
            @Override
            public void onResponse(Call<DeleteModel> call, Response<DeleteModel> response) {
                if (response.code() >= 200 && response.code() <= 300) {
                    DeleteModel Docs = response.body();
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.def);
                    Toast.makeText(getApplicationContext(), Docs.getMessage(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<DeleteModel> call, Throwable t) {

            }
        });
    }

    View.OnClickListener DeleteFunc = new View.OnClickListener() {
        public void onClick(View v) {
            DeleteDocument();
        }
    };

    View.OnClickListener DownloadFunc = new View.OnClickListener() {
        public void onClick(View v) {
            DownloadFile app = new DownloadFile(context, retrofit);
            app.download(Data.getId() ,Data.getName() + Data.getExtension());
        }
    };
}
