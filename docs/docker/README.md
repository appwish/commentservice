# Comment Service

## Docker:

At this stage we are using docker for setting up the local development 
environment.

To install docker on you local machine, follow the below links.

- Mac: https://docs.docker.com/docker-for-mac/install/
- Windows: https://docs.docker.com/docker-for-windows/install/
- Linux: https://docs.docker.com/install/linux/docker-ce/ubuntu/
    
## Docker Compose

In [appwish/deployment](https://github.com/appwish/deployment), required tools ans script are 
available for starting all the service in one go.

For setting the environment variables in docker, the following configuration
file "docker-compose.json" should be created. 

When starting the docker image, "docker-compose.json" 
file will be providing the environment related information.

## Building Docker Image for Comment Service

The comment service image has to be build, so that it can be used by
all service when configured in the deployment.

The build the image the following process has to be followed.

1. Make sure you have credential to login to docker repository.
    
    follow this [Docker Hub](https://hub.docker.com/signup) for more details.

2. Creating the Dockerfile

    By reading the instruction on this file, docker creates the image.
    [Dockerfile](https://github.com/appwish/commentservice/Dockerfile)

3. Building the image.

    Open the command prompt, based on the operating system
    
    
    ## Login to the current working directory
    
    ## You will prompted for username and password.
    ## provide the details created in step 1
    docker login
    
    ## Once logged in. Run the following script to build and push the docker image
    ./docker-build.sh


## Running Comment Service in Local

Once docker image has been build. use the following command to run the 
the service locally.

    
    docker run -it -p 8080:8080 appwish/commentservice:latest