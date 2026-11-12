package vn.ptit.libman.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/** Entity/DTO matching the supplied module design; dates use LocalDate. */
public class ReaderCard7 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
    private Reader7 reader;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Reader7 getReader() { return reader; }
    public void setReader(Reader7 reader) { this.reader = reader; }
}
