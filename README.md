# LEDController

A program to control some thing on the raspberry pi via a web api. Current features: 
 - RGB-Strip Control
 - Color modes
 - GPIO Control
 - Stats collector

## Endpoints

All paths have to start with `http://<ip>:<port>/control`

Parameters are URL query parameters. Bodies are plain-text POST bodies.

| HTTP-Method | Relative path | Description | Parameter (if any) | Body (if any) |
| ----------- | ------------- | ----------- | ------------------ | ------------- |
| GET | /version | Returns the current version | | `X.X.X` |
| GET | /lighting | Returns the current color | | `#RRGGBB` |
| POST | /lighting | Sets the color| `color` The new color ; `time`  Time to use for color transition  _(Optional)_ |  |
| GET | /lighting/default | Returns the default (startup) color | | `#RRGGBB` |
| POST | /lighting/default | Sets the default (startup) color | `color` The new default color | |
| POST | /lighting/reset | Resets the color to the default (startup) color | `time`  Time to use for color transition  _(Optional)_ |
| GET | /lighting/mode | Returns the current mode | | |
| POST | /lighting/mode | Sets the mode | `mode` The name of the mode | |
| POST | /lighting/mode/start | Starts the current mode | `multiplier` Value the multiply the time _(Optional)_ | |
| POST | /lighting/mode/stop | Stops the current mode | | |
| GET | /lighting/mode/list | Lists all modes  | | |
| POST | /gpio/write | Sets a GPIO to low or high | `pin` Pin id to write (BCM ids), `state` Either `high` _(On)_ or `low` _(Off)_ | |
| POST | /stats/delete | Removes a statistic from tracking | `name` Name of the stat to delete | |
| GET | /stats/get/latest | Returns the latest value from the stat | `name` Name of the stat | |
| GET | /stats/get/all | Returns all values from the stat | `name` Name of the stat, `max` Max values to return _(Optional)_ | |
| POST | /stats/update | Update a stat with a new value. If the stats does not exist it will be created | `name` Name of the stat | `Value` |
| GET | /stats/list | Lists all stats | | |