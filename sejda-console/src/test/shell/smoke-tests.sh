#!/bin/bash -e
#
# Sejda console smoke tests shell script.
#
# Usage: maven clean install && ./src/test/shell/smoke-tests.sh

BASEDIR=`dirname $0`/../../..
BASEDIR=`(cd "$BASEDIR"; pwd)`

echo "Adding execute permission to the console binaries"
chmod +x $BASEDIR/target/assembled/bin/sejda-console

echo "Creating /tmp/sejda-smoketest folder if it doesnt exist"
mkdir -p /tmp/sejda-smoketest

echo "Removing previous files from /tmp/sejda-smoketest"
rm -fr /tmp/sejda-smoketest/*

# cygwin on windows
if [ -d "/cygdrive/c/tmp/sejda-smoketest/" ]; then
    echo "Removing previous files from /cygdrive/c/tmp/sejda-smoketest"
	rm -fr /cygdrive/c/tmp/sejda-smoketest/*
fi

function run_tests {
  local exit_code=0

  while read cmd; do 
    # echo "RUNNING: $cmd"
    if eval "$cmd"; then
      # echo "PASSED: $cmd"
    else
      echo "FAILED: $cmd"
      return 1
    fi
  done < "$BASEDIR"/src/test/shell/commands.txt 

}

if run_tests; then
  echo "ALL SMOKE TESTS PASSED!"
  exit 0
else
  echo "FAILURE DETECTED, STOPPING..."
  exit 1
fi

