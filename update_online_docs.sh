#!/bin/env bash
set -ev

COMMIT_HASH="$(git log --pretty=format:%H -n 1)"
if [ -z "${COMMIT_HASH}" ]; then
  echo "Could not get current commit hash from git?"
  exit 1
fi

# fetch gh-pages
rm -rf gh-pages
REMOTE=$(git remote get-url origin)
git clone --depth 1 -b gh-pages "$REMOTE" gh-pages
pushd gh-pages
# Remove all existing files - dokka will rebuild!
git rm -r .
popd

# Build documentation
./gradlew :dokkaGeneratePublicationHtml
cp -R build/dokka/html/* gh-pages/

# Commit the change
pushd gh-pages
git add .
# This step will fail if there is no data change
git commit -m "Update documentation to ${COMMIT_HASH}"

# Push it up.
git push
popd
