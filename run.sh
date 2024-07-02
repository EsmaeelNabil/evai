#!/bin/sh

ollama pull llava:latest &&

ollama create qm &&

docker-compose up --build