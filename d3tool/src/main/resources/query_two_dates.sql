SELECT 'datum_einbring' AS name, datum_einbring AS datum FROM d3p..phys_datei WHERE doku_id = ?
UNION 
SELECT 'sterbe_datum', sterbe_datum FROM d3p..sterbe_datum_doks WHERE doku_id = ?