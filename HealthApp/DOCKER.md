# Docker Image
`docker run hello-world`
- Creates a Docker container with a image named `hello-world`

# Docker file
- A Dockerfile is a text document that contains all the commands which could be called on the command line to assemble or build an image.
- The following is a sample command for building an image using a Dockerfile:
```aidl
 docker build -f tomcat.df -t tomcat_debug .
```
- The preceding command would look for the Dockerfile tomcat.df in the current directory specified by ".", and build the image with the tag tomcat_debug