#!/bin/bash
if [ $# -eq 0 ]
  then
    echo "Start tasker"

  else
  	echo "New build used from " + $1
  	sudo rm /home/pi/java-pi/tasker.jar
  	sudo cp $1 /home/pi/java-pi/tasker.jar
fi

sudo nohup java -jar /home/pi/java-pi/tasker.jar &
