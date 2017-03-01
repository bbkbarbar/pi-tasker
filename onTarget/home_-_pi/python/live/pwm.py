from __future__ import division
import time
import argparse
import os
import datetime

# Constants
outout_channel_of_red   = 1
outout_channel_of_green = 0
outout_channel_of_blue  = 2

# Import the PCA9685 module.
import Adafruit_PCA9685

def write_to_file(data):
	homePath = os.environ["HOME"]
	with open(homePath + "/logs/rgb_output.log", "a") as myfile:
		myfile.write(data + "\r\n")
		myfile.close()

# Uncomment to enable debug output.
#import logging
#logging.basicConfig(level=logging.DEBUG)

# Initialise the PCA9685 using the default address (0x40).
pwm = Adafruit_PCA9685.PCA9685()

# Set frequency to 60hz, good for servos.
pwm.set_pwm_freq(600)

# Parse arguments
parser = argparse.ArgumentParser('RGB test')
parser.add_argument("-r","--red", type=int, help="red", default = 0)
parser.add_argument("-g","--green", type=int, help="green", default = 0)
parser.add_argument("-b","--blue", type=int, help="blue", default = 0)
parser.add_argument("-x", "--hex", type=str, help="RGB color in hex string", default = "  ")
parser.add_argument("-c", "--complete", type=str, help="all color component in one", default = "  ")

parser.add_argument("-ch3","--ch3", type=int, help="channel_3", default = -1)

parser.add_argument("-ch4","--ch4", type=int, help="channel_4", default = -1)
parser.add_argument("-ch5","--ch5", type=int, help="channel_5", default = -1)
parser.add_argument("-ch6","--ch6", type=int, help="channel_6", default = -1)
parser.add_argument("-ch7","--ch7", type=int, help="channel_7", default = -1)

parser.add_argument("-ch8","--ch8", type=int, help="channel_8", default = -1)
parser.add_argument("-ch9","--ch9", type=int, help="channel_9", default = -1)
parser.add_argument("-ch10","--ch10", type=int, help="channel_10", default = -1)
parser.add_argument("-ch11","--ch11", type=int, help="channel_11", default = -1)

parser.add_argument("-ch12","--ch12", type=int, help="channel_12", default = -1)
parser.add_argument("-ch13","--ch13", type=int, help="channel_13", default = -1)
parser.add_argument("-ch14","--ch14", type=int, help="channel_14", default = -1)
parser.add_argument("-ch15","--ch15", type=int, help="channel_15", default = -1)

args = parser.parse_args()

allComponent = args.complete

#hexStr = args.hex
#if hexStr[1] == "x":
#	print "got a hex color: " + hexStr
#	data = hexStr[2:]
#	print "Data: |" + data + "|"

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

	ch3_val = args.ch3

	ch4_val = args.ch4
	ch5_val = args.ch5
	ch6_val = args.ch6
	ch7_val = args.ch7

	ch8_val = args.ch8
	ch9_val = args.ch9
	ch10_val = args.ch10
	ch11_val = args.ch11

	ch12_val = args.ch12
	ch13_val = args.ch13
	ch14_val = args.ch14
	ch15_val = args.ch15

# Set PWM outputs
pwm.set_pwm(outout_channel_of_red,   0, red)
pwm.set_pwm(outout_channel_of_green, 0, green)
pwm.set_pwm(outout_channel_of_blue,  0, blue)
if ch8_val >= 0:
	pwm.set_pwm(8,  0, ch8_val)
	
if ch3_val >= 0:
	pwm.set_pwm(3,  0, ch3_val)
if ch4_val >= 0:
	pwm.set_pwm(4,  0, ch4_val)
if ch5_val >= 0:
	pwm.set_pwm(5,  0, ch5_val)
if ch6_val >= 0:
	pwm.set_pwm(6,  0, ch6_val)
if ch7_val >= 0:
	pwm.set_pwm(7,  0, ch7_val)

if ch8_val >= 0:
	pwm.set_pwm(8,  0, ch8_val)
if ch9_val >= 0:
	pwm.set_pwm(9,  0, ch9_val)
if ch10_val >= 0:
	pwm.set_pwm(10,  0, ch10_val)
if ch11_val >= 0:
	pwm.set_pwm(11,  0, ch11_val)

if ch12_val >= 0:
	pwm.set_pwm(12,  0, ch12_val)
if ch13_val >= 0:
	pwm.set_pwm(13,  0, ch13_val)
if ch14_val >= 0:
	pwm.set_pwm(14,  0, ch14_val)
if ch15_val >= 0:
	pwm.set_pwm(15,  0, ch15_val)

#pwm.set_pwm(channel, 0, value)

# Write log
lasttime = str(datetime.datetime.time(datetime.datetime.now()))[:8]
write_to_file(lasttime + "\tR:" + str(red) + "\tG: " + str(green) + "\tB: " + str(blue) )
    