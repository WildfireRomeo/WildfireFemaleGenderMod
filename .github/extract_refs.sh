#!/usr/bin/env bash

# Extract the branch name from $GITHUB_REF, replacing any slash characters with dashes
# https://github.com/CaffeineMC/sodium-fabric/blob/435a6bd7ecfa58499b64f1a73b61309070929d86/.github/workflows/build-commit.yml#L15
ref="${GITHUB_REF#refs/heads/}" && echo "branch=${ref////-}" >> $GITHUB_OUTPUT

# Extract the Minecraft version from gradle.properties
minecraft_version=$(grep 'minecraft_version=' gradle.properties --color=never) && echo $minecraft_version >> $GITHUB_OUTPUT

### Build version summary

fabric_loader=$(grep 'loader_version=' gradle.properties --color=never)
fabric_loader="${fabric_loader#loader_version=}"

fabric_api=$(grep 'fabric_version=' gradle.properties --color=never)
fabric_api="${fabric_api#fabric_version=}"
# Strip the trailing '+version' syntax
fabric_api="${fabric_api//+[0-9]*/}"

echo "## Version summary" >> $GITHUB_STEP_SUMMARY
echo "" >> $GITHUB_STEP_SUMMARY
echo "- Targeting Minecraft version ${minecraft_version#minecraft_version=}" >> $GITHUB_STEP_SUMMARY
echo "- Using Fabric loader $fabric_loader" >> $GITHUB_STEP_SUMMARY
echo "- Using Fabric API version $fabric_api" >> $GITHUB_STEP_SUMMARY
