import socket
import network
import time
import machine
from machine import Pin


# pin10 = Pin(10, Pin.IN)
# pin11 = Pin(11, Pin.IN)
# pin12 = Pin(12, Pin.IN)
# pin13 = Pin(13, Pin.IN)

pin0 = Pin(0, Pin.IN)

counter = 0

def score():
    global counter
    counter += 1

def reset_score():
    global counter
    counter = 0

def run():
    p10 = pin10.value()
    p11 = pin11.value()
    p12 = pin12.value()
    p13 = pin13.value()
    if p10 == 1 or p11 == 1 or p12 == 1 or p13 == 1:
        score()
    return p10 + p11 + p12 + p13

def webpage():
    #Template HTML
    html = f"""
            <!DOCTYPE html>
            <html>
            <head>
            <title>EverPlate Webapp</title>
            </head>
            <body>
            <h1>""" + str(counter) + f"""</h1>
            </body>
            </html>
            """
    return str(html)
    
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
print ("created socket")


port = 12000

#if connecting to local wifi
ssid = 'Oded'
password = 'Oded2021$'

def connect(ssid, password):
    wlan = network.WLAN(network.AP_IF)
    #security=0 means no password, can be removed
    wlan.config(essid=ssid, password=password, security=0)
    wlan.active(True)
#     wlan.connect(ssid, password)
#     while wlan.isconnected() == False:
#         print('Waiting for connection...')
#         time.sleep(1)
    ip = wlan.ifconfig()[0]
    print(f'Connected on {ip}')
    addr = socket.getaddrinfo('0.0.0.0', port)[0][-1]
    s.bind(addr)
    print ("socket binded to %s" %(port)) 
    return ip

#create the network first parameter is name and second is password
connect('not a virus', '0')



# turn on the socket's listening mode 
# s.listen(1)
# print ("socket is listening")

while True: 
    # Establish connection with client. 
#     c, addr = s.accept()
#     print ('Got connection from', addr )

    try:
        m, addr = s.recvfrom(1024)
        while True:
            s.sendto(str(pin0.value()), addr)
            time.sleep(0.2)
#         while True:
#             c, addr = s.accept()
#             print ('Got connection from', addr )
#             request = c.recv(1024)
#             c.send(str(pin0.value()).encode())
#             print(pin0.value())
#             c.close()
#             time.sleep(0.3)
             
    finally:
        c.close()
        machine.reset()
        print("test")
        break
