# In Progress.. 





## Weather Application
Welcome to my Weather App!

## General Information 
- Spring Boot Application
- Automatically deployed on heroku via CircleCI
- https://weather-application-tm.herokuapp.com/

## Technologies Used
- REST API
- JWT (Jason Web Token)
- MySQL / H2 database
- JUnit & Integration tests
- CircleCI
- Heroku
- Postman 2
- JavaMail - Email verification, account activation link

## Features
- JSON Responses from Weather Application
  {
  "country": "CZ",
  "name": "Žižkov",
  "temperature": "14,36 °C"
  }
- Make you list of favourite Cities {
  "listOfFavouriteCities": [
  {
  "id": 1,
  "name": "Praha",
  "longitude": 14.43780049999,
  "latitude": 50.0755381
  },
  {
  "id": 2,
  "name": "Říčany",
  "longitude": 14.654276,
  "latitude": 49.991678
  }
  ]
  }
## Room for Improvement
To do:
- React.js front-end

## Contact
- Created by t.mikula@centrum.cz,
  https://www.linkedin.com/in/tom%C3%A1%C5%A1-mikula-29695b240/
- feel free to contact me!

## My Notes
- for EMAIL ACTIVATION LINK, I use gmail ( smtp.gmail.com ) . You must have EmailConfig and application.properties modified.
  From May 2022, you must use generated "application password" (-> Google Account/Settings) instead of you login password.