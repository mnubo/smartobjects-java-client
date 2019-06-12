[![Build status](https://travis-ci.org/mnubo/smartobjects-java-client.svg?branch=master)](https://travis-ci.org/mnubo/smartobjects-java-client.svg?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.mnubo/java-sdk-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.mnubo/java-sdk-client)

## Quickstart

[comment]: # (Important: leave the HTML in this section)
[comment]: # (quickstart-setup)

<h3>Getting the client library</h3>
<p>The client library is available on <a target="_blank" href="http://search.maven.org/#artifactdetails%7Ccom.mnubo%7Cjava-sdk-client%7C1.12.0%7Cjar">maven</a>.</p>

<p>You can get started quickly by adding the following to your pom file:</p>
<pre>
    <code>
&lt;dependency&gt;
    &lt;groupId&gt;com.mnubo&lt;/groupId&gt;
    &lt;artifactId&gt;java-sdk-client&lt;/artifactId&gt;
    &lt;version&gt;1.12.0&lt;/version&gt;
&lt;/dependency&gt;
    </code>
</pre>

<p>For more information, visit <a target="_blank" href="https://github.com/mnubo/smartobjects-java-client">GitHub</a>.</p>

<h3>Create a client instance</h3>

<p>The following Java code can be used to create an instance:</p>

<pre>
    <code>
import com.mnubo.java.sdk.client.services.MnuboSDKFactory;
import com.mnubo.java.sdk.client.spi.MnuboSDKClient;

final String host = "<%= hostname %>;
final String clientKey = "<%= clientKey %>";
final String clientSecret = "<%= clientSecret %>";

final MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient(host , clientKey, clientSecret);
    </code>
</pre>

[comment]: # (quickstart-setup)


<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [mnubo Java SDK](#mnubo-java-sdk)
- [Introduction](#introduction)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation & Configuration](#installation-&-configuration)
  - [Maven](#maven)
  - [Download source code](#download-source-code)
    - [Configuration](#configuration)
- [Usage](#usage)
  - [Getting a "MnuboSDKClient" (client) instance](#getting-a-mnubosdkclient-client-instance)
    - [Configuration with client/secret](#configuration-with-clientsecret)
    - [Configuration with a static token](#configuration-with-a-static-token)
  - [Creating owners](#creating-owners)
  - [Updating owners](#updating-owners)
  - [Create or update a batch of Owners](#create-or-update-a-batch-of-owners)
  - [Deleting owners](#deleting-owners)
  - [Check if an owner exists](#check-if-an-owner-exists)
  - [Check if a batch of owners exist](#check-if-a-batch-of-owners-exist)
  - [Claiming an object](#claiming-an-object)
  - [Batch claiming](#batch-claiming)
  - [Unclaiming an object](#unclaiming-an-object)
  - [Batch unclaiming](#batch-unclaiming)
  - [Creating objects](#creating-objects)
  - [Updating objects](#updating-objects)
  - [Create or update a batch of Objects](#create-or-update-a-batch-of-objects)
  - [Deleting objects](#deleting-objects)
  - [Check if an object exists](#check-if-an-object-exists)
  - [Check if a batch of objects exist](#check-if-a-batch-of-objects-exist)
  - [Sending Events](#sending-events)
    - [To send multiple events to a single object:](#to-send-multiple-events-to-a-single-object)
    - [To send multiple events to multiple objects:](#to-send-multiple-events-to-multiple-objects)
  - [Check if an event exists](#check-if-an-event-exists)
  - [Check if a batch of events exist](#check-if-a-batch-of-events-exist)
  - [Model](#model)
  - [Searching](#searching)
    - [Sending search query](#sending-search-query)
  - [Retrieving datasets](#retrieving-datasets)
- [References](#references)
- [Configuring the example](#configuring-the-example)
  - [Running the example](#running-the-example)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# mnubo Java SDK

Introduction
============
This is a Java implementation of the [API documentation](https://smartobjects.mnubo.com/documentation/).

Architecture
============
Use class `MnuboSDKFactory` to get a client instance for ObjectsSDK, OwnersSDK, EventsSDK or SearchSDK.  Then the appropriate client API.

The current SDK only supports synchronous calls.


Prerequisites
=============

- Using this java SDK requires JAVA 1.7 or higher.
- The SDK has been built with maven 3.2.3.


Installation & Configuration
============================

Include the mnubo client in your Java application using:

Maven
-----
You can find information about dependencies to add into your Pom.xml here.

https://maven-badges.herokuapp.com/maven-central/com.mnubo/java-sdk-client

Download source code
---------------------
Download the source code and include it in your Java Application project.

### Configuration
The following parameters must be configured before using the mnubo client:

    - **hostname**.- mnubo's server name, for example: ```rest.sandbox.mnubo.com```.
    - **consumer-key**.- Your unique client identity which is provided by mnubo.
    - **consumer-secret**.- Your secret key which is used in conjunction with the consumer key to access the mnubo server. This key is provided by mnubo.


Usage
=====

Getting a "MnuboSDKClient" (client) instance
--------------------------------------------
To get a client instance use the **"MnuboSDKFactory" Class**. Note that you only need one client instance. We provide multithread support and a pool of connections.

There are different ways to obtain a client instance:

### Configuration with client/secret
Using this method, three parameters are required:

* host
* consumer key
* consumer secret

```
//Example:
//Configure constants
private final String HOST = "rest.sandbox.mnubo.com";
private final String CONSUMER_KEY = "your consumer key";
private final String CONSUMER_SECRET = "your consumer secret";

//Obtain a client instance using default values. (w/o exponentional backoff strategy)
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );
MnuboSDKClient mnuboClientWithBackoff = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET, new ExponentialBackoffConfig());
```

### Configuration with a static token
It is not recommended to use this method in production because your token has an expiry date and this method will not refresh it automatically. Two parameters are required:

* host
* token

```
//Example:
//Configure constants
private final String HOST = "rest.sandbox.mnubo.com";
private final String TOKEN = "your token";

//Obtain a client instance using default values. (w/o exponentional backoff strategy)
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClientWithToken( HOST , TOKEN);
MnuboSDKClient mnuboClientWithBackoff = MnuboSDKFactory.getClientWithToken( HOST , TOKEN, new ExponentialBackoffConfig());
```

Creating owners
---------------
To create an owner you need to:
1. Request an OwnersSDK interface using the mnubo client instance.
2. Build an owner.

This example describes how to create an Owner:
```
//get an Owners client interface
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

//build the owner
Owner Owner2Create = Owner.builder()
                          .withUsername("john.smith@mycompany.com")
                          .withPassword("dud7#%^ddd_J")
                          .withRegistrationDate(DateTime.parse("2015-01-01T00:00:00+04:00"))
                          .withAddedAttribute("age", 35)
                          .withAddedAttribute("gender", "male")
                          .withAddedAttribute("height", 1.80)
                          .build();

//create the owner
mnuboOwnersClient.create( Owner2Create );
```

You can also create an owner using a JSON file:

```
//get an Owners client interface
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

//read the Owner from the JSON file
String owner2BePosted = readingFile( "myOwnerFile.json" );

//deserialise the JSON file
Owner owner2Create = SDKMapperUtils.readValue( owner2BePosted , Owner.class );

//create the owner
mnuboOwnersClient.create( owner2Create );
```

The JSON file "myOwnerFile.json" is as follows:

```
{
  "username":"john.smith@mycompany.com",
  "x_password":"dud7#%^ddd_J",
  "x_registration_date":"2015-01-01T00:00:00+04:00"
}
```

Updating owners
---------------
To update an owner you need to:
1. Request an OwnersSDK interface using the mnubo client instance.
2. Build an owner with the parameters to change.

This example describes how to update an Owner:
```
//get an Owners client interface
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

//build the owner, including only the parameters to update. Remember "username" & "password" cannot change
Owner Owner2Update = Owner.builder()
                          .withRegistrationDate(DateTime.parse("2015-01-02T00:00:00+04:00"))
                          .withAddedAttribute("age", 38)
                          .build();

//update the owner
mnuboOwnersClient.update( Owner2Update, "john.smith@mycompany.com" );
```

You can also update an owner using a JSON file:

```
//get an Owners client interface
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

//read the Owner from the JSON file
String owner2BePosted = readingFile( "myOwnerFile.json" );

//deserialise the JSON file
Owner owner2Update = SDKMapperUtils.readValue( owner2BePosted , Owner.class );

//update the owner
mnuboOwnersClient.update( owner2Update, "john.smith@mycompany.com" );
```

The JSON file "myOwnerFile.json" is as follows. remember only parameters to change:

```
{
  "x_registration_date":"2015-01-02T00:00:00+04:00",
  "age":38,
}
```

Create or update a batch of Owners
---------------
To create a batch of owners you need to:
1. Request an OwnersSDK interface using the mnubo client instance.
2. Build a list of Owners.

This example describes how to create a batch of Owners:
```
//get an Owners client interface
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

//build the list of owner to create / update
List<Owner> owners = new ArrayList<>();

owners.add(Owner.builder()
                .withUsername("user1@mycompany.com")
                .withPassword("dud7#%^ddd_J")
                .withRegistrationDate(DateTime.parse("2015-01-01T00:00:00+04:00"))
                .withAddedAttribute("age", 35)
                .withAddedAttribute("gender", "male")
                .withAddedAttribute("height", 1.80)
                .build());

owners.add(Owner.builder()
                .withUsername("user2@mycompany.com")
                .withPassword("ewor$@$!@fqrq")
                .withRegistrationDate(DateTime.parse("2015-02-01T00:00:00+04:00"))
                .withAddedAttribute("age", 18)
                .withAddedAttribute("gender", "male")
                .withAddedAttribute("height", 1.75)
                .build());

// A single batch can contain up to 1000 Owners

//create the list of owners
List<Result> results = mnuboOwnersClient.createUpdate( owners );
```

You can verify if there is any error(s) for the list of owners created / updated.

The API result will contain a list of the username's (id), their ResultStates
and a message if an error occurred.

Note: ResultStates is an ENUM with four values:
* success
* error
* notfound
* conflict

Here is an example to get the data of the list of Result:
```
for(Result r: results) {
    String id = r.getId();
    ResultStates resultState = r.getResult();
    String message = r.getMessage();
}
```

Deleting owners
---------------
This example describes how to delete an Owner:
```
//get an Owners client interface
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

//delete the owner
mnuboOwnersClient.delete( "john.smith@mycompany.com" );
```

Check if an owner exists
------------------------
This example describes how to validate if an Owner exists:
```
//get an Owners client interface
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

//true if the owner exists, false if not.
boolean doesExists = mnuboOwnersClient.ownerExists( "john.smith@mycompany.com" );
```

Check if a batch of owners exist
--------------------------------
This example describes how to validate if a list of Owners exists:
```
//get an Owners client interface
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

Map<String, Boolean> ownersExist = mnuboOwnersClient.ownersExist( Arrays.asList("john.smith@mycompany.com", "my.test@mycompany.com") );
//true if the owner `john.smith@mycompany.com` exists, false if not.
boolean johnSmithExists = ownersExist.get("john.smith@mycompany.com")
//true if the owner `my.test@mycompany.com` exists, false if not.
boolean myTestExists = ownersExist.get("my.test@mycompany.com")
```

Claiming an object
------------------
This example describes how to link an Object with an Owner:
```
//get an Owners client interface
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

//claim the object
mnuboOwnersClient.claim( "john.smith@mycompany.com", "my_device_Id" );
```

Batch claiming
------------------
This example describes how to link Objects to Owners:
```
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

mnuboOwnersClient.batchClaim(Arrays.asList(
    new ClaimOrUnclaim("john.smith@mycompany.com", "my_device_Id"),
    new ClaimOrUnclaim("freddy@mycompany.com", "my_device_Id2", Collections.singletonMap("x_timestamp", "2017-04-26T07:38:36+00:00"))
));
```

Unclaiming an object
------------------
This example describes how to unlink an Object from an Owner:
```
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

mnuboOwnersClient.unclaim( "john.smith@mycompany.com", "my_device_Id" );
```

Batch unclaiming
------------------
This example describes how to unlink Objects of Owners:
```
OwnersSDK mnuboOwnersClient = mnuboClient.getOwnerClient();

mnuboOwnersClient.batchUnclaim(Arrays.asList(
    new ClaimOrUnclaim("john.smith@mycompany.com", "my_device_Id"),
    new ClaimOrUnclaim("freddy@mycompany.com", "my_device_Id2", Collections.singletonMap("x_timestamp", "2017-04-26T07:38:36+00:00"))
));
```

Creating objects
----------------------
To create an object:
1. Request an ObjectSDK interface from the mnubo client instance.
2. Build an object.

```
//get an Object client interface
ObjectsSDK mnuboObjectClient = mnuboClient.getObjectClient();

//build the object
SmartObject object2Create = SmartObject.builder()
                                       .withDeviceId("connect_alpha.6hv135nw00393.1234567")
                                       .withObjectType("gateway")
                                       .withOwner("john.smith@mycompany.com")
                                       .withRegistrationDate(DateTime.now())
                                       .withAddedAttribute("partnerid", "connect_alpha")
                                       .withAddedAttribute("business_line", "connect")
                                       .withAddedAttribute("siteid", "6hv135nw00393")
                                       .withAddedAttribute("site_description", "My connected House")
                                       .build();

//create the object
mnuboObjectClient.create( object2Create );
```

You can also create an object using a JSON file

```
//get an Object client interface
ObjectsSDK mnuboObjectClient = mnuboClient.getObjectClient();

//read the Object from the JSON file
String object2BePosted = readingFile( "myObjectFile.json" );

//deserialise the JSON file
SmartObject object2Create = SDKMapperUtils.readValue( object2BePosted , SmartObject.class );

//create the object
mnuboObjectClient.create( object2Create );
```

The JSON file "myObjectFile.json" is as follows:

```
{
    "x_device_id" : "connect_alpha.6hv135nw00393.1234567",
    "x_object_type" : "gateway",
    "x_registration_date":"2015-01-27T08:01:01.000Z",
    "partnerid" : "connect_alpha",
    "business_line" : "connect",
    "siteid" : "6hv135nw00393",
    "site_description" : "My connected House",
    "x_owner":
    {
           "username" : "john.smith@mycompany.com"
    }
}
```

Updating objects
----------------
To update an object:
1. Request an ObjectSDK interface from the mnubo client instance.
2. Build an object with parameters to change.

Note: that an owner cannot be added when updating an object.

```
//get a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Object client interface
ObjectsSDK mnuboObjectClient = mnuboClient.getObjectClient();

//build the object, only with parameters to change.
SmartObject object2Update = SmartObject.builder()
                                       .withDeviceId("connect_beta.6hv135nw00393.1234567")
                                       .withObjectType("gateway1")
                                       .withAddedAttribute("site_description", "My connected building")
                                       .build();

//update the object
mnuboObjectClient.update( object2Update, "connect_alpha.6hv135nw00393.1234567" );
```

You can also update an object using a JSON file

```
//get a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Object client interface
ObjectsSDK mnuboObjectClient = mnuboClient.getObjectClient();

//read the Object from the JSON file
String object2BePosted = readingFile( "myObjectFile.json" );

//deserialise the JSON file
SmartObject object2Update = SDKMapperUtils.readValue( object2BePosted , SmartObject.class );

//update the object
mnuboObjectClient.update( object2Update, "connect_alpha.6hv135nw00393.1234567" );
```

The JSON file "myObjectFile.json" is as follows:

```
{
    "x_device_id" : "connect_beta.6hv135nw00393.1234567",
    "x_object_type" : "gateway1",
    "site_description" : "My connected building",
}
```

Create or update a batch of Objects
-----------------------------------
To create a batch of objects you need to:
1. Request an ObjectSDK interface from the mnubo client instance.
2. Build a list of objects.

```
//get a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Object client interface
ObjectsSDK mnuboObjectClient = mnuboClient.getObjectClient();

//build the list of objects to create / update

List<SmartObject> objects = new ArrayList<>();

objects.add(SmartObject.builder()
                       .withDeviceId("connect_alpha.6hv135nw00393.1234567")
                       .withObjectType("gateway")
                       .withOwner("user1@mycompany.com")
                       .withRegistrationDate(DateTime.now())
                       .withAddedAttribute("partnerid", "connect_alpha")
                       .withAddedAttribute("business_line", "connect")
                       .withAddedAttribute("siteid", "6hv135nw00393")
                       .withAddedAttribute("site_description", "My connected House 1")
                       .build());

objects.add(SmartObject.builder()
                       .withDeviceId("connect_beta.7hv234nw60696.7654321")
                       .withObjectType("gateway")
                       .withOwner("user2@mycompany.com")
                       .withRegistrationDate(DateTime.now())
                       .withAddedAttribute("partnerid", "connect_beta")
                       .withAddedAttribute("business_line", "connect")
                       .withAddedAttribute("siteid", "7hv234nw60696")
                       .withAddedAttribute("site_description", "My connected House 2")
                       .build());

// A single batch can contain up to 1000 Objects

// Create the list of objects
List<Result> results = mnuboObjectClient.createUpdate( objects );

```
You can verify if there is any error(s) for the list of owners created / updated.

The API result will contain a list of the x_device_id's (id), their ResultStates
and a message if an error occurreds.

Here is an example to get the data of the list of Result:
```
for(Result r: results) {
    String id = r.getId();
    ResultStates resultState = r.getResult();
    String message = r.getMessage();
}
```

Deleting objects
----------------
```
//get a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Object client interface
ObjectsSDK mnuboObjectClient = mnuboClient.getObjectClient();

//delete the object
mnuboObjectClient.delete( "connect_alpha.6hv135nw00393.1234567" );
```

Check if an object exists
-------------------------
This example describes how to validate if an Object exists:
```
//Request a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Object client interface
ObjectsSDK mnuboObjectClient = mnuboClient.getObjectClient();

//true if the object exists, false if not.
boolean doesExists = mnuboObjectClient.objectExists( "connect_alpha.6hv135nw00393.1234567" );
```

Check if a batch of objects exist
---------------
This example describes how to validate if a list of Objects exists:
```
//Request a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Object client interface
ObjectsSDK mnuboObjectClient = mnuboClient.getObjectClient();

Map<String, Boolean> objectsExist = mnuboObjectClient.objectsExist( Arrays.asList("connect_alpha.6hv135nw00393.1234567", "connect_beta.1234567") );
//true if the device `connect_alpha.6hv135nw00393.1234567` exists, false if not.
boolean deviceAExists = objectsExist.get("connect_alpha.6hv135nw00393.1234567")
//true if the device `connect_beta.1234567` exists, false if not.
Boolean deviceBExists = objectsExist.get("connect_beta.1234567")
```

Sending Events
--------------
To send events:
1. Request an OwnersSDK interface from the mnubo client instance.
2. Build an event.

### To send multiple events to a single object:

```
//private String objectID = "mythermostat0301424";

//get a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Event client interface
EventsSDK mnuboEventClient = mnuboClient.getEventClient();

//build the events
List<Event> event2Send = new ArrayList<Event>();
Event event1 = Event.builder()
                    .withEventType("thermostat_temperature")
                    .withEventID(UUID.fromString("aac652a3-955e-4ac4-b185-b7c2f44111cc"))
                    .withTimestamp(DateTime.now())
                    .withAddedTimeseries("temperature", 20)
                    .withAddedTimeseries("errorcode", "")
                    .withAddedTimeseries("varname", "temperature")
                    .build();
event2Send.add(event1);
Event event2 = Event.builder()
                    .withEventType("thermostat_temperature")
                    .withEventID(UUID.fromString("583acbba-8a4f-4503-ac49-bc5a8e4f7577"))
                    .withTimestamp(DateTime.now())
                    .withAddedTimeseries("temperature", 22)
                    .withAddedTimeseries("cause_type", "normal")
                    .withAddedTimeseries("varname", "temperature")
                    .build();
event2Send.add(event2);

//send the event
List<Result> results = mnuboEventClient.send( objectID, event2Send );
```

You can also send multiple events to a single object using a JSON file:

```
//private String objectID = "mythermostat0301424";

//get a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Event client interface
EventsSDK mnuboEventClient = mnuboClient.getEventClient();

//read the event from the JSON file
String event2BeSent = readingFile( "myEvents.json" );

//deserialise the JSON file
EventValues event2Send = SDKMapperUtils.readValue( event2BeSent , Events.class );

//send the event
List<Result> results = mnuboEventClient.send( objectID, event2Send );
```

The JSON file " myEventsByObjectFile.json " is as follows:

```
[
    {
        "x_event_type" : "thermostat_temperature",
        "event_id": "11111111-2222-3333-4444-555555555555",
        "thermostat_temperature": 20,
        "errorcode": "",
        "varname": "temperature",
    },
    {
        "x_event_type" : "thermostat_temperature",
        "event_id": "11111111-2222-3333-6666-555555555555",
        "thermostat_temperature": 22,
        "cause_type": "normal",
        "varname": "temperature",
    }
]
```

You can verify if there is any error(s) for the list of owners created / updated.

The API result will contain a list of the “x_device_id”s (id), their ResultStates
and the message if there is an error.

Here is an example to get the data of the list of Result:
```
for(Result r: results) {
    String id = r.getId();
    ResultStates resultState = r.getResult();
    String message = r.getMessage();
}
```

### To send multiple events to multiple objects:

```
//private String objectID = "mythermostat0301424";

//get a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Event client interface
EventsSDK mnuboEventClient = mnuboClient.getEventClient();

//build the events
List<Event> event2Send = new ArrayList<Event>();
Event event1 = Event.builder()
                    .withEventType("thermostat_temperature")
                    .withSmartObject("connect_alpha.6hv135nw00393.81")
                    .withTimestamp(DateTime.now())
                    .withAddedTimeseries("temperature", 20)
                    .withAddedTimeseries("errorcode", "")
                    .withAddedTimeseries("varname", "temperature")
                    .build();
event2Send.add(event1);
Event event2 = Event.builder()
                    .withEventType("light_dimmer.internal")
                    .withSmartObject("connect_alpha.6hv135nw00393.82")
                    .withEventID(UUID.fromString("d7284e6b-cf62-4d70-bf79-6ff81af97d7f"))
                    .withTimestamp(DateTime.now())
                    .withAddedTimeseries("light_dimmer", 0)
                    .withAddedTimeseries("cause_type", "internal")
                    .withAddedTimeseries("varname", "level")
                    .withAddedTimeseries("varfunction", "light-dimmer")
                    .build();
event2Send.add(event2);
Event event3 = Event.builder()
                    .withEventType("mask_masked.internal")
                    .withSmartObject("connect_alpha.6hv135nw00393.83")
                    .withTimestamp(DateTime.now())
                    .withAddedTimeseries("mask_masked", "Not Masked")
                    .withAddedTimeseries("cause_type", "internal")
                    .withAddedTimeseries("varname", "mask-state")
                    .build();
event2Send.add(event3);

//send the event
List<Result> results = mnuboEventClient.send( event2Send );
```

You can also send multiple events to multiple objects using a JSON file:

```
//get a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Event client interface
EventsSDK mnuboEventClient = mnuboClient.getEventClient();

//read event from the JSON file
String event2Besent = readingFile( "myEvents.json" );

//deserialise the JSON file
EventValues event2Send = SDKMapperUtils.readValue( event2Besent , EventValues.class );

//send the event
List<Result> results = mnuboEventClient.send( event2Send );
```

The JSON file "myEvents.json" is as follows:

```
[
    {
        "x_object":
        {
            "x_device_id" :"connect_alpha.6hv135nw00393.81"
        },
        "x_event_type": "thermostat_temperature",
        "event_id": "11111111-2222-3333-4444-555555555555",
        "thermostat_temperature": 20,
        "cause_type": null,
        "errorcode": "",
        "varname": "temperature",
    },
    {
        "x_object":
        {
            "x_device_id" :"connect_alpha.6hv135nw00393.82"
        },
        "x_event_type": "light_dimmer.internal",
        "event_id": "11111111-2222-3333-6666-555555555555",
        "light_dimmer": 0,
        "cause_type": "internal",
        "errorcode": null,
        "varname": "level",
        "varfunction": "light-dimmer",
    },
    {
        "x_object":
        {
            "x_device_id" :"connect_alpha.6hv135nw00393.83"
        },
        "x_event_type": "mask_masked.internal",
        "x_timestamp": "2014-10-30T21:46:10+00:00",
        "event_id": "11111111-2222-1111-6666-555555555555",
        "mask_masked": "Not Masked",
        "cause_type": "internal",
        "errorcode": null,
        "varname": "mask-state",
    }
]
```

You can verify if there is any error(s) for the list of events sent.

The API result will contain a list of the “event_id”s (id), their ResultStates
and the message if there is an error.

Here is an example to get the data of the list of Result:
```
for(Result r: results) {
    String id = r.getId();
    ResultStates resultState = r.getResult();
    String message = r.getMessage();
}
```

Check if an event exists
------------------------
This example describes how to validate if an event exists:
```
//Request a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Event client interface
EventsSDK mnuboEventClient = mnuboClient.getEventClient();

//true if the event exists, false if not.
boolean doesExists = mnuboEventClient.eventExists( UUID.fromString("11111111-2222-1111-6666-555555555555") );
```

Check if a batch of events exist
--------------------------------
This example describes how to validate if a list of events exists:
```
//Request a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//get an Event client interface
EventsSDK mnuboEventClient = mnuboClient.getEventClient();

Map<String, Boolean> exists = mnuboEventClient.eventsExist( Arrays.asList(UUID.fromString("22222222-2222-2222-2222-222222222222"), UUID.fromString("11111111-1111-1111-1111-111111111111")) );
//true if the event `22222222-2222-2222-2222-222222222222` exists, false if not.
boolean eventAExists = exists.get(UUID.fromString("22222222-2222-2222-2222-222222222222"))
//true if the event `11111111-1111-1111-1111-111111111111` exists, false if not.
boolean eventBExists = exists.get(UUID.fromString("11111111-1111-1111-1111-111111111111"))
```

Model
---------

The SDK allows you to retrieve the model in the current zone (production or sandbox).

This example shows different methods to view the current data model:

```java
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );
Model model = mnuboClient.getModelClient().export();
Set<Timeseries> tss = mnuboClient.getModelClient().getTimeseries();
Set<ObjectAttribute> objs = mnuboClient.getModelClient().getObjectAttributes();
Set<OwnerAttribute> owners = mnuboClient.getModelClient().getOwnerAttributes();
Set<ObjectType> objectTypes = mnuboClient.getModelClient().getObjectTypes();
Set<EventType> eventTypes = mnuboClient.getModelClient().getEventTypes();
```

You can use the SDK to update the data model as well. Note that these actions are only
available from a SDK configured in sandbox. This example shows how to create and deploy
a timeseries (with an event type), an owner attribute and an object attribute(with
an object type):

```java
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );
EventType et = new EventType("genKey", "desc", "scheduled", Collections.<String>emptySet());
mnuboClient.getModelClient().sandboxOps().eventTypesOps().createOne(et);

ObjectType ot = new ObjectType("genKey", "desc", Collections.<String>emptySet());
mnuboClient.getModelClient().sandboxOps().objectTypesOps().createOne(ot);

Timeseries ts = new Timeseries("-ts", "dp", "desc", "TEXT", Collections.singleton(et.getKey()));
mnuboClient.getModelClient().sandboxOps().timeseriesOps().createOne(ts);
mnuboClient.getModelClient().sandboxOps().timeseriesOps().deploy(ts.getKey());
mnuboClient.getModelClient().sandboxOps().eventTypesOps().addRelation(et.getKey(), ts.getKey());
mnuboClient.getModelClient().sandboxOps().eventTypesOps().removeRelation(et.getKey(), ts.getKey());

ObjectAttribute obj = new ObjectAttribute("-object", "dp", "desc", "DOUBLE", "none", Collections.singleton(ot.getKey()));
mnuboClient.getModelClient().sandboxOps().objectAttributesOps().createOne(obj);
mnuboClient.getModelClient().sandboxOps().objectAttributesOps().deploy(obj.getKey());
mnuboClient.getModelClient().sandboxOps().objectTypesOps().addRelation(ot.getKey(), obj.getKey());
mnuboClient.getModelClient().sandboxOps().objectTypesOps().removeRelation(ot.getKey(), obj.getKey());

OwnerAttribute owner = new OwnerAttribute("-owner", "dp", "desc", "FLOAT", "none");
mnuboClient.getModelClient().sandboxOps().ownerAttributesOps().createOne(owner);
mnuboClient.getModelClient().sandboxOps().ownerAttributesOps().deploy(owner.getKey());

```


You can also see [mnubo documentation](https://smartobjects.mnubo.com/documentation/api_modeler.html) for more information.

Searching
---------

The SDK support sending search query to namespace datasets:
1. Request an SearchSDK interface using the mnubo client instance.
2. Provide your MQL query as a string.

See [mnubo documentation](https://smartobjects.mnubo.com/documentation/) for details on MQL format.

### Sending search query

This example describes how to send search query:
```
//Request a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//Get a Search client interface
SearchSDK mnuboSearchClient = mnuboClient.getSearchClient();

//build the query
String query = "{ \"from\": \"event\", \"select\": [ {\"value\": \"speed\"} ] }";

//Send search
ResultSet resultSet = mnuboSearchClient.search( query );
```

The `ResultSet` can be used to extract the column definitions read and the row values.
`ResultSet` implement `Iterable<Row>` interface which allow iterating on the result values (`Row`).
Column definitions are packaged in a `ColumnDefinition`.
`ColumnDefinitions` can be extracted from the `ResultSet` or from each `Row`.

```
// Get all the column with label and Type
List<ColumnDefinition> columns = resultSet.getColumnDefinitions();

// Get all the Search Rows (including column definitions)
List<Row> searchRows = resultSet.all();

// Get all the column with label and Type from a Search Rows <line number>
List<ColumnDefinition> columnsFromRow = searchRows.get(0).getColumnDefinitions();

// Iterator can be used to got through rs (rows)

    while (resultSet.iterator().hasNext()) {
        Row searchRow = resultSet.iterator().next();

        // Get Column Definitions for the Search Row
        List<ColumnDefinition> columnDefinitions = searchRow.getColumnDefinitions();

        // You need to find the data type (primitive type) to get the corresponding value
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            String datatype = columnDefinition.getPrimitiveType();
        }

        // Get data of the row for a specific column :
        // The search Row method use the primitive type (not the High Level Type)
        // For example if the primitive type is "STRING" and we want the data of the column "column1":
        String stringData = searchRow.getString("column1");

        // Other Example with when primitive type is "DOUBLE" and we want the data of the column "column2":
        Double doubleData = searchRow.getDouble("column2");
    }
```

Retrieving datasets
-------------------
To Get the datasets:
1. Request a SearchSDK interface using the mnubo client instance.

This example describes how to create an request to get the DataSets:
```
//Request a mnubo client using the basic method.
MnuboSDKClient mnuboClient = MnuboSDKFactory.getClient( HOST , CONSUMER_KEY , CONSUMER_SECRET );

//Get a Search client interface
SearchSDK mnuboSearchClient = mnuboClient.getSearchClient();

//Get the list of DataSet
List<DataSet> datasets = mnuboSearchClient.getDatasets();
```

You can use the data return by the Basic Search Query.

```
    for(DataSet dataset: datasets) {
        String datasetDescription = dataset.getDescription();
        String datasetDisplayName = dataset.getDisplayName();
        String datasetKey = dataset.getKey();

        for(Field field: dataset.getFields()) {
            String containerType = field.getContainerType();
            String fieldDescription = field.getDescription();
            String fieldDisplayName = field.getDisplayName();
            String fieldHighLevelType = field.getHighLevelType();
            String fieldKey = field.getKey();
        }
    }
```



References
==========

[java](http://www.oracle.com/technetwork/java/index.html)

[maven](https://maven.apache.org/)

[mnubo documentation](https://smartobjects.mnubo.com/documentation/)


Configuring the example
=======================

- Go to the file 'SdkClientIntegrationTest' in '...example/src/test/java/com/mnubo/java/sdk/client/integration/' folder
- Remove or comment out the 'Ignore' annotation.
- Assign your personal consumer key to the field 'CONSUMER_KEY' as: CONSUMER_KEY = "consumerKeyProvidedByMnubo";
- Assign your personal consumer secret to the field 'CONSUMER_SECRET' as: CONSUMER_SECRET = "consumerSecretProvidedByMnubo";
- Add at least one Owner's attribute to the fields 'OWNER_ATTRIBUTES' and 'OWNER_ATTRIBUTES_2_UPDATE' this can be the sames or not.
- Add at least one Object's attribute to the fields 'OBJECT_ATTRIBUTES' and 'OBJECT_ATTRIBUTES_2_UPDATE' this can be the sames or not.
- Add at least one Timeseries to the fields 'TIMESERIES_2_POST' and 'EXTRA_TIMESERIES_2_POST' this can be the sames or not.


Running the example
-------------------

- Go to '...example/' folder
- Invoke 'mvn package'.
