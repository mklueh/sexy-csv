# SexyCSV

Very simple Java CSV parser based on Java 8 Streaming API and Lombok.

### Sample

```java
Path path = Path.of("some-file.csv");

SexyCSV parser = SexyCSV.builder()
        .delimiter(",")
        .hasHeader(true) //auto-use of the given header
        //.header(Arrays.asList("id", "name", "age", "country")) manual headers
        .skipRows(3)
        .rowFilter(s -> s.matches("^\\d.*")) //we are only interested in rows that start with a number
        .build();

List<Row> data = parser.parse(path)
        .collect(Collectors.toList());

Row firstRow = data.get(0);

// Access cells
String a = row.get(1);
String b = row.get("columnName")


```

### Features

- Skipping lines
- Filtering
- Header detection


