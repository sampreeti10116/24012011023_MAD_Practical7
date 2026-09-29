# Practical-7: JSON API, RecyclerView & SQLite Database

## Aim
Develop an Android application that retrieves person data in JSON format from an Internet API and stores the retrieved data in an SQLite database.

## Objectives
1. Create MainActivity according to the given UI design.
2. Generate and use a JSON API URL for person data.
3. Create a `Person` class containing person details.
4. Retrieve JSON data from an Internet URL.
5. Parse JSON data into Person objects.
6. Display records using RecyclerView or ListView.
7. Create an `HttpRequest` class for web communication.
8. Use Kotlin Coroutines for background network operations.
9. Add Internet permission in the Manifest.
10. Store retrieved data in SQLite.
11. Pass Person objects between Activities using Serializable.
12. Use latitude and longitude for map-related functionality.

## Concepts Covered
- JSON and JSON API
- HTTP requests
- `HttpURLConnection`
- Kotlin Coroutines and `CoroutineScope`
- RecyclerView
- ListView
- Adapter
- SQLite and `SQLiteOpenHelper`
- `Serializable`
- Intent and `putExtra()`
- JSON parsing
- Internet permission
- Latitude and Longitude

## 1. JSON Format
JSON (JavaScript Object Notation) is a lightweight format commonly used for exchanging data between applications and web services.

Example:

```json
{
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "phone": "9876543210",
    "email": "john@example.com",
    "address": "Ahmedabad",
    "latitude": 23.0225,
    "longitude": 72.5714
}
```

The practical instructions use a JSON Generator service to create the API data and then use its generated URL in the Android application.

## 2. Person Class
Create a model class containing ID, name, phone, email, address, latitude and longitude.

```kotlin
import java.io.Serializable

data class Person(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
) : Serializable
```

The class implements `Serializable` so that a Person object can be passed between Activities.

## 3. HttpRequest Class
Create an `HttpRequest` class to communicate with the JSON web URL.

Its responsibilities are:
- Open the URL connection.
- Send the HTTP request.
- Read the response.
- Return JSON data.
- Close the connection.

Example:

```kotlin
val url = URL(apiUrl)
val connection = url.openConnection() as HttpURLConnection
connection.requestMethod = "GET"
connection.connectTimeout = 10000
connection.readTimeout = 10000

val response = connection.inputStream
    .bufferedReader()
    .use { it.readText() }

connection.disconnect()
```

## 4. Internet Permission
Add the following permission to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 5. Kotlin Coroutines
Network operations should run away from the main/UI thread.

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    val jsonData = httpRequest.getData()

    withContext(Dispatchers.Main) {
        // Update UI
    }
}
```

`Dispatchers.IO` is suitable for network/database work, while `Dispatchers.Main` is used for UI operations.

## 6. RecyclerView
RecyclerView displays the retrieved Person records efficiently.

A row may contain:
- Name
- Phone
- Email
- Address
- Map button

```text
MainActivity
     |
     v
RecyclerView
     |
     v
PersonAdapter
     |
     +-- Person Item
     +-- Person Item
     +-- Person Item
```

## 7. ListView
ListView can also be used as an alternative to RecyclerView for displaying the person records through an Adapter.

## 8. PersonAdapter
The adapter connects Person data to the RecyclerView and handles item/map-button clicks.

```kotlin
class PersonAdapter(
    private val personList: List<Person>
) : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {
    // ViewHolder and binding implementation
}
```

## 9. JSON Parsing
The API response is converted from JSON into Person objects.

```text
JSON Response
      |
      v
JSON Parser
      |
      v
Person Objects
      |
      v
Person List
      |
      v
RecyclerView
```

## 10. SQLite Database
SQLite is Android's lightweight local relational database. The retrieved Person information can be stored locally.

Possible table:

| Column | Type |
|---|---|
| id | INTEGER |
| firstName | TEXT |
| lastName | TEXT |
| phone | TEXT |
| email | TEXT |
| address | TEXT |
| latitude | REAL |
| longitude | REAL |

## 11. SQLiteOpenHelper
`SQLiteOpenHelper` can be used to create and manage the database.

```kotlin
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "PersonDatabase.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create Person table
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Upgrade database
    }
}
```

The application can perform Insert, Read, Update and Delete operations as required. The main requirement is storing retrieved API data in SQLite.

## 12. Serializable and Activity Communication
The practical uses Serializable to pass a Person object from `PersonAdapter` to `MapActivity`.

```kotlin
val intent = Intent(context, MapActivity::class.java)
intent.putExtra("person", person)
context.startActivity(intent)
```

The receiving Activity can retrieve the object:

```kotlin
val person = intent.getSerializableExtra("person") as? Person
```

For newer Android versions, the typed Serializable API should be preferred where available.

## 13. Latitude and Longitude
The Person class contains:

```kotlin
val latitude: Double
val longitude: Double
```

These coordinates can be used by `MapActivity` to display the person's location on a map.

## 14. Application Flow

```text
                    MainActivity
                         |
                         v
                  Request JSON API
                         |
                         v
                    HttpRequest
                         |
                         v
                  HttpURLConnection
                         |
                         v
                    JSON Data
                         |
                         v
                    JSON Parser
                         |
                         v
                   Person Objects
                    /                            /                             v             v
          SQLite Database   PersonAdapter
                                |
                                v
                           RecyclerView
                                |
                           Map Button
                                |
                                v
                           MapActivity
```

## 15. Suggested Project Structure

```text
app/
└── src/main/
    ├── java/com/example/practical7/
    │   ├── MainActivity.kt
    │   ├── Person.kt
    │   ├── PersonAdapter.kt
    │   ├── HttpRequest.kt
    │   ├── DatabaseHelper.kt
    │   └── MapActivity.kt
    ├── res/layout/
    │   ├── activity_main.xml
    │   ├── item_person.xml
    │   └── activity_map.xml
    └── AndroidManifest.xml
```

## 16. Expected Application Behavior
1. MainActivity starts.
2. The application requests data from the JSON API.
3. HttpRequest uses HttpURLConnection to obtain the response.
4. JSON data is parsed into Person objects.
5. Person records are displayed in RecyclerView/ListView.
6. Records are stored in SQLite.
7. Clicking the map button passes the selected Person to MapActivity.
8. MapActivity can use latitude and longitude to show the person's location.

## 17. Learning Outcomes
After completing this practical, the student will be able to:
- Understand JSON and JSON APIs.
- Retrieve data from an Internet API.
- Use `HttpURLConnection`.
- Use Kotlin Coroutines for background operations.
- Create RecyclerView/ListView adapters.
- Parse JSON into Kotlin objects.
- Create and manage SQLite databases.
- Use `SQLiteOpenHelper`.
- Implement `Serializable`.
- Pass objects between Activities.
- Work with latitude and longitude.
- Configure Internet permission.
- Combine remote API data with local database storage.


## Output Screenshots:
<table>
    <tr>
        <td><img width="377" height="836" alt="image" src="https://github.com/user-attachments/assets/58fbd24b-f5aa-466a-9b29-084bd58f0ecf" />
</td>
        <td><img width="377" height="837" alt="image" src="https://github.com/user-attachments/assets/2e7b9bdc-c225-416b-a212-2796f5e82301" />
</td>
        <td><img width="378" height="835" alt="image" src="https://github.com/user-attachments/assets/bba2d496-34ac-453e-9771-06a288f49708" />
</td>
    </tr>
</table>

## Conclusion
Practical-7 demonstrates how an Android application communicates with an Internet-based JSON API, converts the response into Kotlin objects, displays the records using RecyclerView/ListView, and stores the retrieved information in SQLite. It also introduces HttpURLConnection, Kotlin Coroutines, Serializable, Activity-to-Activity data transfer, and location coordinates, providing a foundation for applications that combine web services with local storage.
