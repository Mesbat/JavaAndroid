package etna.capitalsante.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.BuildConfig;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import etna.capitalsante.R;
import etna.capitalsante.model.Doctor;
import etna.capitalsante.model.FileType;
import etna.capitalsante.model.Structure;
import etna.capitalsante.presenter.ApiClient;
import etna.capitalsante.presenter.CS_API;
import etna.capitalsante.presenter.DownloadFile;
import etna.capitalsante.presenter.ImageFromCamera;
import etna.capitalsante.presenter.Post;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static etna.capitalsante.presenter.documentFromFileManager.getPath;
import static java.lang.System.out;

public class AddFile extends AppCompatActivity implements View.OnFocusChangeListener {

    private Retrofit retrofit;
    private EditText nameField;
    private EditText noteField;
    private EditText fileDate;
    private Spinner doctorSpinner;
    private Spinner structureSpinner;
    private Spinner typeSpinner;
    private Button btnDocument;
    private TextView nomDocument;
    private final Context context = this;
    private Uri fileURI = null;
    private File finalFile;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int PICKFILE_RESULT_CODE = 10;
    private boolean noteType = false;
    private String filename;
    private Button downloadFile;
    private final Context parent = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);

        // Récupération des champs de formulaire
        nameField = (EditText) findViewById(R.id.EditTextName);
        noteField = (EditText) findViewById(R.id.EditTextFeedbackNote);
        doctorSpinner = (Spinner) findViewById(R.id.SpinnerDoctor);
        structureSpinner = (Spinner) findViewById(R.id.SpinnerStructure);
        typeSpinner = (Spinner) findViewById(R.id.SpinnerkType);
        btnDocument = (Button) findViewById(R.id.btnDocument);
        fileDate = (EditText) findViewById(R.id.fileDate);
        nomDocument = (TextView) findViewById(R.id.nomDocument);

        // Création d'un listner sur les type des note ( pour ajouter un document ou pas )
        ListnerTypeSpinner();
        FocusDateField();
        if (noteType)
            FocusNoteField();

        btnDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                final CharSequence[] items = {"Prendre une photo", "Récupérer un document"};

                alertDialogBuilder.setTitle(R.string.ajouter_document)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                            startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE);
                                        }
                                        break;
                                    case 1:
                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                        intent.setType("*/*");
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        Intent chooser = Intent.createChooser(intent, "Select a File to Upload");
                                        try {
                                            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),PICKFILE_RESULT_CODE);
                                        } catch (Exception ex) {
                                            System.out.println("browseClick :"+ex);//android.content.ActivityNotFoundException ex
                                        }
                                        break;
                                }
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
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
        });

        // Remplir les selects de formulaires en recupérant les information de l'API (Médecins, Type, Structure)
        setStructureListSpinner();
        setDoctorListSpinner();
        setTypeListSpinner();

        // Mettre un focus sur le champ Nom pour afficher prévenir le user s'il est vide
        FocusNameField();

    }

    protected void FocusNameField() {

        nameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && TextUtils.isEmpty(nameField.getText().toString())) {
                    nameField.setError("Champ obligatoire");

                }
            }
        });
    }

    protected void FocusNoteField() {

        noteField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && TextUtils.isEmpty(noteField.getText().toString())) {
                    noteField.setError("Champ obligatoire");

                }
            }
        });
    }

    // focus sur le champ date et verifier s'il n'est vide
    protected void FocusDateField() {

        fileDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && TextUtils.isEmpty(fileDate.getText().toString())) {
                    fileDate.setError("Champ obligatoire");

                }
            }
        });
    }

    //Afficher ou cacher le boutton pour upload document
    protected void ListnerTypeSpinner() {

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = parent.getItemAtPosition(position).toString();
                if (!choice.equals("note")) {
                    noteType = false;
                    btnDocument.setVisibility(View.VISIBLE);
                    nomDocument.setVisibility(View.VISIBLE);
                }
                else {
                    noteType = true;
                    btnDocument.setVisibility(View.GONE);
                    nomDocument.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Requete Get pour récupérer les structures
    protected void setStructureListSpinner() {

        retrofit = ApiClient.getClient("https://apicapitalsante.herokuapp.com/api/", this);
        CS_API service = retrofit.create(CS_API.class);
        final Call<Structure> StructureList = service.getStructureList();

        StructureList.enqueue(new Callback<Structure>() {
            @Override
            public void onResponse(Call<Structure> call, Response<Structure> response) {
                out.println("sucsess : ----------------- "+response);
                Structure data = response.body();
                setSpinner(structureSpinner, data.getData());
            }

            @Override
            public void onFailure(Call<Structure> call, Throwable t) {
                out.println("Error : ---------------- "+t);
            }
        });
    }

    // Requete Get pour récupérer les médecins
    protected void setDoctorListSpinner() {

        retrofit = ApiClient.getClient("https://apicapitalsante.herokuapp.com/api/", this);
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString("Token", new String());
        CS_API service = retrofit.create(CS_API.class);
        final Call<Doctor> DoctorList = service.getDoctorList(token);

        DoctorList.enqueue(new Callback<Doctor>() {
            @Override
            public void onResponse(Call<Doctor> call, Response<Doctor> response) {
                out.println("sucsess : ----------------- "+response);
                Doctor data = response.body();
                if (data != null)
                    setSpinner(doctorSpinner, data.getData());
            }

            @Override
            public void onFailure(Call<Doctor> call, Throwable t) {
                out.println("Error : ---------------- "+t);
            }
        });
    }

    // Requete Get pour récupérer les types
    protected void setTypeListSpinner() {

        retrofit = ApiClient.getClient("https://apicapitalsante.herokuapp.com/api/", this);
        CS_API service = retrofit.create(CS_API.class);
        final Call<FileType> FileTypeList = service.getFileTypeList();

        FileTypeList.enqueue(new Callback<FileType>() {
            @Override
            public void onResponse(Call<FileType> call, Response<FileType> response) {
                out.println("sucsess : ----------------- "+response);
                FileType data = response.body();
                setSpinner(typeSpinner, data.getData());
            }

            @Override
            public void onFailure(Call<FileType> call, Throwable t) {
                out.println("Error : ---------------- "+t);
            }
        });
    }

    // Remplir le select passé en paramètre
    protected void setSpinner(Spinner spinner, List<String> data) {

        ArrayList<String> ArrayList = new ArrayList<String>();
        for (int i = 0; i < data.size(); i++) {
            ArrayList.add(data.get(i));
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ArrayList);
        spinner.setAdapter(adp);
    }

    // Valider le formulaire
    public void sendFeedback(View button) {
        final String name = nameField.getText().toString();
        String note = noteField.getText().toString();
        String doctor = doctorSpinner.getSelectedItem().toString();
        String structure = structureSpinner.getSelectedItem().toString();
        String type = typeSpinner.getSelectedItem().toString();
        String fileDateString = fileDate.getText().toString();
        // setter of form
        Post.setName(name);
        Post.setNote(note);
        Post.setDoctor(doctor);
        Post.setStructure(structure);
        Post.setType(type);
        Post.setFileDate(fileDateString);

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(fileDateString)) {

            if (type.equals("note")) {
                if (!TextUtils.isEmpty(note)) {
                    Post.postForm(fileURI, finalFile, this, false);
                    out.println("post sans ficher");
                }
                else {
                    Toast.makeText(this, "Veuillez remplir la note", Toast.LENGTH_LONG).show();
                    out.println("note vide");
                }
                //display toast
            }
            else {
                if (fileURI != null) {
                    Post.postForm(fileURI, finalFile, this, true);
                    out.println("post avec ficher");
                }
                else
                    Toast.makeText(this, "Aucun document choisi", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_LONG).show();
            out.println("Remplir tous les champs");
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String path = "";
        String sourcePath = "";
        Uri uri = null;

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            ImageFromCamera imageApp = new ImageFromCamera();
            fileURI = imageApp.getImageUri(getApplicationContext(), photo);
            finalFile = new File(imageApp.getRealPathFromURI(fileURI, this));
            System.out.println(fileURI);
            filename = finalFile.getName();
            nomDocument.setText(filename);
            //System.out.println(getRealPathFromURI(tempUri));

        }

        else  if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    uri = data.getData();
                    String mimeType = getContentResolver().getType(uri);

                    path = getPath(this, uri);
                    out.println("path    :"+ path);

                    if (path == null) {
                        if (mimeType == null) {
                            path = getPath(this, uri);
                            if (path == null) {
                                filename = FilenameUtils.getName(uri.toString());
                            } else {
                                File file = new File(path);
                                filename = file.getName();
                            }
                        } else {
                            Uri returnUri = data.getData();
                            Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                            returnCursor.moveToFirst();
                            filename = returnCursor.getString(nameIndex);
                            String size = Long.toString(returnCursor.getLong(sizeIndex));
                        }
                    }
                    File fileSave = getExternalFilesDir(null);
                    sourcePath = getExternalFilesDir(null).toString();
                    out.println("source path ------ : "+sourcePath);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                out.println("filename    :"+ filename);
                out.println("chemin    :"+ sourcePath + "/" + filename);
                out.println("path file    :"+ path);
                out.println("uri    :"+ uri);


                //fileURI = FileProvider.getUriForFile(this,
                //      BuildConfig.APPLICATION_ID + ".provider", new File("/"));
                //fileURI = new Uri.Builder().scheme("content")
                  //      .authority(BuildConfig.APPLICATION_ID + ".provider").encodedPath(path).build();


                finalFile = new File(path);
               // fileURI = Uri.parse(new File(path).toString());
                fileURI = uri;
                nomDocument.setText(finalFile.getName());
            }
        }
    }
}
