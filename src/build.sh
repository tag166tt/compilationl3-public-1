#!/bin/bash
rm -rf sc
find ../test/input -name '*.sa' -delete
find ../test/input -name '*.sc' -delete
find ../test/input -name '*.ts' -delete
find ../test/input -name '*.xml' -delete
find ../test/input -name '*.c3a' -delete
find ../test/input -name '*.pre-nasm' -delete
find ../test/input -name '*.nasm' -delete
java -jar ../sablecc.jar grammaireL.sablecc
