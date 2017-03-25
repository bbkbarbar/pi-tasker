# Control center in living room
Based on Raspberry Pi Zero

#### Clients can get infos from:
###### - Environment:
 	- Air temperature
 	- Aquarium-water temperature
 	- Relative humidity (later)
###### - Internal status:
 	- Worker info (contains toDo items)
 	- Pwm output stated
 	- IO output states
###### - System infos
 	- CPU temp
 	- Memory info (later if needed)
 	- Free space (later if needed)
 	- Network logs (# of connections, etc.) (later if needed)

#### Temperature warnings can be sent via email for multiple recipient in case of:
  - temperature (in selected sensor) rises above specified limit
  - temperature (in selected sensor) drops below specified limit

Temperature warning rules described in [JSON file](https://github.com/bbkbarbar/pi-tasker/blob/master/onTarget/home_-_pi/taskerData/TempWarnings.json).


#### Client can control:
| Item           | Control type               | Scheduled | Sensor based control | Exceptional control | Src                      |
| :------------- | :------------------------: | :-------: | :------------------: | :-----------------: | :----------------------: |
| Airpump        | On/Off                     | ?         | -                    | X                   | 230VAC                   |
| Filter         | On/Off                     | ?         | -                    | X                   | 230VAC                   |
| Water heater   | On/Off                     | -         | X                    | X                   | 230VAC                   |
| Water cooler   | Gradual speed control (pwm)| -         | X                    | X                   | 230VAC->12VDC - 1ch pwm  |
| Light          | On/Off, 3ch RGB color      | TBD       | -                    | X                   | 230VAC->12VDC - 3ch pwm  |
| Addtitional PWM outputs (12 channel) | 12 bit PWM output | possible | possible | X                   | 230VAC->12VDC - pwm      |


#### Other outputs:
 - WebUI (show temperature infos and status infos of controller peripherials)
 - Temperature logs
 - System logs


## Used external tool(s):
 - Install Apache Webserver:
 ```
 sudo apt-get install apache2 -y
 ```

## Deployment
 - Copy content of onTarget
 - Run deploy.sh
 - run first with run.sh with name of copied release as parameter (./run.sh t102.jar)

## ToDo list:
 - [ ] Cleanup files for easy deployment
 - [ ] Create "deploy script"

[Back to top](#top)
