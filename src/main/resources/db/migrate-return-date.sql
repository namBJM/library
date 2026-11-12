-- Run once on existing SQL Server databases created before this UI update.
ALTER TABLE tblBorrowingReceipt ADD returnDate DATE NULL;
