#!/bin/bash

# Compile your program with Gradle and suppress the output
if gradle run --args="./codegen_test_files/scope.lang -o ./codegen_test_files/scope.class" > /dev/null ; then
  echo "compiled successfully"
else
  echo "Could not compile the file!"
  gradle run --args="./codegen_test_files/scope.lang -o scope"
  exit 1
fi

# Run your program and store its output
output=$(cd codegen_test_files && java scope)
# Define the expected outputs
expected_outputs=("10" "99" "22" "8.8" "7.7" "5.5" "hello" "yes it is me" "hello")
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
    echo "Test 08: Scope test: Passed!"
else
    echo "Test 08: Scope test: FAILED!"
fi
