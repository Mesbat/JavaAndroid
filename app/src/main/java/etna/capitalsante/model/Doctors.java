package etna.capitalsante.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Doctors implements Serializable
{

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private List<DoctorData> data = null;
    private final static long serialVersionUID = 2084842297914635803L;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DoctorData> getData() {
        return data;
    }

    public void setData(List<DoctorData> data) {
        this.data = data;
    }

}

