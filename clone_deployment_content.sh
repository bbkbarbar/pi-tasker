# !/bin/bash

mkdir piTaskerDeployment
cd piTaskerDeployment
git init
git remote add -f origin https://github.com/bbkbarbar/pi-tasker.git

echo "onTarget/" >> .git/info/sparse-checkout
git pull origin master

mv onTarget/* ../
rm -r onTarget
cd ..
rm -rf piTaskerDeployment