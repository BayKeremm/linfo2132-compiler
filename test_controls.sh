#!/bin/bash

# Compile your program with Gradle and suppress the output
if gradle run --args="./codegen_test_files/controls.lang -o controls" > /dev/null ; then
  echo "compiled successfully"
else
  echo "Could not compile the file!"
  gradle run --args="./codegen_test_files/controls.lang -o controls"
  exit 1
fi

# Run your program and store its output
output=$(java controls)

# Define the expected outputs
expected_outputs=("81" "0" "82" "1" "83" "2" "84" "3" "85" "4" "86" "5" "87")




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
    echo "Test 03: Controls: Passed!"
else
    echo "Test 03: Controls: FAILED!"
fi
