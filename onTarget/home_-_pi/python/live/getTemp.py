# Temp.py

from ds18b20 import DS18B20   
import time
import argparse
import os
import datetime

def write_to_file(data):
	homePath = os.environ["HOME"]
	with open(homePath + "/logs/read_temp.log", "a") as myfile:
		myfile.write(data + "\r\n")
		myfile.close()


parser = argparse.ArgumentParser('Temperature reader')
parser.add_argument("-s","--sensorChannel", type=int, help="temperature sensor channel", default = 0)
args = parser.parse_args()
sensor = args.sensorChannel

# test temperature sensors
x = DS18B20()
count=x.device_count()

if sensor == -1:
	strResult = str(count) + " "
	i = 0
	while i < count:
	   strResult += ('%.3f' % x.tempC(i))
	   i += 1
	   if i < count:
	   		strResult += " "
	print(strResult)
	lasttime = str(datetime.datetime.time(datetime.datetime.now()))[:8]
	write_to_file(lasttime + "\t" + strResult)
else:
	if sensor < count:
		print('%.3f' % x.tempC(sensor))		
		lasttime = str(datetime.datetime.time(datetime.datetime.now()))[:8]
		write_to_file(lasttime + "\t" + '%.3f' % x.tempC(sensor))


