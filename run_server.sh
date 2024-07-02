#!/bin/sh

if ! command -v ollama >/dev/null 2>&1; then
    echo "Please install Ollama first https://ollama.com/"
    exit 1
fi

if ! command -v docker >/dev/null 2>&1; then
    echo "Please install Docker first https://docs.docker.com/get-docker/"
    exit 1
fi

echo "Pulling llava:latest"
ollama pull llava:latest &&

echo "Creating qm model"
ollama create qm &&

echo "Running backend service"
docker-compose up --build