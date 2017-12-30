select *
from d3p..sterbe_datum_doks s 
inner join d3p..firmen_spezifisch f on s.doku_id = f.doku_id 
inner join d3p..phys_datei        d on s.doku_id = d.doku_id
order by s.doku_id, s.sterbe_datum desc