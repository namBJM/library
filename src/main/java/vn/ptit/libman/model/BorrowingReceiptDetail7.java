package vn.ptit.libman.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/** Entity/DTO matching the supplied module design; dates use LocalDate. */
public class BorrowingReceiptDetail7 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private BorrowingReceipt7 borrowingReceipt;
    private DocumentCopy7 documentCopy;
    private String conditionWhenBorrowed;

    public BorrowingReceipt7 getBorrowingReceipt() { return borrowingReceipt; }
    public void setBorrowingReceipt(BorrowingReceipt7 borrowingReceipt) { this.borrowingReceipt = borrowingReceipt; }

    public DocumentCopy7 getDocumentCopy() { return documentCopy; }
    public void setDocumentCopy(DocumentCopy7 documentCopy) { this.documentCopy = documentCopy; }

    public String getConditionWhenBorrowed() { return conditionWhenBorrowed; }
    public void setConditionWhenBorrowed(String conditionWhenBorrowed) { this.conditionWhenBorrowed = conditionWhenBorrowed; }
}
