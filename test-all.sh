#! /bin/bash
# ---------------------------------------------------------------
# Runs `ant test` on all the palettes in one batch
# ---------------------------------------------------------------
pushd () {
    command pushd "$@" > /dev/null
}
popd () {
    command popd "$@" > /dev/null
}
echo "This script will build all the palettes in this folder."
for dir in */
do
 if [[ $dir == "lib/" ]]; then continue; fi
 echo Testing $dir...
 pushd $dir
 ant test > /dev/null
 #if [ $? -ne 0 ]; then echo "Error testing"; fi
 popd
done