#!/bin/bash

# Start the responder in the background
mvn exec:java@responder &

# Wait for a moment to ensure the responder is up
sleep 5

# Run the requester
mvn exec:java@requester

# After the requester finishes, kill the responder
kill $!