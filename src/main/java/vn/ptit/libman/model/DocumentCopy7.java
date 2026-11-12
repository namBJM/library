package vn.ptit.libman.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/** Entity/DTO matching the supplied module design; dates use LocalDate. */
public class DocumentCopy7 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String physicalCondition;
    private Document7 document;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPhysicalCondition() { return physicalCondition; }
    public void setPhysicalCondition(String physicalCondition) { this.physicalCondition = physicalCondition; }

    public Document7 getDocument() { return document; }
    public void setDocument(Document7 document) { this.document = document; }
}
