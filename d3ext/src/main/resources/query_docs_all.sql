select *
from d3p..sterbe_datum_doks s 
inner join d3p..firmen_spezifisch f on s.doku_id = f.doku_id 
inner join d3p..phys_datei        d on s.doku_id = d.doku_id
where s.doku_id in ('P2883797', 'P5730553', 'P2884149', 'P5733191', 'P010440642')
order by s.doku_id, s.sterbe_datum desc