
# secure-message-stub

## Endpoints

1. */secure-message-stub*      to create a message

2. */secure-message-stub/conversations* to view your inbox

3. */secure-message-stub/conversation-message/:id* to view a specific message

4. */secure-message-stub/messages/:id* to view a specific letter/conversation based on the message type encoded in the id

## Running locally:

Ensure that you have `secure-message` and `secure-message-frontend` running, along side `auth-login-stub`.

These can either be running locally (please see the respective project README file for instructions on running these services locally) or you can simply use the following `service-manager` profile like so, ensuring you have `service-manager`'s python environment setup and loaded:

`source ../servicemanager/bin/activate`

`sm --start DC_TWSM_ALL`

`sm --stop SECURE_MESSAGE_STUB`

To run the stub simply run the following command:

`sbt "run 9202"`

## Run the tests and sbt fmt before raising a PR

Ensure you have service-manager python environment setup:

`source ../servicemanager/bin/activate`

Format:

`sbt fmt`

Then run the tests and coverage report:

`sbt clean coverage test coverageReport`

If your build fails due to poor testing coverage, *DO NOT* lower the test coverage, instead inspect the generated report located here on your local repo: `/target/scala-2.12/scoverage-report/index.html`

Then run the integration tests:

`sbt it:test`

## Create a CDS message/query:

Visit http://localhost:9202/secure-message-stub

## View your message inbox (partial from secure-message-frontend)

First authenticate via `auth-login-stub` (i.e. Auhtority Wizard) with the following:

Redirect URL should be set to http://localhost:9202/secure-message-stub/messages to view your user's inbox or http://localhost:9202/secure-message-stub to create a message

Example of an enrolment:

Enrolment Key: `HMRC-CUS-ORG`
Identifier Name: `EORINumber`
Identifier Value: `GB1234567890`

Examples of filters on the messages inbox:

http://localhost:9202/secure-message-stub/messages?enrolment=HMRC-CUS-ORG~EORINumber~GB1234567890

http://localhost:9202/secure-message-stub/messages?enrolmentKey=HMRC-CUS-ORG&enrolment=HMRC-CUS-ORG~EORINumber~GB1234567890

http://localhost:9202/secure-message-stub/messages?enrolment=HMRC-CUS-ORG~EORINumber~GB1234567890&tag=notificationType~CDS%20Exports&tag=sourceId~ccdm

Click on submit and you should be presented with your inbox. If it's empty, load your local mongo `secure-message` database with some records as described [here](https://github.com/hmrc/secure-message/blob/master/README.md). Alternatively you can use the `Create a CDS message/query` endpoint as previously mentioned.

## Swagger-UI endpoint for secure-message

Ensure that `secure-message` is running locally on port `9051` (as mentioned above, either by using `service-manager` or by running the service locally as described [here](https://github.com/hmrc/secure-message/blob/master/README.md)

You can then find the swagger-UI interface here:

http://localhost:9202/secure-messaging/docs/swagger-ui/index.html?url=/secure-messaging/api/schema.json

## EIS endpoint for conversation reply to pega
To test sending a QueryResponse to EIS:
    PUT /prsup/PRRestService/DMS/Service/QueryResponse
    With example QueryResponse:
```json
{"queryResponse": { "id": "cdc3f605-cb77-4025-a48d-b733cd88c3e6","conversationId":  "D-80542-20201120","message": "QmxhaCBibGFoIGJsYWg=" }}
```
Set the Authentication header to `Bearer AbCdEf123456`
###Responses
- 204 The QueryResponse was sucessfully processed by EIS
- 400 The QueryResponse was invalid
- 401 The Bearer token was invalid
- 500 EIS could not process the QueryResponse to simulate this prepend `err` to the conversationId


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
