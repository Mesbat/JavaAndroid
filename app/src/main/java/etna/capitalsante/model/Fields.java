package etna.capitalsante.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Fields implements Serializable
{

    @SerializedName("statut_d_exercice")
    @Expose
    private String statutDExercice;
    @SerializedName("nom_du_medecin")
    @Expose
    private String nomDuMedecin;
    @SerializedName("code_finess")
    @Expose
    private int codeFiness;
    @SerializedName("prenom_du_medecin")
    @Expose
    private String prenomDuMedecin;
    @SerializedName("nom_region")
    @Expose
    private String nomRegion;
    @SerializedName("code_dept")
    @Expose
    private String codeDept;
    @SerializedName("libelle_long_de_la_specialite_du_medecin")
    @Expose
    private String libelleLongDeLaSpecialiteDuMedecin;
    private final static long serialVersionUID = -5448234681326982053L;

    public String getStatutDExercice() {
        return statutDExercice;
    }

    public void setStatutDExercice(String statutDExercice) {
        this.statutDExercice = statutDExercice;
    }

    public String getNomDuMedecin() {
        return nomDuMedecin;
    }

    public void setNomDuMedecin(String nomDuMedecin) {
        this.nomDuMedecin = nomDuMedecin;
    }

    public int getCodeFiness() {
        return codeFiness;
    }

    public void setCodeFiness(int codeFiness) {
        this.codeFiness = codeFiness;
    }

    public String getPrenomDuMedecin() {
        return prenomDuMedecin;
    }

    public void setPrenomDuMedecin(String prenomDuMedecin) {
        this.prenomDuMedecin = prenomDuMedecin;
    }

    public String getNomRegion() {
        return nomRegion;
    }

    public void setNomRegion(String nomRegion) {
        this.nomRegion = nomRegion;
    }

    public String getCodeDept() {
        return codeDept;
    }

    public void setCodeDept(String codeDept) {
        this.codeDept = codeDept;
    }

    public String getLibelleLongDeLaSpecialiteDuMedecin() {
        return libelleLongDeLaSpecialiteDuMedecin;
    }

    public void setLibelleLongDeLaSpecialiteDuMedecin(String libelleLongDeLaSpecialiteDuMedecin) {
        this.libelleLongDeLaSpecialiteDuMedecin = libelleLongDeLaSpecialiteDuMedecin;
    }

}
