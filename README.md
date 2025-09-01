# Anagram
The signaling server powers the operation of the P2P video chat application.

## Build
You need to build the JAR artifact of the signaling server.

## Run
You can run the signaling server on your server using a Docker container:
```bash
docker run --detach \
	--name anagram-ws \
	--restart=always \
	--publish 8888:8888 \
	--volume YOUR_FOLDER_WITH_JAR:/app \
	openjdk:18 \
	java -jar /app/anagram.jar
```
You will also need to run the STUN and TURN services on your server using a Docker container:
```bash
docker run --detach \
	--name stun-server \
	--restart=always \
	--publish 3478:3478/udp \
	--publish 49160-49200:49160-49200/udp \
	instrumentisto/coturn \
	--external-ip='YOUR_SERVER_IP' \
	--min-port=49160 --max-port=49200 \
        --user=username:password \
        --realm=YOUR_SERVER_DOMAIN
```

## License

Copyright 2025 Roman Popov

Licensed under the GNU GPLv3: https://www.gnu.org/licenses/gpl-3.0.html
