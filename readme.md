# Data Visualization Using R

A small data visualization project for the SWE 307 Big Data course.

The project reads data from MongoDB using Java, sends the data to an R plotting function through GraalVM, and displays the generated graph in a web browser. The graph is updated continuously as new values are read from the database.

The project requirements are included separately in this repository.

## Technologies Used

- Java 17
- Spring Boot
- MongoDB
- R
- R `lattice` package
- GraalVM / FastR
- Maven
- HTML and JavaScript

## How It Works

The project follows this flow:

```text
MongoDB
   ↓
Java / Spring Boot
   ↓
GraalVM / FastR
   ↓
R plotting function
   ↓
PNG image
   ↓
Spring Boot endpoint
   ↓
Web browser
```

Java is responsible for reading the data from MongoDB. R does not access the database directly.

Every second, Java reads the next value from `Col-1` and sends the collected values to the R plotting function. R generates a PNG graph using `lattice::xyplot()`.

The browser requests the generated image and refreshes it every second so the graph can change dynamically.

## Database

The data is stored in a MongoDB database, and the data is imported from a CSV file to MongoDB using mongoimport. Each row from the CSV file is stored as a MongoDB document.

An `index` field was added to make it easy for Java to retrieve the rows in order:

```text
1 → 2 → 3 → ... → 100
```

Java uses this index to determine which value should be read next.

## Java and MongoDB

The Java application connects to MongoDB and retrieves the value of `Col-1` for the current index.

The database value is converted to a `double` before being passed to R. This is useful because MongoDB can store numeric values using different numeric types.

The application keeps the retrieved values in a Java list:

```text
values
```

This list is then converted to a `double[]` before being passed to R.

## Java and R

GraalVM is used to run R code directly from Java.

The R code is stored in:

```text
src/main/resources/Plot.R
```

At application startup, Spring Boot creates a GraalVM `Context` for R and loads the R plotting function.

Java can then call the R function and pass the collected values to it.

The important idea is that **Java owns the data flow**, while **R is responsible for creating the graph**.

## R Plot

The project uses the `lattice` package and `xyplot()` to create the graph.

The R function receives the values from Java and creates the x-axis automatically:

```text
0, 1, 2, ..., n-1
```

The y-axis contains the values received from Java.

The graph uses a line plot and includes a grid to make the values easier to follow.

The plot is written to a PNG file, which is then served by the Spring Boot application.

## Web Page

Spring Boot exposes the generated image through:

```text
/plot
```

The endpoint reads the PNG file and returns it as an `image/png` response.

The web page contains an `<img>` element pointing to this endpoint.

JavaScript refreshes the image every second. A timestamp is added to the request so that the browser does not keep showing a cached version of the image.

## Problems Solved During Development

### Reading the CSV data into MongoDB

The original CSV structure was not directly convenient for identifying rows, so an `index` field was added.

This allowed the Java application to retrieve the data in a predictable order.

### Running R from Java

### Lattice plot error with one value

The graph initially failed when only one value had been collected.

With only one point, the x-axis range became:

```text
0 to 0
```

which caused an invalid `xscale` error in `lattice`.

The application was therefore changed to generate the graph only after at least two values had been collected.

## Project Structure

A simplified view of the important files is:

```text
DataVisualizationUsingR/
├── src/
│   └── main/
│       ├── java/
│       │   └── org/example/datavisualizationusingr/
│       │       ├── DataVisualizationUsingRApplication.java
│       │       ├── MongoService.java
│       │       └── PlotController.java
│       │
│       └── resources/
│           ├── Plot.R
│           └── static/
│               └── index.html
│
└── pom.xml
```

## Running the Project

Before running the application, MongoDB should be running and the required database and collection should already contain the project data.

The Spring Boot application can then be started with Maven:

```bash
./mvnw spring-boot:run
```

After the application starts, open:

```text
http://localhost:8080
```

The graph should be displayed and updated as the application reads new values from MongoDB.

The image itself is available at:

```text
http://localhost:8080/plot
```

## What I Learned

This project was mainly useful for understanding how different technologies can work together rather than using each technology separately.

The main concepts I worked with were:

- Storing and retrieving data with MongoDB
- Accessing MongoDB from Java
- Using Spring Boot to create a web application
- Running R from Java through GraalVM
- Passing data between Java and R
- Creating plots with R and `lattice`
- Automatically updating content in a browser with JavaScript
- Debugging problems caused by dependency versions, plotting devices, and data size
