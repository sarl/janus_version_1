#!/bin/sh

# Get the directy where this script is installed
DIRNAME=`dirname $0`
DIRNAME=`readlink -f "$DIRNAME"`

# Detect the java
if test -n "$JAVA_HOME"
then
	echo "JAVA_HOME=$JAVA_HOME"
	JAVA="$JAVA_HOME/bin/java"
else
	JAVA="java"
fi

# Check for the right version of Java
JAVA_VERSION=`$JAVA -version 2>&1 | awk '/version/ {print $3}' | egrep -o '[0-9]+\.[0-9]+'`

echo "Version=$JAVA_VERSION"

if test "v$JAVA_VERSION" != "v1.6"
then
	echo "ERROR: you must use a JDK 1.6 to compile the Janus platform," >&2
	echo "ERROR: because several parts of the platform are not compatible " >&2
	echo "ERROR: newver versions (eg. Android modules)." >&2
	echo "ERROR: Recommended command line:" >&2
	echo "ERROR: $> JAVA_HOME=\"path/to/jdk/1.6\" ./build.sh" >&2
	exit 1
fi

# Compile
cd "$DIRNAME/janus"
mvn -Dmaven.test.skip=true clean
cd "$DIRNAME/janus-ui"
mvn -Dmaven.test.skip=true clean

exit 0
