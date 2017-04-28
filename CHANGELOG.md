<a name='1.9.0'></a>

# [1.9.0](https://github.com/mnubo/smartobjects-java-client/compare/1.8.1...1.9.0) (2017-04-28)


- Added model service to fetch current model
- Added batch claim/unclaim
- Added body to owner's object claim/unclaim
<a name='1.8.1'></a>

# [1.8.1](https://github.com/mnubo/smartobjects-java-client/compare/1.7.7...1.8.1) (2016-11-30)


### Features

* **POST /api/v3/owners/{username}/objects/{device}/unclaim**: support for unclaim object

### Breaking changes

* Convert.java which parse result from the mnubo platform would throw an `NumberFormatException` if no type was found. An `IllegalArgumentException` is thrown instead.
<a name="1.7.7"></a>

# [1.7.7](https://github.com/mnubo/mnubo-java-sdk/compare/v1.7.6...v1.7.7) (2016-07-25)

### Features

* **POST /api/v3/events:** support for 'conflict' and 'notfound' error codes

<a name="1.7.6"></a>

# [1.7.6](https://github.com/mnubo/mnubo-java-sdk/compare/v1.7.5...v1.7.6) (2016-07-21)

### Features

* **POST /api/v3/events:** this API does not allow anymore to overwrite an existing event
* **POST /api/v3/events/exists:** new endpoint to check the existence of a list of events
* **GET /api/v3/events/exists/{event_id}:** new endpoint to check the existence of a specific event
* **POST /api/v3/objects/exists:** new endpoint to check the existence of a list of objects
* **GET /api/v3/objects/exists/{device_id}:** new endpoint to check the existence of a specific object
* **POST /api/v3/owners/exists:** new endpoint to check the existence of a list of owners
* **GET /api/v3/owners/exists/{username}:** new endpoint to check the existence of a specific owner
