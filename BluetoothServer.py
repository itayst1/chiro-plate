# Import necessary modules
import time
from machine import Pin 
import bluetooth
from ble_simple_peripheral import BLESimplePeripheral

# Create a Bluetooth Low Energy (BLE) object
ble = bluetooth.BLE()

# Create an instance of the BLESimplePeripheral class with the BLE object
sp = BLESimplePeripheral(ble)

# Create a Pin object for the sensors, configure it as an input
pin1 = Pin(18, Pin.IN)
pin2 = Pin(19, Pin.IN)
pin3 = Pin(20, Pin.IN)
pin4 = Pin(21, Pin.IN)

activatedSensorIdx = 0

def run():
    global activatedSensorIdx
    if pin1.value() == 1:
        activatedSensorIdx = 1
    elif pin2.value() == 1:
        activatedSensorIdx = 2
    elif pin3.value() == 1:
        activatedSensorIdx = 3
    elif pin4.value() == 1:
        activatedSensorIdx = 4
    else:
        activatedSensorIdx = 0
    return activatedSensorIdx

# Define a callback function to handle received data
def on_rx(data):
    print("Data received: ", data)  # Print the received data
    if data == b'is plate':  # Check the received data
        sp.send("yes")

# Start an infinite loop
while True:
    if sp.is_connected():  # Check if a BLE connection is established
        sp.on_write(on_rx)  # Set the callback function for data reception
        data = run()
        sp.send(str(data))  #sends data
#         print(data)
        time.sleep(0.01)