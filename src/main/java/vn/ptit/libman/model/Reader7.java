package vn.ptit.libman.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/** Entity/DTO matching the supplied module design; dates use LocalDate. */
public class Reader7 extends Member7 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String readerID;
    private ReaderCard7 readerCard;

    public String getReaderID() { return readerID; }
    public void setReaderID(String readerID) { this.readerID = readerID; }

    public ReaderCard7 getReaderCard() { return readerCard; }
    public void setReaderCard(ReaderCard7 readerCard) { this.readerCard = readerCard; }
}
