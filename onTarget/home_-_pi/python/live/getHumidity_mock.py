import datetime
import argparse


#print "Pin: " + str(pin) + " State: " + str(state)

def main():
	parser = argparse.ArgumentParser('GPIO output controller')
	parser.add_argument("-p","--pin", type=int, help="Input pin", default = 7)
	parser.add_argument("-m","--mock", type=int, help="Value to mockReturn", default = -1)

	args = parser.parse_args()
	pin = args.pin

	mockValue = args.mock

	print "Humidity: " + str(mockValue)

if __name__ == "__main__":
	main()
