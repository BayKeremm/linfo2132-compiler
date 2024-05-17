#!/bin/bash

mkdir -p submission

cp -r test src codegen_test_files build.gradle.kts test_*.sh TEST_ME.sh submission/

zip -r submission.zip submission

