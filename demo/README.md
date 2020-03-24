# Table of Contents
1. [System requirements](#system-requirements)
1. [Starting Rendezvous Service in Docker](#instructions)
    * [Docker Dependent Files](#get-dependent-files)
    * [Keystore settings](#create-java-keystore-files)
    * [Docker compose settings](#docker-compose-configuration)
1. [Run service](#docker-commands)

# System requirements

* Operating system: Linux Ubuntu 18.04.

*  Linux packages:<br/><br/>
`Docker engine (minimum version 18.06.0)`<br/><br/>
`Docker-compose (minimum version 1.23.2)`<br/>

* Internet connection

# Instructions

## Get dependent files
If you are working from the Release Package, these files have already been copied to this folder and you need not do anything.
If you are working with source code, build the project and it will copy the required dependencies to the demo folder.

## Create Java keystore files
See instructions in the [Secure Device Onboard Rendezvous Service README](https://github.com/secure-device-onboard/rendezvous-service/#generate-keystores). Once the keystore and truststore files are created, update docker-compose.yml to reflect the file name, path and password.  The default configured keystore is /certs/rendezvous-keystore.jks and the default configured truststore is /certs/rendezvous-truststore.jks. Default passwords for both: 123456

The rendezvous service will not start if the keystore is not present. If truststore is not present, rendezvous service will not be able to communicate with rendezvous verification service.

***IMPORTANT***:

-  This is an example implementation using simplified credentials. This must be changed while performing production deployment

## Docker-compose configuration
Review the docker-compose.yml file and follow instructions in the file to customize for your environment.

# Docker commands

## Start Docker
* Use the following command to start the docker container.
```
$ sudo docker-compose up -d --build
```
* Your Docker container is now ready to support TO0 & TO1 protocol operations.

## Stop Docker

* Use the following command to stop all running docker containers.
```
$ sudo docker stop $(sudo docker ps -a -q)
```

## Clean up containers

* Use the following command to delete all the docker artifacts. (Note: docker containers must be stopped before deleting them)
```
$ sudo docker system prune -a
```
