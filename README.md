# Control center in living room
Based on Raspberry Pi Zero

####Clients can get infos from:
###### - Environment:
 	- Air temperature
 	- Aquarium-water temperature
 	- Relative humidity (later)
###### - System infos
 	- CPU temp
 	- Memory info
 	- Free space
 	- Network logs (# of connections, etc.)

####Client can control:
| Item           | Control type               | Scheduled | Sensor based control | Exceptional control | Src                      | 
| :------------- | :------------------------: | :-------: | :------------------: | :-----------------: | :----------------------: |
| Airpump        | On/Off                     | ?         | -                    | X                   | 230VAC                   |
| Filter         | On/Off                     | ?         | -                    | X                   | 230VAC                   |
| Water heater   | On/Off                     | -         | X                    | X                   | 230VAC                   |
| Water cooler   | Gradual speed control (pwm)| -         | X                    | X                   | 230VAC->12VDC - 1ch pwm  |
| Light          | On/Off, 3ch RGB color      | X         | -                    | X                   | 230VAC->12VDC - 3ch pwm  |

[Back to top](#top)
