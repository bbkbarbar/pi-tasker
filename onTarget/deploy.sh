#!/bin/bash

sudo apt-get install apache2 -y

sudo cp -a ./home_-_pi/. .
sudo chmod 755 cl.sh
sudo chmod 755 empty
sudo cp empty nohup.out
sudo chmod 755 run.sh
sudo chmod 755 stop_tasker.sh
sudo chmod 775 nohup.out
sudo cp -a ./var_-_www_-_html/. /var/www/html/

sudo rm -r ./home_-_pi
sudo rm -r ./var_-_www_-_html

echo "Deploy successfuly done.."
