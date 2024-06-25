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

UDP_port = 12000
TCP_port = 5000

TCP_socket = None
UDP_socket = None

def connect(ssid, password):
    wlan = network.WLAN(network.AP_IF)
    #security=0 means no password, can be removed
    wlan.config(essid=ssid, password=password, security=0)
    wlan.active(True)
    ip = wlan.ifconfig()[0]
    print(f'Connected on {ip}')
    return ip

def connect_UDP():
    global UDP_socket
    UDP_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    print ("created UDP socket")
    UDP_socket.settimeout(3)
    addr = socket.getaddrinfo('0.0.0.0', UDP_port)[0][-1]
    UDP_socket.bind(addr)
    print ("UDP socket binded to %s" %(UDP_port))
    
def connect_TCP():
    global TCP_socket
    TCP_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    print ("created TCP socket")
    addr = socket.getaddrinfo('0.0.0.0', TCP_port)[0][-1]
    TCP_socket.bind(addr)
    print ("TCP socket binded to %s" %(TCP_port))
    TCP_socket.listen(5)

#create the network first parameter is name and second is password
connect('not a virus', '0')

while True: 
    # Establish connection with client.
    connect_TCP()
    c, TCP_addr = TCP_socket.accept()
    print ('Got TCP connection from', TCP_addr )

    try:
        connect_UDP()
        m, UDP_addr = UDP_socket.recvfrom(1024)
        print ('Got TCP connection from', UDP_addr )
        while True:
            c.send(str(run()))
            UDP_socket.sendto(str(counter), UDP_addr)
            try:
                data = int(UDP_socket.recv(1024))
                if data == 1:
                    reset_score()
            except:
                print("fail")
            time.sleep(0.1)
    except:
        c.close()
        TCP_socket.close()
        UDP_socket.close()
             
    finally:
        c.close()
        TCP_socket.close()
        UDP_socket.close()
#         machine.reset()

machine.reset()
