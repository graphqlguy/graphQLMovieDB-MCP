#!/usr/bin/env bash
set -euo pipefail

# Class 19: Claude Desktop / Claude Code launch this as a subprocess and speak
# JSON-RPC over stdin/stdout. exec replaces this shell with the JVM so no wrapper
# process lingers; set -euo pipefail fails fast so the host sees a clear error
# rather than a hanging connection. logback-stdio.xml routes all logs to stderr
# and the stdio profile disables the web container and banner.
JAR_PATH="${HOME}/projects/moviedb/target/moviedb-0.0.1-SNAPSHOT.jar"
JAVA_HOME="${JAVA_HOME:-/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home}"

exec "${JAVA_HOME}/bin/java" \
    -Dlogging.config=classpath:logback-stdio.xml \
    -jar "${JAR_PATH}" \
    --spring.profiles.active=stdio
