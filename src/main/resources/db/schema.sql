-- Shared SQL Server / H2 MSSQLServer schema. Run in an empty LibMan database.
CREATE TABLE tblMember (
 id INT IDENTITY(1,1) PRIMARY KEY,
 userName VARCHAR(50) NOT NULL UNIQUE,
 passWord NVARCHAR(255) NOT NULL,
 fullName NVARCHAR(100) NOT NULL,
 dayOfBirth DATE NOT NULL,
 email VARCHAR(100) NOT NULL,
 phone VARCHAR(15) NOT NULL
);
CREATE TABLE tblReader (
 id INT PRIMARY KEY REFERENCES tblMember(id),
 readerID VARCHAR(20) NOT NULL UNIQUE
);
CREATE TABLE tblStaff (
 id INT PRIMARY KEY REFERENCES tblMember(id),
 jobPosition NVARCHAR(50) NOT NULL
);
-- Explicit manager membership: Manager7 still extends Member7 directly.
CREATE TABLE tblManager (id INT PRIMARY KEY REFERENCES tblMember(id));
CREATE TABLE tblReaderCard (
 id INT IDENTITY(1,1) PRIMARY KEY,
 issueDate DATE NOT NULL,
 expiryDate DATE NOT NULL,
 status NVARCHAR(30) NOT NULL,
 readerId INT NOT NULL UNIQUE REFERENCES tblReader(id),
 CONSTRAINT ck_card_dates CHECK (expiryDate >= issueDate)
);
CREATE TABLE tblDocument (
 id INT IDENTITY(1,1) PRIMARY KEY,
 title NVARCHAR(255) NOT NULL,
 category NVARCHAR(100) NOT NULL,
 author NVARCHAR(150) NOT NULL,
 publisher NVARCHAR(150) NOT NULL,
 publicationYear INT NOT NULL
);
CREATE TABLE tblDocumentCopy (
 id INT IDENTITY(1,1) PRIMARY KEY,
 physicalCondition NVARCHAR(100) NOT NULL,
 documentId INT NOT NULL REFERENCES tblDocument(id)
);
CREATE TABLE tblBorrowingReceipt (
 id INT IDENTITY(1,1) PRIMARY KEY,
 borrowDate DATE NOT NULL,
 dueDate DATE NOT NULL,
 returnDate DATE NULL,
 status NVARCHAR(30) NOT NULL,
 readerId INT NOT NULL REFERENCES tblReader(id),
 librarianId INT NOT NULL REFERENCES tblStaff(id),
 CONSTRAINT ck_receipt_dates CHECK (dueDate >= borrowDate)
);
CREATE TABLE tblBorrowingReceiptDetail (
 borrowingReceiptId INT NOT NULL REFERENCES tblBorrowingReceipt(id),
 documentCopyId INT NOT NULL REFERENCES tblDocumentCopy(id),
 conditionWhenBorrowed NVARCHAR(100) NOT NULL,
 PRIMARY KEY (borrowingReceiptId, documentCopyId)
);
CREATE TABLE tblSupplier (
 id INT IDENTITY(1,1) PRIMARY KEY,
 supplierName NVARCHAR(150) NOT NULL,
 email VARCHAR(100), phoneNumber VARCHAR(15)
);
CREATE TABLE tblImportReceipt (
 id INT IDENTITY(1,1) PRIMARY KEY, importDate DATE NOT NULL,
 supplierId INT NOT NULL REFERENCES tblSupplier(id),
 librarianId INT NOT NULL REFERENCES tblStaff(id)
);
CREATE TABLE tblImportReceiptDetail (
 importReceiptId INT NOT NULL REFERENCES tblImportReceipt(id),
 documentId INT NOT NULL REFERENCES tblDocument(id),
 quantity INT NOT NULL CHECK (quantity > 0),
 PRIMARY KEY (importReceiptId, documentId)
);
CREATE INDEX ix_receipt_borrowDate ON tblBorrowingReceipt(borrowDate);
CREATE INDEX ix_copy_document ON tblDocumentCopy(documentId);
CREATE INDEX ix_detail_copy ON tblBorrowingReceiptDetail(documentCopyId);
