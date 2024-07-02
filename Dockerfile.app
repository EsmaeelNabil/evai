# Dockerfile
FROM node:lts-bullseye

# Create app directory
WORKDIR /usr/src/app

# Install app dependencies
COPY package*.json ./

RUN npm install

# Bundle app source
COPY . .

EXPOSE 3993


CMD [ "node", "src/main.js" ]
