#!/bin/bash
rm -rf sc
find ../test/input -name '*.sa' -delete
find ../test/input -name '*.sc' -delete
find ../test/input -name '*.ts' -delete
find ../test/input -name '*.xml' -delete
find ../test/input -name '*.c3a' -delete
java -jar ../sablecc.jar grammaireL.sablecc
