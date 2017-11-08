package bob.d3.d3ext;

public class D3ExDocFields {
	/* @formatter:off
	 SELECT TOP 1000 [repository_id]
      ,[repository_text]
      ,[doc_field_nr_pref]
      ,[data_type]
      ,[data_length]
      ,[min_value]
      ,[max_value]
      ,[predefined_values]
      ,[predef_val_id]
      ,[predef_val_look_tab]
      ,[predef_val_look_col]
      ,[hook_plausi_func]
      ,[hook_get_value_func]
      ,[regular_expression]
      ,[flag_feld]
  FROM [D3P].[dbo].[field_repository]
  order by [repository_id], doc_field_nr_pref
  
  SELECT TOP 1000 [kue_dokuart]
      ,[dok_dat_feld_nr]
      ,[sprache_fispe_titel]
      ,[dok_dat_titel]
      ,[logi_position]
      ,[logi_position_tab]
      ,[modifizierbar]
      ,[werte_auswahl]
      ,[daten_typ]
      ,[laenge]
      ,[repository_id]
      ,[flag_feld]
      ,[caption_praefix]
      ,[caption_suffix]
      ,[caption_pos_1]
      ,[caption_pos_2]
      ,[caption_delimiter]
  FROM [D3P].[dbo].[fispe_titel_dokuart]
  where substring(kue_dokuart, 1, 1) != '$'
  order by [kue_dokuart], [dok_dat_feld_nr]
  
  
	 @formatter:on */

}
