# SENG JavaFX-to-Web UI Integration Port Demo

Web UI demo of ported JavaFX GUI software engineering projects completed at the University of Canterbury:
* SENG201: Christchurch International Airport Parking
* SENG301: (NZTA WoF) Vehicle Registration
> Note: courses/projects completed before ChatGPT and other Gen-AI models have been publicly available.

There are two existing vehicle owner registrations in the SQLite DB sample:
```console
email: one@test.com
password: one
```
```console
email: two@test.com
password: two
```

## Original JavaFX GUI/CLI App Project Test Results

<details><summary>SENG201 Christchurch International Airport Parking GUI/CLI Test Suite</summary>
      
> Unit test results are generated from a workflow run on a University of Canterbury JavaFX GUI/CLI project.

| Test Status | Count |
|---|---:|
| Passed | 125 |
| Failed | 0 |
| Skipped | 0 |

```console
[PASS] addLot
[PASS] vehicleFor
[PASS] getParkingLot
[PASS] parkOptions
[PASS] setArrival
[PASS] getDeparture
[PASS] identityCrisis
[PASS] regNo
[PASS] getArrival
[PASS] setDeparture
[PASS] getParkingLot
[PASS] setParkingLot
[PASS] setOwner
[PASS] getOwner
[PASS] computeChargeForOneDayOneMin
[PASS] computeChargeForOneMin
[PASS] computeChargeForTwentyThreeHoursOneMin
[PASS] computeChargeForTwoDaysZeroMin
[PASS] computeChargeForTwoDaysSixteenHoursOneMin
[PASS] computeChargeForEightDaysZeroMin
[PASS] computeChargeForTwentyFiveDaysThirteenHoursOneMin
[PASS] computeChargeForThreeDaysZeroMin
[PASS] computeChargeForTwoDaysOneMin
[PASS] computeChargeForZeroTime
[PASS] computeChargeForFiveDaysTwentyThreeHoursOneMin
[PASS] computeChargeForOneDaySevenHoursZeroMin
[PASS] computeChargeForOneDayZeroMin
[PASS] computeChargeForThreeDaysOneMin
[PASS] computeChargeForOneDayOneMin
[PASS] computeChargeForOneMin
[PASS] computeChargeForTwentyThreeHoursOneMin
[PASS] computeChargeForTwoDaysZeroMin
[PASS] computeChargeForTwoDaysSixteenHoursOneMin
[PASS] computeChargeForEightDaysZeroMin
[PASS] computeChargeForTwentyFiveDaysThirteenHoursOneMin
[PASS] computeChargeForThreeDaysZeroMin
[PASS] computeChargeForTwoDaysOneMin
[PASS] computeChargeForZeroTime
[PASS] computeChargeForFiveDaysTwentyThreeHoursOneMin
[PASS] computeChargeForOneDaySevenHoursZeroMin
[PASS] computeChargeForOneDayZeroMin
[PASS] computeChargeForThreeDaysOneMin
[PASS] computeChargeForOneDayOneMin
[PASS] computeChargeForTwentyThreeHoursOneMin
[PASS] computeChargeForThirteenDaysOneHourZeroMin
[PASS] computeChargeForOneDayTwentyThreeHoursOneMin
[PASS] computeChargeForOneDayOneHourZeroMin
[PASS] computeChargeForFourDaysZeroMin
[PASS] computeChargeForOneDayTwoHoursZeroMin
[PASS] computeChargeForOneDayTwoHoursOneMin
[PASS] computeChargeForTwoHoursOneMinute
[PASS] computeChargeForTwentyThreeHoursZeroMin
[PASS] computeChargeForOneDayTwentyThreeHoursZeroMin
[PASS] computeChargeForZeroTime
[PASS] computeChargeForOneDayOneHourOneMin
[PASS] computeChargeForThirtyDaysTwentyThreeHoursOneMin
[PASS] computeChargeForNineDaysZeroMin
[PASS] computeChargeForOneDayZeroMin
[PASS] computeChargeForTwentyOneDaysTwoHoursOneMin
[PASS] computeChargeForOneHourZeroMin
[PASS] computeChargeForOneMin
[PASS] computeChargeForOneHourOneMin
[PASS] computeChargeForTwentyThreeHoursOneMin
[PASS] computeChargeForSixteenMin
[PASS] computeChargeForTwoHoursOneMin
[PASS] computeChargeForTwentyThreeDaysTwoHoursOneMin
[PASS] computeChargeForTwoDaysOneHourZeroMin
[PASS] computeChargeForSixDaysTwentyThreeHoursOneMin
[PASS] computeChargeForFiveDaysTwentyThreeHoursZeroMin
[PASS] computeChargeForFifteenMin
[PASS] computeChargeForOneDayZeroHoursZeroMin
[PASS] computeChargeForTwoHoursZeroMin
[PASS] computeChargeForOneDayZeroHoursOneMin
[PASS] computeChargeForFortyOneMin
[PASS] computeChargeForThreeHoursOneMin
[PASS] computeChargeForThreeHoursZeroMin
[PASS] computeChargeForFourDaysOneHourOneMin
[PASS] computeChargeForFourHoursZeroMin
[PASS] computeChargeForFourDaysTwoHoursOneMin
[PASS] computeChargeForFourDaysTwoHoursZeroMin
[PASS] computeChargeForFortyMin
[PASS] computeChargeForFourDaysThreeHoursZeroMin
[PASS] computeChargeForZeroTime
[PASS] computeChargeForFourDaysThreeHoursOneMin
[PASS] computeChargeForOneHourZeroMin
[PASS] computeChargeForOneDayOneMin
[PASS] computeChargeForOneHourOneMin
[PASS] computeChargeForTwentyThreeHoursOneMin
[PASS] computeChargeForTwoHoursOneMin
[PASS] computeChargeForSevenDaysZeroHoursOneMin
[PASS] computeChargeForSevenDaysOneHourZeroMin
[PASS] computeChargeForSixDaysTwentyThreeHoursOneMin
[PASS] computeChargeForOneDayOneHourZeroMin
[PASS] computeChargeForFourteenDaysThreeHoursZeroMin
[PASS] computeChargeForTwoHoursZeroMin
[PASS] computeChargeForThreeHoursOneMin
[PASS] computeChargeForFourDaysTwoHoursOneMin
[PASS] computeChargeForZeroTime
[PASS] computeChargeForSevenDaysZeroHoursZeroMin
[PASS] computeChargeForThreeDaysOneHourOneMin
[PASS] computeChargeForOneDayZeroMin
[PASS] computeChargeForFiveDaysThreeHoursOneMin
[PASS] occupancy
[PASS] occupants
[PASS] capacity
[PASS] getDuration
[PASS] admit
[PASS] release
[PASS] availability
[PASS] computeChargeForTwentyNineDaysTwentyThreeHoursOneMin
[PASS] computeChargeForOneDayOneMin
[PASS] computeChargeForOneHourOneMin
[PASS] computeChargeForTwentyThreeHoursOneMin
[PASS] computeChargeForTwoHoursOneMin
[PASS] computeChargeForTwentyOneDaysThreeHoursZeroMin
[PASS] computeChargeForTwentyFourDaysThreeHoursOneMin
[PASS] computeChargeForOneDayOneHourZeroMin
[PASS] computeChargeForTwoHoursZeroMin
[PASS] computeChargeForThreeHoursOneMin
[PASS] computeChargeForElevenDaysTwoHoursZeroMin
[PASS] computeChargeForTwentyDaysTwoHoursOneMin
[PASS] computeChargeForZeroTime
[PASS] computeChargeForTwentyNineDaysTwentyThreeHoursZeroMin
[PASS] computeChargeForThreeDaysOneHourOneMin
[PASS] computeChargeForOneDayZeroMin
```
</details>

<details><summary>SENG301 NZTA WoF Vehicle Registration GUI/CLI BDD Acceptance-Test</summary>
   
> Cucumber test results are generated from a workflow run on a University of Canterbury JavaFX GUI/CLI project.

| Scenario Status | Count |
|---|---:|
| Passed | 35 |
| Failed | 0 |
| Skipped | 0 |

```gherkin
Feature: Vehicle Owner Account Management
--------------------------------

Scenario: [PASS] Successfully register a new vehicle owner (email) to a WoF app account

  Given I am connected to the WoF app database
  And I am not yet registered to the WoF app - the email "joe@bloggs.com" is not registered to any existing account
  When I register a WoF app account given forename "Joe", surname "Bloggs", email "joe@bloggs.com" and password "pw"
  Then the owner must be in the database
  And a message "joe@bloggs.com successfully registered to the WoF app" must be displayed on the screen

Scenario: [PASS] Unsuccessfully register an existing vehicle owner (email) to a WoF app account

  Given I am connected to the WoF app database
  And the email "one@test.com" is registered to an existing account
  When I register a WoF app account given forename "Wrong", surname "Owner", email "one@test.com" and password "two"
  Then the owner with forename "WRONG", surname "OWNER" and password "two" must not be in the database with email "one@test.com"
  And the owner with forename "TEST", surname "DUMMY", and password "one" must be in the database with email "one@test.com"
  And a message "one@test.com is already registered to an account" must be displayed on the screen

Scenario: [PASS] Unsuccessfully register a new vehicle owner with an empty forename field to a WoF app account

  Given I am connected to the WoF app database
  And I am not yet registered to the WoF app - the email "zero@test.com" is not registered to any existing account
  When I register a WoF app account given forename "", surname "Dummy", email "zero@test.com" and password "zero"
  Then the owner with email "zero@test.com" must not be in the database

Scenario: [PASS] Unsuccessfully register a new vehicle owner with an empty surname field to a WoF app account

  Given I am connected to the WoF app database
  And I am not yet registered to the WoF app - the email "zero@test.com" is not registered to any existing account
  When I register a WoF app account given forename "Test", surname "", email "zero@test.com" and password "zero"
  Then the owner with email "zero@test.com" must not be in the database

Scenario: [PASS] Unsuccessfully register a new vehicle owner with an empty email field to a WoF app account

  Given I am connected to the WoF app database
  And I am not yet registered to the WoF app - the email "" is not registered to any existing account
  When I register a WoF app account given forename "Test", surname "Dummy", email "" and password "zero"
  Then the owner with email "zero@test.com" must not be in the database

Scenario: [PASS] Unsuccessfully register a new vehicle owner with an empty password field to a WoF app account

  Given I am connected to the WoF app database
  And I am not yet registered to the WoF app - the email "zero@test.com" is not registered to any existing account
  When I register a WoF app account given forename "Test", surname "Dummy", email "zero@test.com" and password ""
  Then the owner with email "zero@test.com" must not be in the database

Scenario: [PASS] A registered vehicle owner can login to their account

  Given I am connected to the WoF app database
  And the email "one@test.com" is registered to an existing account
  When I login with email "one@test.com" and password "one"
  Then I must be logged in to my account

Scenario: [PASS] A vehicle owner who is not registered cannot login

  Given I am connected to the WoF app database
  And I am not yet registered to the WoF app - the email "three@test.com" is not registered to any existing account
  When I login with email "three@test.com" and password "three"
  Then I must not be logged in to my account

Scenario: [PASS] A registered vehicle owner can view their account information

  Given I am connected to the WoF app database
  And the email "one@test.com" is registered to an existing account
  When I login with email "one@test.com" and password "one"
  And I view my account information
  Then a message containing "TEST", "DUMMY" and null for all other fields must be displayed on the screen

Scenario: [PASS] A registered vehicle owner can not update/edit their email information

  Given I am connected to the WoF app database
  And the email "one@test.com" is registered to an existing account
  When I login with email "one@test.com" and password "one"
  And the vehicle owner account "email" field is currently "one@test.com"
  And I update the vehicle owner account "email" field to "different@test.com"
  Then the vehicle owner account "email" field is currently "one@test.com"

Scenario: [PASS] A registered vehicle owner can update/edit their address information

  Given I am connected to the WoF app database
  And the email "one@test.com" is registered to an existing account
  When I login with email "one@test.com" and password "one"
  And the vehicle owner account "address_one" field is currently "empty/null"
  And the vehicle owner account "address_two" field is currently "empty/null"
  And I update the vehicle owner account "address-one" field to "NEW"
  And I update the vehicle owner account "address-two" field to "HOME"
  Then the vehicle owner account "address_one" field is currently "NEW"
  And the vehicle owner account "address_two" field is currently "HOME"

Scenario: [PASS] A registered vehicle owner can update/edit their phone information

  Given I am connected to the WoF app database
  And the email "one@test.com" is registered to an existing account
  When I login with email "one@test.com" and password "one"
  And the vehicle owner account "phone" field is currently "empty/null"
  And I update the vehicle owner account "phone" field to "012345678"
  Then the vehicle owner account "phone" field is currently "012345678"

Scenario: [PASS] A registered vehicle owner can update/edit their password information

  Given I am connected to the WoF app database
  And the email "one@test.com" is registered to an existing account
  When I login with email "one@test.com" and password "one"
  And the vehicle owner account "password" field is currently "one"
  And I update the vehicle owner account "password" field to "two"
  Then the vehicle owner account "password" field is currently "two"

Scenario: [PASS] A registered vehicle owner can remove their account

  Given I am connected to the WoF app database
  And the email "two@test.com" is registered to an existing account
  When I login with email "two@test.com" and password "two"
  And I remove the registered vehicle owner account
  Then the owner with email "two@test.com" must not be in the database

Scenario: [PASS] A vehicle owner can register a new account with an email from a removed account

  Given I am connected to the WoF app database
  And the email "four@test.com" is registered to an existing account
  And I am logged in to my account with email "four@test.com" and password "four"
  When I remove the registered vehicle owner account
  And the owner with email "four@test.com" must not be in the database
  And I register a WoF app account given forename "New", surname "Dummy", email "four@test.com" and password "four"
  Then the owner must be in the database
  And a message "four@test.com successfully registered to the WoF app" must be displayed on the screen


Feature: Vehicle Registration Management
-------------------------------

Scenario: [PASS] Successfully register a new vehicle to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "NEW123" is not in the database
  When I register a vehicle with plate "NEW123", make "Toyota", model "Corolla", manufacture date "2010-01-01", address one "A", address two "Home", type "MA", and fuel type "petrol"
  Then the vehicle with plate "NEW123" is now in the database
  And a message "NEW123 successfully registered to one@test.com" is displayed on the screen

Scenario: [PASS] Unsuccessfully register an existing vehicle registration to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  When I register a vehicle with plate "ABC123", make "FORD", model "MODEL T", manufacture date "1970-01-01", address one "MICHIGAN", address two "USA", type "MA", and fuel type "OTHER"
  And a message "ABC123 is already registered to an account" is displayed on the screen

Scenario: [PASS] Unsuccessfully register a new vehicle to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  When I register a vehicle with plate "", make "Toyota", model "Corolla", manufacture date "2010-01-01", address one "A", address two "Home", type "MA", and fuel type "petrol"
  Then the vehicle with plate "" is now not in the database

Scenario: [PASS] Unsuccessfully register a new vehicle to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  When I register a vehicle with plate "NEW456", make "", model "Corolla", manufacture date "2010-01-01", address one "A", address two "Home", type "MA", and fuel type "petrol"
  Then the vehicle with plate "NEW456" is now not in the database

Scenario: [PASS] Unsuccessfully register a new vehicle to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  When I register a vehicle with plate "NEW456", make "Toyota", model "", manufacture date "2010-01-01", address one "A", address two "Home", type "MA", and fuel type "petrol"
  Then the vehicle with plate "NEW456" is now not in the database

Scenario: [PASS] Unsuccessfully register a new vehicle to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  When I register a vehicle with plate "NEW456", make "Toyota", model "Corolla", manufacture date "", address one "A", address two "Home", type "MA", and fuel type "petrol"
  Then the vehicle with plate "NEW456" is now not in the database

Scenario: [PASS] Unsuccessfully register a new vehicle to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  When I register a vehicle with plate "NEW456", make "Toyota", model "Corolla", manufacture date "2010-01-01", address one "", address two "Home", type "MA", and fuel type "petrol"
  Then the vehicle with plate "NEW456" is now not in the database

Scenario: [PASS] Unsuccessfully register a new vehicle to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  When I register a vehicle with plate "NEW456", make "Toyota", model "Corolla", manufacture date "2010-01-01", address one "A", address two "", type "MA", and fuel type "petrol"
  Then the vehicle with plate "NEW456" is now not in the database

Scenario: [PASS] Successfully list the information for an existing vehicle registration to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  When I list the information for vehicle "ABC123"
  Then a message containing "ABC123", "FORD", "MODEL T", "1970-01-01", "MICHIGAN", "USA", "MA", "OTHER" is displayed on the screen

Scenario: [PASS] Successfully edit/update an existing vehicle registration make field to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  And the vehicle "ABC123" field "make" has existing value "FORD" in the database
  When I update/edit vehicle "ABC123" field "make" to the new value "Honda"
  Then the vehicle "ABC123" field "make" will have value "HONDA" in the database

Scenario: [PASS] Successfully edit/update an existing vehicle registration model field to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  And the vehicle "ABC123" field "model" has existing value "MODEL T" in the database
  When I update/edit vehicle "ABC123" field "model" to the new value "Civic"
  Then the vehicle "ABC123" field "model" will have value "CIVIC" in the database

Scenario: [PASS] Successfully edit/update an existing vehicle registration manufacture date field to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  And the vehicle "ABC123" field "manufacture_date" has existing value "0" in the database
  When I update/edit vehicle "ABC123" field "manufacture-date" to the new value "2000-12-12"
  Then the vehicle "ABC123" field "manufacture_date" will have value "2000-12-12" in the database

Scenario: [PASS] Successfully edit/update an existing vehicle registration address one field to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  And the vehicle "ABC123" field "address_one" has existing value "MICHIGAN" in the database
  When I update/edit vehicle "ABC123" field "address-one" to the new value "Different"
  Then the vehicle "ABC123" field "address_one" will have value "DIFFERENT" in the database

Scenario: [PASS] Successfully edit/update an existing vehicle registration address two field to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  And the vehicle "ABC123" field "address_two" has existing value "USA" in the database
  When I update/edit vehicle "ABC123" field "address-two" to the new value "Place"
  Then the vehicle "ABC123" field "address_two" will have value "PLACE" in the database

Scenario: [PASS] Successfully edit/update an existing vehicle registration vehicle type field to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  And the vehicle "ABC123" field "vehicle_type" has existing value "MA" in the database
  When I update/edit vehicle "ABC123" field "type" to the new value "O"
  Then the vehicle "ABC123" field "vehicle_type" will have value "O" in the database

Scenario: [PASS] Successfully edit/update an existing vehicle registration fuel type field to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  And the vehicle "ABC123" field "fuel_type" has existing value "OTHER" in the database
  When I update/edit vehicle "ABC123" field "fuel" to the new value "petrol"
  Then the vehicle "ABC123" field "fuel_type" will have value "PETROL" in the database

Scenario: [PASS] Unsuccessfully edit/update an existing vehicle registration plate field to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  And the vehicle "ABC123" field "plate" has existing value "ABC123" in the database
  When I update/edit vehicle "ABC123" field "plate" to the new value "NEW789"
  Then the vehicle "ABC123" field "plate" will have value "ABC123" in the database

Scenario: [PASS] Successfully remove an existing vehicle registration to an owner's WoF app account

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  When I remove the vehicle "ABC123"
  Then the vehicle with plate "ABC123" is now not in the database

Scenario: [PASS] Successfully register a vehicle to an owner's WoF app account wth a plate from a removed vehicle

  Given I am connected to the WoF app database
  And I am logged in to my account with email "four@test.com" and password "four"
  And the vehicle with plate "NEW123" is not in the database
  When I register a vehicle with plate "NEW123", make "Toyota", model "Corolla", manufacture date "2010-01-01", address one "A", address two "Home", type "MA", and fuel type "petrol"
  Then the vehicle with plate "NEW123" is now in the database
  And a message "NEW123 successfully registered to four@test.com" is displayed on the screen

Scenario: [PASS] Successfully remove existing vehicle registration(s) when the owner's WoF app account is removed

  Given I am connected to the WoF app database
  And I am logged in to my account with email "one@test.com" and password "one"
  And the vehicle with plate "ABC123" is in the database
  When I remove the registered vehicle owner account
  Then the owner with email "one@test.com" must not be in the database
  And the vehicle with plate "ABC123" is now not in the database

```
</details>

![chch_airport](https://github.com/user-attachments/assets/3112c1a4-3fad-46a9-97da-51a66a34d67b)

##

<details>
<summary>Personal Note</summary> 

The Java source code is mostly unchanged for both SENG201 & SENG301 GUI projects in this port to a web UI. However, the code changed significantly during project completion & also assessment. I almost left university altogether when completing SENG201 - my first software engineering project - because of an arcane Eclipse IDE bug producing erroneous computational results, or so it seemed. I couldn't successfully debug this; just as I couldn’t avoid the incessant attacks against me, mostly while I slept late at night, especially with some (projectiles) causing temporary technical blindness. This is because the fault was never in the source code & was beyond my control. The "IDE bug" was resolved last-moment with an evening remaining to finish the project. I am unsure about the assessment for this project & never did I question such matters back then, especially with an increased workload from average, & when still recovering, &, as mentioned, suffering from multiple severe injuries. This is potentially the most epic in such a context, potentially in all history, as it stands, consistently under eternal 24/7 wrongdoing/terror/"special" plague effects - potentially continuing since home invasions, A-to-K (eradicate) me since ~6/7 - & extremely unnaturally, uninhabitable conditions (just ask those randomly home invasion attacking me nocturnally while I'm sleeping). I was also "leaving" to participate in formal international exchanges on the other side of the globe. Upon returning, I completed SENG301, along with all other computer science, software engineering, embedded systems & computer systems courses taught at the University of Canterbury, or of equivalent standing from studies abroad. I also completed all GIS & remote sensing, relevant engineering, math, stats & data analysis studies, & not to mention other topics, including history with top-score rewards, & a second degree in parallel to this, or rather completed just before SENG301, just as instructed; consistently ending how it begins; double the requirements for less than half the rights. The first time I questioned any assessment concerned random, ambiguous, nonsensical, intoxicated dribble excusing a base-pass for the SENG301 project. This was "corrected", but unlike an advanced software testing exam given at Uppsala University, where a very blatant base-pass error - with excuses like missing TDD discussion in an essay on test-driven development (?!?) & not to mention miscounting ~40% of marks (& this was before Gen-AI) - was corrected to an evident distinction, & although involving a greater extent of correction in grade points, with a lower base-pass score, it did not even come close to distinction, which is also lower; let alone the top A+ grade. The excuse: irrelevant documentation not included in assessment instructions (& yet worth more than 20% of the grade weighting even though it takes nothing but a single click of a button to generate from the source code?!?). Such maltreatment did not come close to ending there, & with those attacking me when completing SENG201 now all directly involved in multiple course assessments while completing the 2nd (aka 3rd BE level) professional examination, possibly the greatest Catch-22 1984 (digital) fraud oppression in history manifested, at least in such context, if not the first such case - with exception to such levels of grand incompetence - directly spanning years, not to mention many more initially directly & indirectly targeted, nor all those indirectly following forever. This is exactly why it is illegal to not provide a service paid for in NZ, but in normal circumstances those paying are not also those working, let alone only, & this was a year-long project course! A whole year stolen, & not to mention all those preceding to get there, or at least in my circumstances, which were somehow void (as if I was never really there, but for all infinite gaslight inverted blame possibly contradictory & self-defeating); like their own socio-technical system, as aptitude was supposedly never a course pass requirement (in "Agile"?!?), explaining why those I had previously completed projects with experienced no issues continuing to do nothing but be everyone else after completion; like a perfect Ponzi. This happened at the same time I was finally receiving surgery relating to a stab wound, & coming undone from poison symtpoms, so you can just imagine the true extent of such context as unimaginable as it all is unfolding before my eyes, unforeseeably beyond my control; like my wallet; like all the cyberattacks in parallel. In that case I don't think they can get away with blaming (immigrant) females instead, even if their complementing random physical attacks are of such nature. You can probably also imagine reporting the “vile” harassing & wrongdoing nature of such “assessment” as the only offense recognized in my entire academic history, & one worthy of a -3 (X) GPA grade, not that there is much left of my academia with the Quixotic cyclic Kafkaesque interfering exacerbating the (most?) extremely schizo maniacal undermining & sabotaging of all, to say the least, that I was reporting. Can you imagine this farcical process started at the start of my first holiday (excluding formal exchange travel), just as I arrive on the other side of the World, during the holidays! That's one thing they can never blatantly say they are instead, at least not after, but when would that ever matter to such autism. Can you imagine it lasted four years; longer than it would take - probably more than twice anyone like me - to complete the studies in the first place! Technically, it still isn't over, & as long as they're debilitating "decisions", it likely never will be. There's no coincidence this doesn't involve just one life obsession. If only I left during SENG201, I would have something to show from all of this. In contrast, when I am assisting teaching in software engineering (& testing, cyber security, machine learning, data mining, data engineering, ..), I am supposedly randomly declaring myself "invalid", perfectly for scapegoat blame, following the equivalent of at least 10x overtime, exactly as "instructed", for not even 1x income, for hundreds of students obviously abusing Gen-AI to explain why they can never complete what I did in ease after honestly completing my course/project work, but can always “teach” me instead, obviously exactly as the teachers taught them to, because at the end of the day all they supposedly required was a grammar correction, without mention to which, other than not of those instructing, & which never even happened anyway. I did all of this literally green inside & out, while already recovering from multiple severe infections literally head-to-toe, chronic malpractice, & not to mention consistent 24/7 wrongdoing plague effects, home invasions, bricked devices, etc, etc, etc. Now I am finding my studies don’t even prove basic knowledge of logic, not even years of discrete math, not with such chronic misrepresentations, potentially also the most epic in all history, in such a context, as it stands.
</details>

## Contribute

Before making a Pull Request, ensure it addresses an Issue, and verify the branch passes:

```bash
npm run verify:fix
```
