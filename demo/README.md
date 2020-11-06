# Table of Contents
1. [System requirements](#system-requirements)
1. [Starting Rendezvous Service in Docker](#instructions)
    * [Docker Dependent Files](#get-dependent-files)
    * [Trust_management](#trust-management)
    * [Keystore settings](#create-java-keystore-files)
    * [Docker compose settings](#docker-compose-configuration)
1. [Run service](#docker-commands)

**NOTE**: The Rendezvous Service demo is provided solely to demonstrate the operation of the Secure Device Onboard Rendezvous service with an example database and configuration.   _This demo is not recommended for use in any production capacity._  Appropriate security measures with respect to key-store management and configuration management should be considered while performing production deployment of any Secure Device Onboard components.

# System requirements

* Operating system: Ubuntu 20.04.

*  Linux packages:<br/><br/>
`Docker engine (minimum version 18.09)`<br/><br/>
`Docker-compose (minimum version 1.21.2)`<br/>

# Instructions

## Get dependent files
If you are working from the Release Package, these files have already been copied to this folder and you need not do anything.
If you are working with source code, build the project and it will copy the required dependencies to the demo folder.

## Trust management
The hashes for the default public keys present in iot-platform-sdk and supply-chain-tools are listed in following table.

| Hashes in Allowlist | SDO Component |
| --- | --- |
| 707B6451B8319C28E412F847E17BB87995441AF356007A03A3A4AC7745A5223D | Supply Chain Toolkit - Manufacturer ECDSA 256 |
| 25D42F0536CE584E5812AB8750E80E7464742B4B65347BEA90AD4BBC71D3FFA6 | Supply Chain Toolkit - Manufacturer ECDSA 384 |
| 283ADF4CCB527C19A72CFB21A9FF7B555788E6B365CEF3A26C6B876EE0FFE017 | Supply Chain Toolkit - Manufacturer RSA 2048 |
| 85A481BBC2DA15EDD7301FF92BA2BB60093D5864A8207F9D78A399B32AB4CFF4 | Supply Chain Toolkit - Reseller ECDSA 256 |
| 31726603CB0751BFB926B6436369265557855744338FFC3307693E0D14D5241D | Supply Chain Toolkit - Reseller ECDSA 384 |
| 2ED65928AD50CB8542E648B9CD5C8B4BFB76DA870C723B16464F49F5140F7098 | Supply Chain Toolkit - Reseller RSA 2048 |
| 1DAC184C6A8BB2D00665F4CFC55B1F55AC9BFB4C899B06827C0C1990A1A0F74C | IOT Platform SDK - ECDSA 256 |
| 834F83875910C8507CE935BE2F947DCF854E6554C3ACB79893ACF91220EA5D8B | IOT Platform SDK - ECDSA 384 |
| B4E95FB7062303BEB84FBB606ED75CCE99D1C4B6CC88F71E65286CAD7C74F3A5 | IOT Platform SDK - RSA 2048 |

To add any other credentials in allowlist, see instructions in the [Secure Device Onboard Rendezvous Service README](https://github.com/secure-device-onboard/rendezvous-service/#trust-management).

## Create Java keystore files
See instructions in the [Secure Device Onboard Rendezvous Service README](https://github.com/secure-device-onboard/rendezvous-service/#generate-keystores). Once the keystore and truststore files are created, update docker-compose.yml to reflect the file name, path and password. The default configured keystore is /certs/rendezvous-keystore.jks and the default configured truststore is /certs/rendezvous-truststore.jks. Default passwords for both: 123456

The rendezvous service will not start if the keystore is not present. If truststore is not present, rendezvous service will not be able to communicate with rendezvous verification service.

***IMPORTANT***:

-  This is an example implementation using simplified credentials. This must be changed while performing production deployment

## Docker-compose configuration
Review the docker-compose.yml file and follow instructions in the file to customize for your environment.

# Docker commands

Before starting the docker deployment ensure that the local redis server is not in running state.
* Command to check the local redis server status
```
$ sudo systemctl status redis
```
* Command to stop local redis server status
```
$ sudo systemctl stop redis
```

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
