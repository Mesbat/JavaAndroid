package etna.capitalsante.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import etna.capitalsante.Interceptors.HeaderInterceptor;
import etna.capitalsante.R;
import etna.capitalsante.model.DeleteModel;
import etna.capitalsante.model.Doctors;
import etna.capitalsante.model.DocumentLister;
import etna.capitalsante.presenter.CS_API;
import etna.capitalsante.presenter.Post;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.System.out;

public class Doctros extends AppCompatActivity {

    private Retrofit retrofit;
    private Doctors Docteurs;
    ListView mListView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctros);
        mListView = (ListView) findViewById(R.id.ListViewDoc);
        context = this;

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new HeaderInterceptor(this));
        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://apicapitalsante.herokuapp.com/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PrintDoctors();
        mListView.setOnItemClickListener(clickItem);
    }

    private void PrintDoctors() {
        CS_API service = retrofit.create(CS_API.class);
        String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Token", new String());
        final Call<Doctors> Docs = service.GetDocs(token);

        Docs.enqueue(new Callback<Doctors>() {
            @Override
            public void onResponse(Call<Doctors> call, Response<Doctors> response) {
                if (response.code() >= 200 && response.code() <= 300) {
                    Docteurs = response.body();
                    String[] names = new String[Docteurs.getData().size()];
                    int i;
                    for (i = 0; i < Docteurs.getData().size(); ++i) {
                        names[i] = "Dr." + Docteurs.getData().get(i).getFields().getNomDuMedecin();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, names);
                    mListView.setAdapter(adapter);
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
            public void onFailure(Call<Doctors> call, Throwable t) {

            }
        });

    }

    AdapterView.OnItemClickListener clickItem = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String item = (String) mListView.getItemAtPosition(position);
            item = item.replace("Dr.","");
            //out.println("item : "+item);

            //List<String> items = Arrays.asList(item.split(" "));

            //String prenom = items.get(1);
            //String nom = items.get(0);

            for (int i = 0; i < Docteurs.getData().size(); ++i) {
                if (Docteurs.getData().get(i).getFields().getNomDuMedecin().equals(item)) {

                    String prenom = Docteurs.getData().get(i).getFields().getPrenomDuMedecin();
                    String nom = Docteurs.getData().get(i).getFields().getNomDuMedecin();
                    addDoctorTolist(nom, prenom);
                    break;
                }
            }


        }
    };

   private void addDoctorTolist(final String nom, final String prenom) {
       AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

       alertDialogBuilder.setTitle(R.string.ajouter_medecin)
       .setMessage("Voulez vous ajouter ce médecin à votre liste ?")

               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       Post.addDoctor(nom, prenom, context);
                   }
               })
               .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });


       alertDialogBuilder.create();
       AlertDialog alertDialog = alertDialogBuilder.create();
       alertDialog.show();
   }
}
