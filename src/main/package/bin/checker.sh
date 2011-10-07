#!/bin/sh
#
# Shell script to start the Overthere Connection Checker
#

# Get Java executable
if [ -z "$JAVA_HOME" ] ; then
  JAVACMD=java
else
  JAVACMD="${JAVA_HOME}/bin/java"
fi

# Get checker home dir
if [ -z "$CHECKER_HOME" ] ; then
  BIN_DIR=`dirname "$0"`
  cd "$BIN_DIR"
  ABSOLUTE_BIN_DIR=`pwd`
  CHECKER_HOME=`dirname "$ABSOLUTE_BIN_DIR"`
elif [ ! -d "$CHECKER_HOME" ] ; then
  echo "Directory $CHECKER_HOME does not exist"
  exit 1
fi

cd "$CHECKER_HOME"

# Build checker classpath
CHECKER_CLASSPATH='conf'
for i in `ls lib/*.jar 2>/dev/null`
do
  if [ -f $i ]; then
    CHECKER_CLASSPATH=${CHECKER_CLASSPATH}:${i}
  fi
done

# Run checker
$JAVACMD -classpath "${CHECKER_CLASSPATH}" com.xebialabs.deployit.overthere.ConnectionChecker "$@"
