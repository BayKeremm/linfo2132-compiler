#!/bin/bash

# Compile your program with Gradle and suppress the output
if gradle run --args="./codegen_test_files/constants.lang -o constants" > /dev/null ; then
  echo "compiled successfully"
else
  echo "Could not compile the file!"
  #gradle run --args="./codegen_test_files/constants.lang -o constants"
  exit 1
fi
# Run your program and store its output
output=$(java constants)

# Define the expected outputs
expected_outputs=("6" "4.0" "hello" "true" "12")

# Count of expected outputs found in the program's output
found_count=0

# Loop through the expected outputs
for expected_output in "${expected_outputs[@]}"; do
    # Check if the output contains the expected output
    if grep -q "$expected_output" <<< "$output"; then
        # If found, increment the count
        ((found_count++))
    fi
done

# Check if all expected outputs are found
if [ "$found_count" -eq "${#expected_outputs[@]}" ]; then
    echo "Test 01: Constants: Passed!"
else
    echo "Test 01: Constants: FAILED!"
fi
