# SexyCSV

Very tiny Java CSV parser based on Java 8 Streaming API and Lombok that is simple to configure.


### Usecase

You don´t want to rely on rather big libraries such as Apache Commons CSV, dislike their API or lacking features such
as line skipping, filling missing cells correctly with null using tabulator delimiter this library might be all you
need.


### Sample


#### Simple

```java
Path path = Path.of("some-file.csv");

SexyCSV.builder()
        .delimiter(",")
        .hasHeader(true)
        .build()
        .parse(path)
        .forEach(row -> {
            String b = row.get("columnName");
        });



```

#### Extended

```java
Path path = Path.of("some-file.csv");

SexyCSV parser = SexyCSV.builder()
                        .delimiter(",")
                        .hasHeader(true) //auto-use of the given header
                        //.header(Arrays.asList("id", "name", "age", "country")) set manual headers
                        .skipRows(3)
                        .rowFilter(s -> s.matches("^\\d.*")) //we are only interested in rows that start with a number
                        //.tokenizer(s -> s.split(";")) optional custom tokenizer
                        .build();

List<Row> data = parser.parse(path)
.collect(Collectors.toList());

Row firstRow = data.get(0);

// Access cells
String a = row.get(1);
String b = row.get("columnName")


```

### Features

- Header detection
- Skipping lines above header
- Filtering out invalid data
- Setting tokenizer for special cases
- Dealing with missing cells using tab delimiter



