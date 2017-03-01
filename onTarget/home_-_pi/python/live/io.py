import RPi.GPIO as GPIO ## Import GPIO library
import argparse

GPIO.setwarnings(False)

def msg(pin, state):
	print "Pin: " + str(pin) + " State: " + str(state)

def getBool(val):
	if val == 0:
		return False
	else:
		return True

def main():
	parser = argparse.ArgumentParser('GPIO output controller')
	parser.add_argument("-s","--state", type=int, help="Wanted state of output")
	parser.add_argument("-p","--pin", type=int, help="Output pin", default = 3)

	args = parser.parse_args()

	pin = args.pin
	state = args.state

	GPIO.setmode(GPIO.BOARD) ## Use board pin numbering
	msg(pin, state)

	GPIO.setup(pin, GPIO.OUT) ## Setup GPIO Pin 7 to OUT
	GPIO.output(pin,getBool(state)) ## Turn on GPIO pin 7

if __name__ == "__main__":
	main()