package etna.capitalsante.presenter;

import etna.capitalsante.model.Consulting;
import etna.capitalsante.model.DeleteModel;
import etna.capitalsante.model.Doctor;
import etna.capitalsante.model.Doctors;
import etna.capitalsante.model.DocumentLister;
import etna.capitalsante.model.FileType;
import etna.capitalsante.model.Login;
import etna.capitalsante.model.Structure;
import etna.capitalsante.model.addDoctor;
import etna.capitalsante.view.Consultation;
import etna.capitalsante.view.Doctros;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Personnel on 28/04/2017.
 */

public interface CS_API {

    @FormUrlEncoded
    @POST("authenticate/login")
    Call<Login> Login(@Field("email") String query_email, @Field("password") String query_pass);

    @FormUrlEncoded
    @POST("authenticate/signup")
    Call<Login> Signup(@Field("firstname") String query_FirstName, @Field("lastname") String query_LastName, @Field("email") String query_email, @Field("password") String query_pass);

    @Headers("Cache-Control: max-age=640000")
    @GET("files/myFiles")
    Call<DocumentLister> GetDocuments(@Header("x-access-token") String token);

    @FormUrlEncoded
    @POST("files/delete")
    Call<DeleteModel> DeleteDocument(@Header("x-access-token") String token, @Field("fileId") String query);

    @POST("consultation/start")
    Call<Consulting> Consult(@Header("x-access-token") String token);

    @GET("doctors")
    Call<Doctors> GetDocs(@Header("x-access-token") String token);


    @GET("data/structures")
    Call<Structure> getStructureList();

    @GET("data/typesOfFiles")
    Call<FileType> getFileTypeList();

    @GET("doctors/myDoctors")
    Call<Doctor> getDoctorList(@Header("x-access-token") String token);

    @Multipart
    @POST("files/upload")
    Call<Post> uploadWithFile(@Header("x-access-token") String token, @Part("name") RequestBody name,
                              @Part("type") RequestBody type,
                              @Part("fileDate") RequestBody fileDate,
                              @Part("fromDoctor") RequestBody fromDoctor,
                              @Part("note") RequestBody note,
                              @Part("fromStructure") RequestBody fromStructure,
                              @Part MultipartBody.Part file
    );

    @Multipart
    @POST("files/upload")
    Call<Post> upload(@Header("x-access-token") String token, @Part("name") RequestBody name,
                      @Part("type") RequestBody type,
                      @Part("fileDate") RequestBody fileDate,
                      @Part("fromDoctor") RequestBody fromDoctor,
                      @Part("note") RequestBody note,
                      @Part("fromStructure") RequestBody fromStructure
    );

    @GET("files/download/{id}")
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Header("x-access-token") String token, @Path("id") String fileid);

    @FormUrlEncoded
    @POST("doctors/addDoctor")
    Call<addDoctor> addDoctor(@Header("x-access-token") String token,
                              @Field("firstname") String firstname,
                              @Field("lastname") String lastname
    );
}