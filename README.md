# d3

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

```
java -cp d3ext.jar bob.d3.indexer.MemoryIndexer "C:\Temp\d3ext\171201"
```

Database from `C:\Temp\d3ext\171201\memdb\` will readed and the Index will wrote under `C:\Temp\d3ext\171201\memidx\`.

## FinderApp

```
java -cp d3ext.jar bob.d3.finder.FinderApp "C:\Temp\d3ext\171201"
```