## Solution of the problem of "retarding requests"

---

### Description
Related project: SQL, JDBC and Hibernate as part of the JavaRush course.

There is a relational MySQL database with a scheme (country-city, language by country). And there is a frequent request 
from the city that is slowing down.

Solution: Extract all data that is requested frequently to Redis (in memory storage type key-value).

We do not need all the data stored in MySQL, only the selected set of fields.

---

### Database Deployment Specifics
Databases were deployed at **Docker**.

**MySql** setup:
`docker run --name mysql -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root --restart unless-stopped -v mysql:/var/lib/mysql 
mysql:8`

**Redis** setup:
`docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest`

The database dump for MySql: `dump-hibernate-final.sql` is located in the project folder `resources`.

---

### Class description
The package `connectordb` contains the classes:
- `MySqlConnector` - configuration class for connecting to the MySql database;
- `RedisConnector` - configuration class for connecting to the Redis database.

The package `domain` contains the classes:
- `City`, `Country`, `Continent`, `CountryLanguage` - Entity database classes.

The package `dao` contains the classes:
- `CityDao`, `CountryDao` - Data Access Object classes.

The package `redis` contains the classes that contain frequently requested (by task) data in a "retarding request":
- `CityCountry` - class contains data on the city and the country in which this city is located;
- `Language` - class contains language data.

The package `service` contains the classes:
- `Controller` - class interacts with databases;
- `DataHandler` - class interacts with data from databases;
- `TesterDB` - class query database testing class. 

___

### Resource description
The directory `resources` contains:
- `dump-hibernate-final.sql` - database dump for MySql;
- `spy.properties` - configuration P6Spy framework to view queries with parameters that Hibernate performs.

### Testing
**Test for Redis:**


