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

## Approaches

Though the problem looked easy, on diving into it deeper I realised I was wrong!  

__Antaeus__  :wrestling: :women_wrestling: __Nam__

I started out with two high level approaches:
* Run a polling job at a constant rate :arrow_right: Job picks the PENDING, FAILED invoices :arrow_right: In case of a __PENDING__ Invoice-> checks if it's the 1st in the country/timezone __FAILED__ Invoice-> Passes for processing regardless :arrow_right: Producer/Consumer model processes the invoices by asynchronously calling the payment provider :arrow_right: Done :moneybag:
* Configure a DB driven Scheduler :arrow_right: Invoices are pushed for processing as per the timezone :arrow_right: Push invoices to channel, open coroutines to read from it and then open async call to payment provider :arrow_right: The success and failure status is noted in a tracking table and invoices table :arrow_right: To run ADHOC/FAILED payments provide REST endpoint :arrow_right: Done :moneybag:

### I swiped Right on :purple_heart: :purple_heart:

:two: approach because:
* Failure retries specifically for payments must be performed post analysis/mentoring of the reason. Auto retries may not be the most ideal route to follow.
* Since it is scheduled  job we can provide a flexibility to reschedule etc.

## Project specifics

### Assumptions made/ A little Personal touch :salt::

1. The billing is always performed as per the customers country timezone. 
2. In addition to the currency, I also added countries, and made a country :left_right_arrow: currency mapping. I am aware the mapping is not direct but considered it for the project.
3. Created a specific implementation for the PaymentProvider, changed it to return a more specific Response structure, instead of a true/false. To keep our compliance/audit teams Happy ROFL!
4. Instead of pushing the [PaymentTrackingResponse] response code
status into the invoice table, decided to create a separate table for tracking these responses. Took this approach because in the future we may want a more detailed third party response tracking!
5. Added a [paymentProcessingDate], to allow pushing future dated payments. This field ensures we don't bill the customer  for March in January! 

### Features Implemented

* Scheduling
    Ensured to implement Transaction management, Chunk based processing and Rescheduling.
    1. [CronJobs] DB driven Jobs using Quartz Scheduler. Table used: 
        Field                   | Value         
        ------------------------|------------- 
        job_class_payment_name  | Name of the kotlin class to run
        job_name                | Name of the job
        job_type                | Type of job. Eg: SCHEDULED, SIMPLE.
        schedule                | The cron trigger schedule
        country_code            | The country for which the job has to be scheduled
        currency_code           | Associated currency
        
        [CronJobService] schedules all the valid jobs.
        Performing background processing in a chunk based model, in the background using __Quartz+ Channel+ coroutines__.
        
    2. REST Endpoint to reschedule already existing job by providing unique trigger.   
        
* Data Validation
    * Performed basic validations on the source data before such as:
        1. validate customer exists
        2. validate amounts(non negative,non zero)
        3. validate countries supported 
      This ensures that only valid data is passed through for processing, thereby increasing efficiency. Smart work over ~~Hardwork~~.
     * REST Request validations. Ensures meaningful responses are passed back in case of invalid data. Also makes sure that only correct and supported requests are passed through.     
      
* Payments Processing 
    * [BillingService] Open out parallel asynchronous connections to the Payment Provider in chunks(10). Thereby Improving performance.
    
        Failure scenarios:
            In case of payment failure REST API to run specific invoices ADHOC. 
            REST API: __rest/v1/invoices/rerun__
      
* Reporting
    * REST endpoints to get payment data based on status, country and currency.
    
        


## :factory: Design Patterns to Lookout for(Making life easy for everyone! :fox_face::fox_face:)
* [Factory Pattern](https://github.com/namratanpillai/antaeus/blob/develop/pleo-antaeus-core/src/main/kotlin/io/pleo/antaeus/core/services/scheduler/jobs/JobFactory.kt) : For Cron Job creation
* [Template Pattern](https://github.com/namratanpillai/antaeus/blob/develop/pleo-antaeus-core/src/main/kotlin/io/pleo/antaeus/core/services/CronJobService.kt) : Run steps for job execution
* [Composite Pattern](https://github.com/namratanpillai/antaeus/blob/develop/pleo-antaeus-core/src/main/kotlin/io/pleo/antaeus/core/services/BillingService.kt) : Source data Validations


## Nam(Wrestler) Stats

Category| Description 
| :--- | ---: 
Time taken  | 8 days * 3 hours(approx per day)
Enjoyed doing  | Learning a new language KOTLIN, Designing the basic structure, Thinking through various failure scenarios, Mothers coffee at nights while trying to figure stuff out :coffee: :coffee:
Struggled with  | Initial setup, Efficient Error Handling



