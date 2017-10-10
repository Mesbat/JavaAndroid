package etna.capitalsante.presenter;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;
import static java.lang.System.out;

/**
 * Created by Nouri on 04/05/2017.
 */

public class DownloadFile {

    private Retrofit retrofit;
    private Context context;

    public DownloadFile(Context context, Retrofit retrofit) {
        this.context = context;
        this.retrofit = retrofit;
    }

    public void download(String fileId, final String filename) {

        CS_API service = retrofit.create(CS_API.class);
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString("Token", new String());
        Call<ResponseBody> call = service.downloadFileWithDynamicUrlSync(token, fileId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(), filename);
                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                if (writtenToDisk)
                    Toast.makeText(context, "Téléchargement réussi!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, "Erreur, le téléchargement a echoué", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "download was failed ");

            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String filename) {
        try {
            // todo change the file location/name according to your needs
            out.println("filename: ----- " + filename);
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + filename);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                try {
                    outputStream = new FileOutputStream(futureStudioIconFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;

            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
