 
# secure-message-stub

## Endpoints

1. */secure-message-stub*      to create a message

2. */secure-message-stub/conversations* to view your inbox

3. */secure-message-stub/conversation-message/:id* to view a specific message

## Running locally:

Ensure that you have `secure-message` and `secure-message-frontend` running, along side `auth-login-stub`.

These can either be running locally (please see the respective project README file for instructions on running these services locally) or you can simply use the following `service-manager` profile like so, ensuring you have `service-manager`'s python environment setup and loaded:

`source ../servicemanager/bin/activate`

`sm --start DC_ACCEPTANCE`

To run the stub simply run the following command:

`sbt "run 9202"`

## Create a CDS message/query:

Visit http://localhost:9202/secure-message-stub

## View your message inbox (partial from secure-message-frontend)

First authenticate via `auth-login-stub` (i.e. Auhtority Wizard) with the following:

Redirect URL should be set to http://localhost:9202/secure-message-stub/conversations

Enrolments should be set as follow:

Enrolment Key: `HMRC-CUS-ORG`
Identifier Name: `EORINumber`
Identifier Value: `GB1234567890`

Click on submit and you should be presented with your inbox. If it's empty, load your local mongo `secure-message` database with some records as described [here](https://github.com/hmrc/secure-message/blob/master/README.md). Alternatively you can use the `Create a CDS message/query` endpoint as previously mentioned.

## Swagger-UI endpoint for secure-message

Ensure that `secure-message` is running locally on port `9051` (as mentioned above, either by using `service-manager` or by running the service locally as described [here](https://github.com/hmrc/secure-message/blob/master/README.md)

You can then find the swagger-UI interface here:

http://localhost:9202/secure-messaging/docs/swagger-ui/index.html?url=http://localhost:9051/assets/schema.json

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
