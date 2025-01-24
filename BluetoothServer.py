# Import necessary modules
import time
from machine import Pin 
import bluetooth
from ble_simple_peripheral import BLESimplePeripheral

time.sleep(1)

# Create a Bluetooth Low Energy (BLE) object
ble = bluetooth.BLE()

# Create an instance of the BLESimplePeripheral class with the BLE object
# sp = BLESimplePeripheral(ble)
# led signal so you know it works
led = Pin("LED", Pin.OUT)
led.on()

# Create a sensor object for the sensors, configure it as an input
sensor1in = Pin(6, Pin.IN)
sensor1out = Pin(7, Pin.OUT)
sensor2in = Pin(11, Pin.IN)
sensor2out = Pin(12, Pin.OUT)
sensor3in = Pin(18, Pin.IN)
sensor3out = Pin(19, Pin.OUT)
sensor4in = Pin(21, Pin.IN)
sensor4out = Pin(22, Pin.OUT)

sensor1in.low()
sensor1out.high()
sensor2in.low()
sensor2out.high()
sensor3in.low()
sensor3out.high()
sensor4in.low()
sensor4out.high()

activatedSensors = 0

def run():
    global activatedSensors
    activatedSensors = sensor1in.value() + sensor3in.value() * 10 + sensor4in.value() * 100 + sensor2in.value() * 1000
    return activatedSensors

# Define a callback function to handle received data
def on_rx(data):
#     print("Data received: ", data)  # Print the received data
    if data == b'is plate':  # Check the received data
        sp.send("yes")

# Start an infinite loop
while True:
    print(sensor1in.value(), " ", sensor2in.value(), " ", sensor3in.value(), " ", sensor4in.value())
    time.sleep(0.1)
#     if sp.is_connected():  # Check if a BLE connection is established
#         sp.on_write(on_rx)  # Set the callback function for data reception
#         data = run()
#         sp.send(str(data))  #sends data
# #         print(data)
#         time.sleep(0.01)

