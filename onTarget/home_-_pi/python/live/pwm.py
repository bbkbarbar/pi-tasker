from __future__ import division
import time
import argparse
import os
import datetime
import serial

# Constants
output_channel_of_red   = 1     # 1
output_channel_of_green = 2 	# 0
output_channel_of_blue  = 0

def write_to_file(data):
	homePath = os.environ["HOME"]
	with open(homePath + "/logs/rgb_output.log", "a") as myfile:
		myfile.write(data + "\r\n")
		myfile.close()

# Uncomment to enable debug output.
#import logging
#logging.basicConfig(level=logging.DEBUG)

# Initialise the serial output.
ser = serial.Serial(
   port='/dev/ttyAMA0',
   baudrate = 9600,
   parity=serial.PARITY_NONE,
   stopbits=serial.STOPBITS_ONE,
   bytesize=serial.EIGHTBITS,
   timeout=1
)

def sendOutputCommandOnSerial(command):
	ser.write(command + "\n");

# Parse arguments
parser = argparse.ArgumentParser('RGB test')
parser.add_argument("-r","--red", type=int, help="red", default = -1)
parser.add_argument("-g","--green", type=int, help="green", default = -1)
parser.add_argument("-b","--blue", type=int, help="blue", default = -1)

parser.add_argument("-x", "--hex", type=str, help="RGB color in hex string", default = "  ")
parser.add_argument("-c", "--complete", type=str, help="all color component in one", default = "  ")

parser.add_argument("-ch3","--ch3", type=int, help="channel_3", default = -1)
parser.add_argument("-ch4","--ch4", type=int, help="channel_4", default = -1)
parser.add_argument("-ch5","--ch5", type=int, help="channel_5", default = -1)

parser.add_argument("-ch6","--ch6", type=int, help="channel_3", default = -1)
parser.add_argument("-ch7","--ch7", type=int, help="channel_4", default = -1)
parser.add_argument("-ch8","--ch8", type=int, help="channel_5", default = -1)

parser.add_argument("-ch9", "--ch9",  type=int, help="channel_9",  default = -1)
parser.add_argument("-ch10","--ch10", type=int, help="channel_10", default = -1)
parser.add_argument("-ch11","--ch11", type=int, help="channel_11", default = -1)

parser.add_argument("-ch12","--ch12", type=int, help="channel_12",  default = -1)
parser.add_argument("-ch13","--ch13", type=int, help="channel_13", default = -1)
parser.add_argument("-ch14","--ch14", type=int, help="channel_14", default = -1)
parser.add_argument("-ch15","--ch15", type=int, help="channel_15", default = -1)

args = parser.parse_args()

allComponent = args.complete

# Store values for each color-channel
if allComponent[0] == "a":
	red   = int(allComponent[1:4])
	print "red:   " + str(red)
	green = int(allComponent[5:8])
	print "green: " + str(green)
	blue  = int(allComponent[8:10])
	print "blue:  " + str(blue)
else:
	red   = args.red
	green = args.green
	blue  = args.blue

	ch3_val = args.ch3
	ch4_val = args.ch4
	ch5_val = args.ch5

	ch6_val  = args.ch6
	ch7_val  = args.ch7
	ch8_val  = args.ch8
	ch9_val  = args.ch9
	ch10_val = args.ch10
	ch11_val = args.ch11


# Set PWM outputs
if((red >= 0) or (green >=0) or (blue >=0)):
	sendOutputCommandOnSerial(str(output_channel_of_blue)  + " " + str(blue));
	sendOutputCommandOnSerial(str(output_channel_of_red)   + " " + str(red));
	sendOutputCommandOnSerial(str(output_channel_of_green) + " " + str(green));

if ch3_val >= 0:
	sendOutputCommandOnSerial("3 " + str(ch3_val));
if ch4_val >= 0:
	sendOutputCommandOnSerial("4 " + str(ch4_val));
if ch5_val >= 0:
	sendOutputCommandOnSerial("5 " + str(ch5_val));

if ch6_val  >= 0:
	sendOutputCommandOnSerial("6 " + str(ch6_val));
if ch7_val  >= 0:
	sendOutputCommandOnSerial("7 " + str(ch7_val));
if ch8_val  >= 0:
	sendOutputCommandOnSerial("8 " + str(ch8_val));
if ch9_val  >= 0:
	sendOutputCommandOnSerial("9 " + str(ch9_val));
if ch10_val >= 0:
	sendOutputCommandOnSerial("10 " + str(ch10_val));
if ch11_val >= 0:
	sendOutputCommandOnSerial("11 " + str(ch11_val));
#sendOutputCommandOnSerial("3 " + str(ch3_val));
#sendOutputCommandOnSerial("4 " + str(ch4_val));
#sendOutputCommandOnSerial("5 " + str(ch5_val));



# Write log
#lasttime = str(datetime.datetime.time(datetime.datetime.now()))[:8]
#write_to_file(lasttime + "\tR:" + str(red) + "\tG: " + str(green) + "\tB: " + str(blue) )
    