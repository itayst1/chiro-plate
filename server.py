import socket
import network
import time
import machine
from machine import Pin


pin18 = Pin(18, Pin.IN)
pin19 = Pin(19, Pin.IN)
pin20 = Pin(20, Pin.IN)
pin21 = Pin(21, Pin.IN)

# pin0 = Pin(0, Pin.IN)

counter = 0
scored = False

def score():
    global counter
    counter += 1

def reset_score():
    global counter
    counter = 0

def run():
    global scored
    p18 = pin18.value()
    p19 = pin19.value()
    p20 = pin20.value()
    p21 = pin21.value()
    pins = p18 + p19 + p20 + p21
    if pins > 0:
        if scored == False:
            score()
            scored = True
    else:
        scored = False
    return pins

    
UDP_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
print ("created UDP socket")
TCP_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print ("created TCP socket")

UDP_port = 12000
TCP_port = 5000

def connect(ssid, password):
    wlan = network.WLAN(network.AP_IF)
    #security=0 means no password, can be removed
    wlan.config(essid=ssid, password=password, security=0)
    wlan.active(True)
    ip = wlan.ifconfig()[0]
    print(f'Connected on {ip}')
    return ip

def connect_UDP():
    addr = socket.getaddrinfo('0.0.0.0', UDP_port)[0][-1]
    UDP_socket.bind(addr)
    print ("UDP socket binded to %s" %(UDP_port))
    
def connect_TCP():
    addr = socket.getaddrinfo('0.0.0.0', TCP_port)[0][-1]
    TCP_socket.bind(addr)
    print ("TCP socket binded to %s" %(TCP_port))
    TCP_socket.listen(5)

#create the network first parameter is name and second is password
connect('not a virus', '0')
connect_TCP()
connect_UDP()



# turn on the socket's listening mode
# s.listen(1)
# print ("socket is listening")

while True: 
    # Establish connection with client. 
    c, addr = TCP_socket.accept()
    print ('Got TCP connection from', addr )

    try:
        m, addr = UDP_socket.recvfrom(1024)
        while True:
            c.send("4")
            print(run())
            UDP_socket.sendto(str(counter), addr)
            time.sleep(0.1)
             
    finally:
        c.close()
#         machine.reset()

break

machine.reset()
