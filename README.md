# d3

![program flow](https://github.com/bobmin/d3/blob/master/d3tool/program_flow.png)

```
-Djava.util.logging.config.file=C:\d3\d3ext\logging.properties
```

Run Configuration/Common/Output File: `C:\Temp\d3ext\Xyz_${current_date}.log`

## ExportTool

```
java -cp d3ext.jar bob.d3.export.ExportTool "C:\Temp\d3ext\171201" --onlyDatabase
```

Database will created under `C:\Temp\d3ext\171201\memdb\`.

## MemoryIndexer

| field | type | example | note |
| ----- | ---- | ------- | ---- |
| ID    | TextField | P5730553 | |
| ERW   | StringField | pdf | lower cases |
| ART | StringField | rech | lower cases; see ```/memory_doc_doku_art.txt``` |
| EINBRING | TextField | 20130321 |
| Xyz | TextField | Karl Mustermann & Söhne KG | field names described in ```/memory_prop_longtext.txt``` |

```
java -cp d3ext.jar bob.d3.indexer.MemoryIndexer "C:\Temp\d3ext\171201"
```

Database from `C:\Temp\d3ext\171201\memdb\` will readed and the Index will wrote under `C:\Temp\d3ext\171201\memidx\`.

## FinderApp

```
java -cp d3ext.jar bob.d3.finder.FinderApp "C:\Temp\d3ext\171201"
```

### IndexSearcher

| input | note | query |
| ----- | ---- | ----- | 
| #direkt ERW:(pdf,tif) | no convert; everything after token | ERW:(pdf,tif) |
| knr = 11016 | a customer | Kunden-Nr.:(11016) |
| datum > 01.01.2009 datum < 31.12.2009 | a date range for documents | EINBRING:[20090101 TO 20091231] |

see [IndexQueryTest](https://github.com/bobmin/d3/blob/master/d3ext/src/test/java/bob/d3/finder/IndexQueryTest.java) for more examples
