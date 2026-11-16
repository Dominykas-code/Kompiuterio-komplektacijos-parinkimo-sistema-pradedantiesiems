#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p .core-build
javac -encoding UTF-8 -d .core-build backend/src/main/java/lt/pcbuilder/domain/*.java backend/src/test/java/lt/pcbuilder/domain/CoreSelfTest.java
java -cp .core-build lt.pcbuilder.domain.CoreSelfTest
