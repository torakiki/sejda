#!/bin/sh -e
#
# Sejda project release howto/script
#
# Usage: ./release.sh | tee release.log

# gather all the info needed to release

BASEDIR=`dirname $0`/..
BASEDIR=`(cd "$BASEDIR"; pwd)`

if [ -z "$REPO" ]
then
  REPO="$BASEDIR"
fi

# EDIT HERE THE VERSIONS BEFORE RUNNING THE SCRIPT
RELEASE_VERSION="1.0.0.BETA2"
NEXT_DEV_VERSION="1.0.0-RC1-SNAPSHOT"

echo "Enter your gpg passphrase:"
read YOUR_GPG_PASSPHRASE
echo "\nYour workspace repository path:  $REPO"
echo "Releasing version: $RELEASE_VERSION"
echo "Development version: $NEXT_DEV_VERSION. (Make sure it ends in -SNAPSHOT)"
echo "(These versions are editable inside the script, before you run it)"
echo "\nReview the above, stop with CTRL+C or press any key to continue..."
read bump

# ok, here it goes... 

# create sejda release staging repo
# all commits are performed on this repo, and if all went well, you push them to the local workspace repository

mkdir -p /tmp/sejda-release-staging
rm -fr /tmp/sejda-release-staging/*
cd /tmp/sejda-release-staging
hg clone $REPO

cd /tmp/sejda-release-staging/sejda

# maven-release-plugin black magic
# :prepare (do a dry-run first, no touching of the poms or repo - if this fails, there's nothing to rollback)
# :prepare (pom versions get changed and commited, release gets tagged commited. no push is executed yet)
# :perform (checks out from the release tag, builds and signs the artifacts, deploys to Maven Central staging repository)

mvn release:prepare -DdevelopmentVersion=$NEXT_DEV_VERSION -DreleaseVersion=$RELEASE_VERSION --batch-mode -DdryRun=true -Darguments=-Dgpg.passphrase=$YOUR_GPG_PASSPHRASE

mvn release:clean

mvn release:prepare -DdevelopmentVersion=$NEXT_DEV_VERSION -DreleaseVersion=$RELEASE_VERSION --batch-mode -Darguments=-Dgpg.passphrase=$YOUR_GPG_PASSPHRASE

mvn release:perform -Darguments="-Dgpg.passphrase=$YOUR_GPG_PASSPHRASE -Psonatype-oss-release"

echo "\nRelease is done. Next steps are:
 1) log in to OSS Nexus and close the staging repository. Log in at https://oss.sonatype.org/ with username sejda, click "Staging Repositories". Select the last release performed (should be Open) and perform 'Close'. You expect no validation error and a successful close. After that, you can do 'Release', BEWARE this is a point of no return for Maven Central binaries.

 2) push changes from the /tmp/sejda-release-staging/sejda to your workspace repository, and then from there upstream to bitbucket.
 3) upload released binaries to bitbucket download page:
 3a) - console binaries: /tmp/sejda-release-staging/sejda/target/checkout/sejda-console/target/sejda-console-*-bin.zip
 3b) - zip containing all jars for the rest of the modules: /tmp/sejda-release-staging/sejda/target/checkout/sejda-distribution/target/sejda-distribution-*-bin.zip
 3c) - upload javadocs to website "

