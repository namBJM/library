package vn.ptit.libman.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/** Entity/DTO matching the supplied module design; dates use LocalDate. */
public class BorrowingReceipt7 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate returnDate;
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate value) { returnDate=value; }
    private int id;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String status;
    private Reader7 reader;
    private Librarian7 librarian;
    private List<BorrowingReceiptDetail7> dsBorrowingReceiptDetail = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Reader7 getReader() { return reader; }
    public void setReader(Reader7 reader) { this.reader = reader; }

    public Librarian7 getLibrarian() { return librarian; }
    public void setLibrarian(Librarian7 librarian) { this.librarian = librarian; }

    public List<BorrowingReceiptDetail7> getDsBorrowingReceiptDetail() { return dsBorrowingReceiptDetail; }
    public void setDsBorrowingReceiptDetail(List<BorrowingReceiptDetail7> dsBorrowingReceiptDetail) { this.dsBorrowingReceiptDetail = dsBorrowingReceiptDetail; }
}
