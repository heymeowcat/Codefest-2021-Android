package com.heymeowcat.codefestadmin.Model;

public class Products {
    String docid;
    String productName;
    String productImgDownURL;
    double productPrice;
    String productStatus;

    public Products() {
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImgDownURL() {
        return productImgDownURL;
    }

    public void setProductImgDownURL(String productImgDownURL) {
        this.productImgDownURL = productImgDownURL;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }
}
