# Anagram
P2P Video Chat for Android Devices

## Build
You need to build the JAR artifact of the signaling server.

## Run
You can run the signaling server on your server either by executing:
```bash
java -jar anagram.jar
```
or using a Docker container:
```bash
docker run --detach \
	--name anagram-ws \
	--restart=always \
	--publish 8888:8888 \
	--volume YOUR_FOLDER_WITH_JAR:/app \
	openjdk:18 \
	java -jar /app/anagram.jar
```

## License

Copyright 2013-2025 Roman Popov

Licensed under the GNU GPLv3: https://www.gnu.org/licenses/gpl-3.0.html
