CREATE TABLE document (
	doc_id				VARCHAR(30) NOT NULL,
	doc_einbring_datum	DATE,
	doc_doku_art		VARCHAR(100),
    doc_nummer			INT,
	doc_folder			VARCHAR(10),
	doc_name			VARCHAR(50),
	doc_ext				VARCHAR(10),
	doc_bytes			BIGINT,
	doc_sterbe_datum	DATE
);

-- ALTER TABLE document ADD COLUMN IF NOT EXISTS doc_einbring_datum DATE
-- ALTER TABLE document ADD COLUMN IF NOT EXISTS doc_sterbe_datum DATE