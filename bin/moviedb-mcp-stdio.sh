#!/usr/bin/env bash
set -euo pipefail

# Class 19: Claude Desktop and Claude Code start this script as a subprocess and
# exchange JSON-RPC messages with it over stdin and stdout. exec replaces this
# shell with the JVM, so no wrapper process stays behind. set -euo pipefail stops
# the script at the first error, so the host sees a clear failure and the
# connection does not hang. logback-stdio.xml sends all logs to stderr, and the
# stdio profile turns off the web container and the banner.
#
# Edit both of these before the first run. They are this machine's paths,
# not a convention: JAR_PATH is wherever you cloned the project, and
# JAVA_HOME must point at a JDK 21 or newer that actually exists here.
JAR_PATH="${HOME}/projects/moviedb/target/moviedb-0.0.1-SNAPSHOT.jar"
JAVA_HOME="${JAVA_HOME:-/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home}"

exec "${JAVA_HOME}/bin/java" \
    -Dlogging.config=classpath:logback-stdio.xml \
    -jar "${JAR_PATH}" \
    --spring.profiles.active=stdio
