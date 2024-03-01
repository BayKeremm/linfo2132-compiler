#!/bin/bash

mkdir -p submission

cp -r test src build.gradle.kts submission/

zip -r submission.zip submission

