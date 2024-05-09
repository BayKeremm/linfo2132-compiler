#!/bin/bash

# Compile your program with Gradle and suppress the output
gradle run --args="./codegen_test_files/builtin.lang -o builtin"&&

# Run your program and store its output
output=$(java builtin)
# Define the expected outputs
expected_outputs=("3" "11" "33" "42" "5")




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
    echo "Test 06: Builtin functions: Passed!"
else
    echo "Test 06: Builtin functions: FAILED!"
fi
