package com.heymeowcat.codefestadmin.Model;

public class Tickets {
    String docid;
    String ticketTitle;
    String ticketBody;
    String attachmentDownURL;
    String option;
    String status;
    String ticketPlacedBy;

    public String getTicketPlacedBy() {
        return ticketPlacedBy;
    }

    public void setTicketPlacedBy(String ticketPlacedBy) {
        this.ticketPlacedBy = ticketPlacedBy;
    }

    public Tickets() {
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getTicketTitle() {
        return ticketTitle;
    }

    public void setTicketTitle(String ticketTitle) {
        this.ticketTitle = ticketTitle;
    }

    public String getTicketBody() {
        return ticketBody;
    }

    public void setTicketBody(String ticketBody) {
        this.ticketBody = ticketBody;
    }

    public String getAttachmentDownURL() {
        return attachmentDownURL;
    }

    public void setAttachmentDownURL(String attachmentDownURL) {
        this.attachmentDownURL = attachmentDownURL;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
