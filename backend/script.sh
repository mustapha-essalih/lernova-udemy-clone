#!/bin/bash
API_URL="http://localhost:8080/api/v1/courses/"

jq -c '.[]' data2.json | while read course; do
  curl -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "$course"
done