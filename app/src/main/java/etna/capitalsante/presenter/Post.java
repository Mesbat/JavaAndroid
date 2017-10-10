package etna.capitalsante.presenter;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import etna.capitalsante.Interceptors.HeaderInterceptor;
import etna.capitalsante.model.addDoctor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static java.lang.System.out;

/**
 * Created by Nouri on 03/05/2017.
 */

public class Post {

    private static Retrofit retrofit;
    private static String name;
    private static String type;
    private static String note;
    private static String doctor;
    private static String fileDate = "";
    private static String structure;

    public static void postForm(Uri fileUri, File file, final Context context, boolean hasFile) {

        out.println("post uri : " + fileUri);
        MultipartBody.Part body = null;
        Call<Post> call;

        if (hasFile) {
            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(context.getContentResolver().getType(fileUri)),
                            file
                    );

            // MultipartBody.Part is used to send also the actual file name
            body =
                    MultipartBody.Part.createFormData("files", file.getName(), requestFile);
        }

        // add another part within the multipart request
        RequestBody nameString = RequestBody.create(MultipartBody.FORM, name);
        RequestBody typeString = RequestBody.create(MultipartBody.FORM, type);
        RequestBody fromDoctorString = RequestBody.create(MultipartBody.FORM, doctor);
        RequestBody fromStructureString = RequestBody.create(MultipartBody.FORM, structure);
        RequestBody fileDateString = RequestBody.create(MultipartBody.FORM, fileDate);
        RequestBody noteString = RequestBody.create(MultipartBody.FORM, note);

        // finally, execute the request
        retrofit = ApiClient.getClient("https://apicapitalsante.herokuapp.com/api/", context);
        CS_API service = retrofit.create(CS_API.class);
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString("Token", new String());

        if (hasFile) {
            call = service.uploadWithFile(token, nameString, typeString, fileDateString, fromDoctorString, noteString, fromStructureString, body);
        } else {
            call = service.upload(token, nameString, typeString, fileDateString, fromDoctorString, noteString, fromStructureString);
        }
        out.println("call:  --  "+call);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Toast.makeText(context, "upload réussi", Toast.LENGTH_LONG).show();
                Log.v("Upload", "success");
                Log.v("mesage", String.valueOf(response));
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(context, "upload echoué", Toast.LENGTH_LONG).show();
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public static void addDoctor(String nom, String prenom, final Context context) {

        retrofit = ApiClient.getClient("https://apicapitalsante.herokuapp.com/api/", context);
        CS_API service = retrofit.create(CS_API.class);
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString("Token", new String());

        Call<addDoctor> call = service.addDoctor(token, nom, prenom);
        out.println("call:  --  "+call);
        call.enqueue(new Callback<addDoctor>() {
            @Override
            public void onResponse(Call<addDoctor> call, Response<addDoctor> response) {
                Toast.makeText(context, "Ajout réussi", Toast.LENGTH_LONG).show();
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<addDoctor> call, Throwable t) {
                Toast.makeText(context, "Erreur, ajout echoué", Toast.LENGTH_LONG).show();
                Log.e("error:", t.getMessage());
            }
        });
    }

    public static void setName(String name) {
        Post.name = name;
    }

    public static void setNote(String note) {
        Post.note = note;
    }

    public static void setDoctor(String doctor) {
        Post.doctor = doctor;
    }

    public static void setStructure(String structure) {
        Post.structure = structure;
    }

    public static void setType(String type) {
        Post.type = type;
    }

    public static void setFileDate(String fileDate) {
        Post.fileDate = fileDate;
    }
}
