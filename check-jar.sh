#! /bin/bash
WORK_DIR=$(mktemp -d)

# check if tmp dir was created
if [[ ! "$WORK_DIR" || ! -d "$WORK_DIR" ]]; then
  echo "Could not create temp dir"
  exit 1
fi

# deletes the temp directory
function cleanup {      
  rm -rf "$WORK_DIR"
  #echo "Deleted temp working directory $WORK_DIR"
}

# register the cleanup function to be called on the EXIT signal
trap cleanup EXIT

filename=$(basename "$1")
cp $1 $WORK_DIR
pushd $WORK_DIR
unzip $filename
list="$(find . -name '*.class' )"
for file in $list;
do
	echo $file
	javap -v $file | grep major
	#break
done
popd