package com.heymeowcat.codefest2021androidproject.Model;

public class Customers {
    String docid;
    String name;
    String nic;
    String telephone;
    String email;
    String gender;
    String profilePicDownloadUrl;

    public Customers() {
    }


    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePicDownloadUrl() {
        return profilePicDownloadUrl;
    }

    public void setProfilePicDownloadUrl(String profilePicDownloadUrl) {
        this.profilePicDownloadUrl = profilePicDownloadUrl;
    }
}
