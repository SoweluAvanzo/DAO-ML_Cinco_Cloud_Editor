#!/bin/bash

# Check if a port number was provided
if [ -z "$1" ]; then
  echo "Usage: $0 <port>"
  exit 1
fi

port=$1

# Check if the port is occupied
if lsof -i :$port > /dev/null; then
  echo "Port $port is occupied."
  echo $(kill -9 $(lsof -t -i :$port | tail -n 1))
else
  echo "Port $port is available."
fi

sleep 1
