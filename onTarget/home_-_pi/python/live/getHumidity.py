import RPi.GPIO as GPIO
import dht11
import datetime
import argparse

parser = argparse.ArgumentParser('GPIO output controller')
parser.add_argument("-m","--mock", type=int, help="Value to mockReturn", default = -1)

args = parser.parse_args()
mockValue = args.mock

#print "Humidity: " + str(mockValue)


# initialize GPIO
GPIO.setwarnings(False)
#GPIO.setmode(GPIO.BCM)
GPIO.setmode(GPIO.BOARD)
GPIO.cleanup()

instance = dht11.DHT11(pin=26)
result = instance.read()
if result.is_valid():
	print("Humidity: " + str(result.humidity))