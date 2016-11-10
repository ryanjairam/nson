# nson
Nson is a Java plugin that allows you to define required and optional fields in Java objects created by the Gson library.

[Why is this needed?](#why-is-this-needed)

[Dependencies](#dependencies)

[Serializing Java Objects](#serializing-java-objects)

[Deserializing JSON Strings](#deserializing-json-strings)

[An Example](#an-example)

## Why is this needed?
Currently, Gson assumes all object fields are optional and missing fields get initialized as null. Nson allows your applications to detect when a required field is missing at the time the Java object is created.  This help you to make sure your client-side applications are sending the correct data to the server. For example, Nson can detect when your JSON string parameter names don't match your Java object fields.

Imagine an application where a user is prompted to enter their first name, last name, email address and phone number. The user must provide their first name, last name and email address but they can choose to leave out their phone number. While working on the website, a developer accidentally uses the wrong parameter name in the AJAX request that posts the form to the server :open_mouth:. 

Now, the server receives this JSON string whenever a user submits the form:
```javascript
{
  'firstName':'Max',
  'lastname': 'Powers',
  'emailAddress':'max.powers@notarealemailaddress.lol'
}
```
(The developer used __lastname__ instead of __lastName__)

On the server, the User object is defined as:
```java
public class User {
    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("emailAddress")
    private String emailAddress;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress.toLowerCase(Locale.CANADA);
    }
}
```
When the server receives the request, it checks that the user has entered valid information for all fields.

Visitors to the website start to complain that none of their form submissions are working because the server is saying that their last name is required (but they actually are including it).  What's happening?  Gson is initializing lastName as null since the JSON string does not contain a parameter called lastName (lastname is not being deserialized into lastName). When the server-side application goes to check that a last name was provided, object.getLastName() returns null and it gets interpreted as no last name being provided.

Nson allows you to handle this situation without having to manually do null checks for the required fields.


## Dependencies
The Gson library is required for Nson to work.

https://github.com/google/gson

## Adding Nson to your project
For the time being, add nson-1.0.jar (located in the target folder) to your project as a library.

## Serializing Java Objects
__Nson.toJson([Java Object])__
```java
String jsonString = Nson.toJson(object);
```

## Deserializing JSON Strings
__(Object class)Nson.fromJson([json string], [Object class]);__
```java
Playlist playlist;
try
{
    playlist = (Playlist) Nson.fromJson(json, Playlist.class);
}
catch (NsonRequiredPropertyMissing nsonRequiredPropertyMissing)
{
    // handle the situation where a required property is missing
}
catch (NsonException e) {
    // handle the exception case
}
```

## An Example
Define your Java object, keeping in mind that with Nson, all fields are assumed to be required unless they have the @Optional annotation (this is the opposite Gson, which assumes all are optional):
```java
public class User {
    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("emailAddress")
    private String emailAddress;

    @Optional
    @SerializedName("phoneNumber")
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress.toLowerCase(Locale.CANADA);
    }
}
```
Notice the __@Optional__ annotation for __phoneNumber__. 

When you need to convert a JSON string into a Java object (in this case, a User object):
```java
User newUser;
try
{
    newUser = (User) Nson.fromJson(json, User.class);
}
catch (NsonRequiredPropertyMissing nsonRequiredPropertyMissing)
{
    // handle the situation where a required property is missing
}
catch (NsonException e) {
    // handle the exception case
}
```

Now the server will detect when a "bad" JSON string is received and you can handle it as you see fit.


