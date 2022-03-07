## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will schedule payment of those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

## Instructions

Fork this repo with your solution. Ideally, we'd like to see your progression through commits, and don't forget to update the README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

## Developing

Requirements:
- \>= Java 11 environment

Open the project using your favorite text editor. If you are using IntelliJ, you can open the `build.gradle.kts` file and it is gonna setup the project in the IDE for you.

### Building

```
./gradlew build
```

### Running

There are 2 options for running Anteus. You either need libsqlite3 or docker. Docker is easier but requires some docker knowledge. We do recommend docker though.

*Running Natively*

Native java with sqlite (requires libsqlite3):

If you use homebrew on MacOS `brew install sqlite`.

```
./gradlew run
```

*Running through docker*

Install docker for your platform

```
docker build -t antaeus
docker run antaeus
```

### App Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── buildSrc
|  | gradle build scripts and project wide dependency declarations
|  └ src/main/kotlin/utils.kt 
|      Dependencies
|
├── pleo-antaeus-app
|       main() & initialization
|
├── pleo-antaeus-core
|       This is probably where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|       Module interfacing with the database. Contains the database 
|       models, mappings and access layer.
|
├── pleo-antaeus-models
|       Definition of the Internal and API models used throughout the
|       application.
|
└── pleo-antaeus-rest
        Entry point for HTTP REST API. This is where the routes are defined.
```

### Main Libraries and dependencies
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library
* [Sqlite3](https://sqlite.org/index.html) - Database storage engine

Happy hacking 😁!





***



__Antaeus__  :wrestling: :women_wrestling: __Nam__

Though the problem looked easy, on diving into it deeper I realised I was wrong!  

## Approaches
I considered two approaches for processing the payments :
1. Running a background task that processes invoices daily at a predetermined time rate. In case of failures the task retries the payment in the next iteration.
2. Scheduling jobs for the 1st of every month for each country according to their timezone. In case of failures allow for manually triggering payments through an API.

### Swiped Right on :purple_heart: :purple_heart:

I chose approach :two: :
* This process will run once a month as per the customers timezone and all the failed payments can be pushed to a table for reviewing, post which can be manually sent for retry. This ensures more control rather than an auto retry, since reasons for failures could be many.
* Avoid an unnecessary process running in the background, thereby saving resources.

## Project specifics

### Assumptions made/ A little Personal touch :salt::

1. The billing is always performed as per the customers country(timezone). 
2. Altered the Response structure for the [PaymentProvider] from true/false to a more specific response code/response message, which can be used to track payments in detail.
3. Added a additional column in the Invoice table called[paymentProcessingDate]. This field ensures that payments for a specific time period can be executed.
4. Added countries to enable timezone specific runs. This is because currency alone cannot be used as an indicator for the apt timezone to consider.
5. Assumed that scenarios of Bank holidays etc will be taken care of by the [PaymentProvider]. In case of failures the invoices can be processed manually via the API. Also assumed that all of the customer bank details etc are hed by the [PaymentProvider].
6. The payment response for the invoice is not only maintained in the status column in the [InvoiceTable] but also in a separate table called [PaymentTrackingTable]. This table is used for reporting, and can be used to store details about the third party process.


### Features Implemented
* Scheduled jobs that runs to process invoices in the customers timezone.
* Flexibility to manually trigger payments by invoice id or within specific time periods and status. This provides the capability to handle error and Adhoc situations.
* Reporting APIs to give detailed cause for Payment errors.
* Data validations:
    * Source data is validated before moving it for further processing, ensuring that we do not perform incorrect/unnecessary processing.

### Technical Features
* Quartz Scheduler
* DB driven Cron Job configuration
* Chunk Based Processing using Channels+ CoRoutines. Divided the tasks in chunks to trigger background tasks.
* Parallel asynchronous calls to External Integrator while performing payments.
* REST API Request Validations with specific Error Codes

## :factory: Design Patterns to Lookout for :fox_face::fox_face:
* [Factory Pattern](https://github.com/namratanpillai/antaeus/blob/develop/pleo-antaeus-core/src/main/kotlin/io/pleo/antaeus/core/services/scheduler/jobs/JobFactory.kt) : For Cron Job creation
* [Template Pattern](https://github.com/namratanpillai/antaeus/blob/develop/pleo-antaeus-core/src/main/kotlin/io/pleo/antaeus/core/services/CronJobService.kt) : Run steps for job execution
* [Composite Pattern](https://github.com/namratanpillai/antaeus/blob/develop/pleo-antaeus-core/src/main/kotlin/io/pleo/antaeus/core/services/BillingService.kt) : Source data Validations


## :post_office: [Click here for Postman](https://github.com/namratanpillai/antaeus/blob/develop/postman/Anateues.postman_collection.json)

### Experience

Category| Description 
| :--- | ---: 
Time taken  | 4 hrs + + 2 hrs + 10 hrs  + 5 hrs + 8 hrs + 2 hrs
Enjoyed doing  | Learning a new language KOTLIN, Thinking through various failure scenarios, Coffee at nights while trying to figure stuff out :coffee: :coffee:
Struggled with  | Initial setup, Efficient Error/Exception Handling
Future | Notification system via mail for failure/reporting, Taking care of Region specific Bank holidays, Ability to only check the status of the invoice without actually performing the payments.



