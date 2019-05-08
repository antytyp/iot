#-*-coding:utf8-*-

from time import mktime, sleep
from datetime import datetime
import RPi.GPIO as GPIO
import dht11
import pyowm
import sys
import mosquitto
import _thread

def get_data():
    while True:
        result = instance.read()
        if result.is_valid():
            return result.temperature, result.humidity

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.cleanup()

instance = dht11.DHT11(pin=4)

# pyowm data
owm = pyowm.OWM('4526d487f12ef78b82b7a7d113faea64')
cities = ['Krakow,PL', 'Istanbul,TR', 'Stockholm,SE', 'London,GB', 'Lima,PE', 'Tofte,US', 'Tynda,RU']

# mqtt
def on_connect(mqttc, obj, rc):
    print("rc: "+str(rc))

def on_subscribe(mqttc, obj, mid, granted_qos):
    print("Subscribed: "+str(mid)+" "+str(granted_qos))

def on_log(mqttc, obj, level, string):
    print(string)
    
def mqtt_thread_func(mqttc):
    mqttc.loop_forever()

mqttc = mosquitto.Mosquitto() 
mqttc.on_connect = on_connect
mqttc.on_subscribe = on_subscribe
# mqttc.on_log = on_log
broker="iot.eclipse.org"
port = 1883
mqttc.connect(broker, port)

topic = "iot-weather"

try:
    thread.start_new_thread(mqtt_thread_func, (mqttc, ))
except:
    pass

print("Client started")

while True:
    try:
        message = []
        
        # data from sensor
        temp, humidity = get_data()
        data = {
            "localisation": 'KrakowRPI,PL',
            "temp": temp,
            "humidity": humidity,
            "type": "rpi",
        }
        
        message.append(data)
        
        for city in cities:
            observation = owm.weather_at_place(city)
            w = observation.get_weather()
            temp = w.get_temperature('celsius')['temp']
            humidity = w.get_humidity()
            data = {
                "localisation": city[:-3],
                "temp": temp,
                "humidity": humidity,
                "type": "api",
            }
            message.append(data)
        print(message)
        mqttc.publish(topic, str(message), 0, True)
        sleep(0.25)
    except KeyboardInterrupt:
        print("Closing mqtt client")
        sys.exit()
