package etna.capitalsante.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import etna.capitalsante.Interceptors.HeaderInterceptor;
import etna.capitalsante.Interceptors.ReceivedCookiesInterceptor;
import etna.capitalsante.R;
import etna.capitalsante.model.Login;
import etna.capitalsante.model.User;
import etna.capitalsante.presenter.CS_API;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Connexion extends AppCompatActivity {

    ImageView CS_logo;
    ConstraintLayout CS_load;
    TextView CS_login;
    TextView CS_Password;
    Button CS_Signup;
    EditText CS_PassField;
    EditText CS_EmailField;
    Login UserInformations;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        CS_logo = (ImageView) findViewById(R.id.Logo);
        CS_load = (ConstraintLayout) findViewById(R.id.LoadingScreen);
        CS_login = (Button) findViewById(R.id.Login);
        CS_Password = (Button) findViewById(R.id.FPass);
        CS_Signup = (Button) findViewById(R.id.SignUp);
        CS_PassField = (EditText) findViewById(R.id.Password);
        CS_EmailField = (EditText) findViewById(R.id.Email);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new ReceivedCookiesInterceptor(this));
        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://apicapitalsante.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Glide.with(getApplicationContext()).load("http://www.myhst.com/images/flaticon-png-medical/big/cardiogram.png").into(CS_logo);
        CS_login.setOnClickListener(login);
        CS_Password.setOnClickListener(password);
        CS_Signup.setOnClickListener(signup);
        CS_load.setVisibility(View.GONE);
    }

    View.OnClickListener login = new View.OnClickListener() {
        public void onClick(View v) {
            // START NEW ACIVITY IF LOGIN SUCCESSFULL
            // ****************** CODE ***************
            ApiLogin();
        }
    };

    View.OnClickListener password = new View.OnClickListener() {
        public void onClick(View v) {
        }
    };

    View.OnClickListener signup = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), inscription.class);
            finish();
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };

    private void ApiLogin() {
        CS_load.setVisibility(View.VISIBLE);
        CS_API service = retrofit.create(CS_API.class);
        final Call<Login> Login = service.Login(CS_EmailField.getText().toString(), CS_PassField.getText().toString());

        Login.enqueue(new Callback<etna.capitalsante.model.Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                UserInformations = response.body();
                if (response.code() >= 200 && response.code() <= 300) {
                    SharedPreferences.Editor memes = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    memes.putString("Token", UserInformations.getToken()).apply();
                    memes.commit();
                    Intent i = new Intent(getApplicationContext(), DocumentFilter.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User", UserInformations);
                    i.putExtras(bundle);
                    finish();
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    CS_load.setVisibility(View.GONE);
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
            public void onFailure(Call<Login> call, Throwable t) {
            }
        });
    }
}
