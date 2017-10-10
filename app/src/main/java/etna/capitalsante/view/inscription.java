package etna.capitalsante.view;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import etna.capitalsante.Interceptors.ReceivedCookiesInterceptor;
import etna.capitalsante.R;
import etna.capitalsante.model.Login;
import etna.capitalsante.presenter.CS_API;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class inscription extends AppCompatActivity {

    ImageButton CS_home;
    Button CS_Subscribe;
    EditText CS_FirstName;
    EditText CS_LastName;
    EditText CS_Email;
    EditText CS_Password;
    ConstraintLayout CS_load;
    Login UserInformations;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        CS_FirstName = (EditText) findViewById(R.id.FirstName_Input);
        CS_LastName = (EditText) findViewById(R.id.LastName_Input);
        CS_Email = (EditText) findViewById(R.id.Email_Input);
        CS_Password = (EditText) findViewById(R.id.Password_Input);
        CS_load = (ConstraintLayout)findViewById(R.id.LoadingScreen);

        CS_home = (ImageButton) findViewById(R.id.Home);
        CS_home.setOnClickListener(home);
        CS_Subscribe = (Button) findViewById(R.id.button3);
        CS_Subscribe.setOnClickListener(subscribe);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new ReceivedCookiesInterceptor(this));
        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://apicapitalsante.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CS_load.setVisibility(View.INVISIBLE);
    }

    View.OnClickListener home = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), Connexion.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    };

    View.OnClickListener subscribe = new View.OnClickListener() {
        public void onClick(View v) {
            CS_load.setVisibility(View.VISIBLE);
            SignupApi();
        }
    };

    private void SignupApi() {
        CS_API service = retrofit.create(CS_API.class);

        Call<Login> Login = service.Signup(CS_FirstName.getText().toString(), CS_LastName.getText().toString(), CS_Email.getText().toString(), CS_Password.getText().toString());
        Login.enqueue(new Callback<etna.capitalsante.model.Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                UserInformations = response.body();
                if (response.code() >= 200 && response.code() <= 300) {
                    Intent i = new Intent(getApplicationContext(), DocumentFilter.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User", UserInformations);
                    i.putExtras(bundle);
                    finish();
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                CS_load.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }
}
