[DEPRECATED] *This project was moved to the company's internal repository where I work and is not actively maintained anymore here.*


# SexyCSV

Java CSV parser / creator with the goal of providing the leanest API possible - based on Java 8 Streaming API

Deals with certain edge cases other parsers aren't capable of and does not require
pre-processing for metadata trimming.


### Use-Case

You don´t want to rely on rather big libraries such as Apache Commons CSV, dislike their API or lacking features such as
line skipping, filling missing cells correctly with null using tabulator delimiter this library might be all you need.

### Features

SexyCsv consists of two components:

**1. SexyCsv.Parser (Read CSV)**

**2. SexyCsv.Creator (Write CSV)**

and works with Java Annotations to map header columns to object fields

````java
@Data
public class TestEntity {

    @CSVColumn(value = "id", position = 0)
    public String id;

    @CSVColumn(value = "name", position = 1)
    public String name;

    @CSVColumn(value = "age")
    private Integer age;

    @CSVColumn(value = "country")
    private String country;
}
````

---

##### SexyCsv.Parser

- Automated header detection
    - Based on header row in CSV files
    - Based on Java class entity
- Parsing CSV files directly to Java Object Streams by using the **@CsvColumn** annotation on column fields
- Parsing CSV files to Row Streams
- Skipping lines above header
- Duplicated header detection
- Filtering out invalid data
- Setting tokenizer for special cases
- Correctly dealing with missing cells when using tab delimiter

###### Simple

```java
Path path = Path.of("some-file.csv");

  SexyCSV.Parser.builder()
         .delimiter(",")
         .hasHeaderRow(true)
         .build()
         .parse(path)
         .forEach(row->{
             String value = row.get("columnName");
         });
```

###### Extended

```java
Path path = Path.of("some-file.csv");

SexyCSV.Parser parser = SexyCSV.Parser.builder()
                               .delimiter(",")
                               .hasHeaderRow(true) //auto-use of the given header in the file
                               //.header(Arrays.asList("id", "name", "age", "country")) set manual header
                               .skipRows(3)
                               .rowFilter(s->s.matches("^\\d.*")) //we are only interested in rows that start with a number
                               //.tokenizer(s -> s.split(";")) optional custom tokenizer
                               .build();

List<Row> data = parser.parse(path)
                       .collect(Collectors.toList());

Row firstRow = data.get(0);

// Access cells
String a = row.get(1);
String b = row.get("columnName")

//or simply parsing to your Java entity

Stream<TestEntity> entities = parser.parse(path,TestEntity.class);


```

##### SexyCsv.Creator

- Automated header creation
    - Based on Java class entity
- Streaming API
- Streaming based prepending of meta information above the header
- Column order rearranging   
- TypeConverters to deal with custom type serialization

````java
SexyCSV.Creator<TestEntity> creator = SexyCSV.Creator.<TestEntity>builder()
        .withEntity(TestEntity.class)
        .withTypeConverter(LocalDateTime.class, new LocalDateTimeConverter())
        .build();

List<TestEntity> entities = new ArrayList<>();

//... add some entities

String csvString = creator.toString(entities.stream());

Stream<String> csvRowStream = creator.toStream(entities.stream());

Path path = Path.of("test.csv");
creator.toFile(entities.stream(), path);
        
````


Add custom type converters based on the Java Function interface
````java
public class LocalDateTimeConverter implements Function<Object, String> {

    static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public String apply(Object localDateTime) {
        return ((LocalDateTime) localDateTime).format(formatter);
    }

}
````

### Short API Overview

##### SexyCsv.Parser

Parses CSV files to Stream of Row or Entity

config

- delimiter
- hasHeader
- withEntity
- charset
- tokenizer

parser

- parse(Path) ~ Stream<Row>
- parse(Path,Entity.class) ~ Stream<Entity>

##### SexyCsv.Creator

Creates CSV files based on Streams, Strings or Entities

config

- delimiter
- prependingHeader(List<String>)
- withTypeConverter(Function<Object,String>)

creator

- toString(Stream<Entity>) ~ String
- toStream(Stream<Entity>) ~ Stream<String>
- toFile(Stream<Entity>,Path) ~ void

### Source

[https://github.com/mklueh/sexy-csv](https://github.com/mklueh/sexy-csv)

### Project Page

[https://mklueh.github.io/sexy-csv/](https://mklueh.github.io/sexy-csv/)
