#!/usr/bin/env bash

# docker login -u "username" -p "password" docker.com
docker build -t appwish/commentservice . && docker push appwish/commentservice