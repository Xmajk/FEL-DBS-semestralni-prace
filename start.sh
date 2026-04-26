#!/usr/bin/env bash
# Spustí HR CLI aplikaci.
# DB připojení lze přebít přes proměnné prostředí:
#   SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD
set -e

# Nutné kvůli Maven: systémový Maven spouští Java 25, ale projekt targetuje Java 17.
export JAVA_HOME=/usr/lib/jvm/jdk-17.0.12-oracle-x64

mvn -q -DskipTests clean package
java -jar target/semestralniPraceDBS-0.1.0.jar
