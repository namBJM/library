package vn.ptit.libman.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/** Entity/DTO matching the supplied module design; dates use LocalDate. */
public class Staff7 extends Member7 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String jobPosition;

    public String getJobPosition() { return jobPosition; }
    public void setJobPosition(String jobPosition) { this.jobPosition = jobPosition; }
}
