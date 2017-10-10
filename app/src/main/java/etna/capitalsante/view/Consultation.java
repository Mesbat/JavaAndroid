package etna.capitalsante.view;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import etna.capitalsante.Interceptors.HeaderInterceptor;
import etna.capitalsante.R;
import etna.capitalsante.model.Consulting;
import etna.capitalsante.model.Data;
import etna.capitalsante.model.DeleteModel;
import etna.capitalsante.model.DocumentLister;
import etna.capitalsante.presenter.CS_API;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Consultation extends AppCompatActivity {

    private Retrofit retrofit;
    private Button Btn;
    private TextView Code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);

        Btn = (Button) findViewById(R.id.BtnConsultation);
        Btn.setOnClickListener(GetConsultCode);
        Code = (TextView) findViewById(R.id.Code);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new HeaderInterceptor(this));
        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://apicapitalsante.herokuapp.com/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void    Consultation() {
        CS_API service = retrofit.create(CS_API.class);
        String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Token", new String());
        Call<Consulting> Docs = service.Consult(token);

        Docs.enqueue(new Callback<Consulting>() {
            @Override
            public void onResponse(Call<Consulting> call, Response<Consulting> response) {
                if (response.code() >= 200 && response.code() <= 300) {
                    Consulting consultData = response.body();
                    Code.setText(consultData.getData().getCode());
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
            public void onFailure(Call<Consulting> call, Throwable t) {

            }
        });
    }

    View.OnClickListener GetConsultCode = new View.OnClickListener() {
        public void onClick(View v) {
            Consultation();
        }
    };
}
