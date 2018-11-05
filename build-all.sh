#! /bin/bash
# ---------------------------------------------------------------
# Builds all the palettes in one batch
# ---------------------------------------------------------------
pushd () {
    command pushd "$@" > /dev/null
}
popd () {
    command popd "$@" > /dev/null
}
echo "This script will build all the palettes in this folder."
mkdir -p lib
mkdir -p doc
for dir in */
do
 if [[ $dir == "lib/" ]]; then continue; fi
 if [[ $dir == "doc/" ]]; then continue; fi
 echo Building $dir...
 pushd $dir
 ant -q download-deps #> /dev/null
 if [ $? -ne 0 ]; then echo "Error downloading dependencies"; fi
 ant -q #> /dev/null
 if [ $? -ne 0 ]; then echo "Error building palette"; fi
 popd
done