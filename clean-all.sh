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
echo "This script will clean all the palettes in this folder."
retcode=0
for dir in */
do
 if [[ $dir == "lib/" ]]; then continue; fi
 if [[ $dir == "doc/" ]]; then continue; fi
 echo Testing $dir...
 pushd $dir
 ant clean > /dev/null
 #if [ $? -ne 0 ]; then echo "Error cleaning"; fi
 let "retcode=retcode+$?"
 popd
done
exit $retcode
