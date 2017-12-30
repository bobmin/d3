-- Verzeichnisse
----------------
select [logi_verzeichnis],[phys_verzeichnis],[lw_praefix],[server_praefix] from d3p..verzeichnisse

-- Datei + Verzeichnis
----------------------
select top 1000 * from d3p..files_datentraeger
select logi_verzeichnis, count(*) as zeilen from d3p..files_datentraeger group by logi_verzeichnis
-- logi_verzeichnis	zeilen
--			A	  706.828
--			B	  115.427
--			F	9.603.809
--			P	       66

-- erste [doku_id] je [logi_verzeichnis]
----------------------------------------
WITH t1 AS (
    SELECT t0.logi_verzeichnis, t0.doku_id
    , ROW_NUMBER() OVER(PARTITION BY t0.logi_verzeichnis ORDER BY t0.doku_id DESC) AS rk
    FROM d3p..files_datentraeger t0
)
SELECT t2.* FROM t1 t2 WHERE t2.rk = 1

-- [doku_id] in "firmen_spezifisch" eindeutig
---------------------------------------------
select count(*) as zeilen from d3p..firmen_spezifisch
-- 10.302.937
select count(*) as zeilen from (
	select [doku_id], count(*) as zeilen from d3p..firmen_spezifisch group by [doku_id] having count(*) = 1
) t1
-- 10.302.937

-- [doku_id] in "sterbe_datum_doks" eindeutig
---------------------------------------------
select top 1000 doku_id from d3p..sterbe_datum_doks group by doku_id having count(*) > 1

-- [doku_id] in "files_datentraeger" NICHT eindeutig
----------------------------------------------------
select top 100 doku_id, count(*) as zeilen from d3p..files_datentraeger group by doku_id having count(*) > 1
select top 100 * from d3p..files_datentraeger where doku_id = 'P008280797'

SELECT TOP 1000 [doku_id]
      ,[datum_einbring]
      ,[letzte_aender_nr]
      ,[archiviert]
      ,[frei_o_gesperrt]
      ,[plan_geprueft]
      ,[aender_nr_in_frei]
      ,[aktu_verzeichnis]
      ,[logi_verzeichnis]
      ,[bearbeiter]
      ,[dokuart]
      ,[zeich_nr]
      ,[var_nr]
      ,[doku_nr]
      ,[erstell_system]
      ,[version]
      ,[text]
      ,[server]
      ,[berech_all]
      ,[zugr_schutz]
      ,[besitzer]
      ,[werk_bez]
      ,[abteilung]
      ,[konstrukteur]
      ,[zustaendiger]
      ,[dateiname]
      ,[datei_erw]
      ,[datei_typ]
      ,[verzeichnis]
      ,[encrypt]
      ,[anzahl_zugriffe]
      ,[dat_letzter_zugr]
      ,[ben_letzter_zugr]
      ,[ua_id]
      ,[sprach_id]
      ,[zustand]
      ,[kzhd]
      ,[adf_id]
      ,[snr_1_2_3]
      ,[snr_c_d_druck]
      ,[avv_max_zustand]
      ,[last_update_file]
      ,[web_published]
      ,[workflow]
      ,[size_in_byte]
      ,[datei_erw_in_frei]
      ,[attr_exported]
      ,[ocr_exported]
      ,[signatures_required]
      ,[overall_proc_date]
  FROM [D3P].[dbo].[phys_datei]
  where doku_id in ('P2883797','P5730553')
  order by doku_id
  
select top 100 * from d3p..files_datentraeger where doku_id = 'P2883797'