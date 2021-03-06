#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "Start tasker"

  else
  	echo "New build used from " + $1
  	sudo rm /home/pi/java-pi/tasker.jar
  	echo "Old build removed.."
  	sudo cp $1 /home/pi/java-pi/tasker.jar
  	echo "New build copied.."
fi

sudo nohup java -jar /home/pi/java-pi/tasker.jar &

echo "Started with nohup.."

sudo tail -f nohup.out -n 40
