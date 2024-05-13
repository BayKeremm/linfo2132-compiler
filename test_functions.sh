#!/bin/bash

# Compile your program with Gradle and suppress the output
if gradle run --args="./codegen_test_files/functions.lang -o functions" > /dev/null ; then
  echo "compiled successfully"
else
  echo "Could not compile the file!"
  gradle run --args="./codegen_test_files/functions.lang -o functions"
  exit 1
fi

# Run your program and store its output
output=$(java functions)
# Define the expected outputs
expected_outputs=("16" "25.0" "kerem" "yessir" "elfje" "100" "222.222" "bruh")


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
    echo "Test 04: Functions: Passed!"
else
    echo "Test 04: Functions: FAILED!"
fi
