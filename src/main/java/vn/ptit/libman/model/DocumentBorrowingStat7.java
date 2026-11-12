package vn.ptit.libman.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/** Entity/DTO matching the supplied module design; dates use LocalDate. */
public class DocumentBorrowingStat7 extends Document7 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int borrowQuantity;
    private LocalDate startDate;
    private LocalDate endDate;

    public int getBorrowQuantity() { return borrowQuantity; }
    public void setBorrowQuantity(int borrowQuantity) { this.borrowQuantity = borrowQuantity; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
