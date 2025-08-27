#!/bin/bash

# Define the API endpoint URL
API_URL="http://localhost:8080/api/v1/courses/"

# Read each JSON object from data2.json, pipe it through jq for compact output,
# and then loop through each course object.
jq -c '.[]' data3.json | while read course; do
  # Send a POST request to the API with the course data
  curl -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "$course"

  sleep 3
done