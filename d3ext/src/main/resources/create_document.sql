CREATE TABLE document (
	doc_id			VARCHAR(30) NOT NULL,
	doc_doku_art	VARCHAR(100),
    doc_nummer		INT,
	doc_folder		VARCHAR(10),
	doc_name		VARCHAR(50),
	doc_ext			VARCHAR(3),
	doc_bytes		BIGINT
);