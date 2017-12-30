CREATE TABLE property (
	prop_doc_id		VARCHAR(30) NOT NULL,
    prop_column		VARCHAR(100),
	prop_longtext	VARCHAR(100),
	prop_value		VARCHAR(max),
	FOREIGN KEY (prop_doc_id) REFERENCES document(doc_id)
);

--ALTER TABLE property ADD FOREIGN KEY (prop_doc_id) REFERENCES document(doc_id);