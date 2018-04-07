# JAlgoArena Ranking [![Build Status](https://travis-ci.org/spolnik/JAlgoArena-Ranking.svg?branch=master)](https://travis-ci.org/spolnik/JAlgoArena-Ranking) [![codecov](https://codecov.io/gh/spolnik/JAlgoArena-Ranking/branch/master/graph/badge.svg)](https://codecov.io/gh/spolnik/JAlgoArena-Ranking) [![GitHub release](https://img.shields.io/github/release/spolnik/jalgoarena-ranking.svg)]()

JAlgoArena Ranking is service dedicated for collecting accepted users submissions with best results and exposing that data together with calculating ranking for all problems as well as for particular problem.

Demo: https://jalgoarena-ui.herokuapp.com/

- [Introduction](#introduction)
- [REST API](#rest-api)
- [Components](#components)
- [Continuous Delivery](#continuous-delivery)
- [Infrastructure](#infrastructure)
- [Running Locally](#running-locally)
- [Notes](#notes)

## Introduction

- JAlgoArena Ranking ir responsible for calculating ranking and exposing it via REST API

![Component Diagram](https://github.com/spolnik/JAlgoArena-Ranking/raw/master/design/component_diagram.png)

# REST API

| Endpoint | Description |
| ---- | --------------- |
| [GET /ranking] | Get general ranking list |
| [GET /ranking/:problemId] | Get ranking for particular problem |
| [GET /solved-ratio] | Get all problems solved by users ratio |

## Components

- [JAlgoArena](https://github.com/spolnik/JAlgoArena)
- [JAlgoArena UI](https://github.com/spolnik/JAlgoArena-UI)
- [JAlgoArena Auth Server](https://github.com/spolnik/JAlgoArena-Auth)
- [JAlgoArena Eureka Server](https://github.com/spolnik/JAlgoArena-Eureka)
- [JAlgoArena API Gateway](https://github.com/spolnik/JAlgoArena-API)

## Continuous Delivery

- initially, developer push his changes to GitHub
- in next stage, GitHub notifies Travis CI about changes
- Travis CI runs whole continuous integration flow, running compilation, tests and generating reports
- coverage report is sent to Codecov
- application is deployed into Heroku machine

## Infrastructure

- Heroku (PaaS)
- Xodus (embedded highly scalable database) - http://jetbrains.github.io/xodus/
- Spring Boot, Spring Cloud (Eureka Client)
- TravisCI - https://travis-ci.org/spolnik/JAlgoArena-Submissions
- Apache Kafka

## Running locally

There are two ways to run it - from sources or from binaries.

### Running from binaries
- go to [releases page](https://github.com/spolnik/JAlgoArena-Ranking/releases) and download last app package (JAlgoArena-Ranking-[version_number].zip)
- after unpacking it, go to folder and run `./run.sh` (to make it runnable, invoke command `chmod +x run.sh`)
- you can modify port and Eureka service url in run.sh script, depending on your infrastructure settings. The script itself can be found in here: [run.sh](run.sh)

### Running from sources
- run `git clone https://github.com/spolnik/JAlgoArena-Ranking` to clone locally the sources
- now, you can build project with command `./gradlew clean stage` which will create runnable jar package with app sources. Next, run `java -jar build/libs/jalgoarena-auth-*.jar` which will start application
- there is second way to run app with gradle. Instead of running above, you can just run `./gradlew clean bootRun`

## Notes
- [Travis Builds](https://travis-ci.org/spolnik)

![Component Diagram](https://github.com/spolnik/JAlgoArena/raw/master/design/JAlgoArena_Logo.png)
