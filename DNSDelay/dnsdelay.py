# -*- coding: utf-8 -*-
import dpkt
import socket
import sys

def printXML(data):
	fd.write("<delay>"+str(data)+"</delay>\n")

def printToScreen(name):
	filed = open(filename+".xml")
	lines = filed.readlines()
	for line in lines:
		print line
	
if len(sys.argv) != 2:
	print '\nUsage: python dnsdelay.py <Wireshark file>\n'

f = open(sys.argv[1])
filename=str(sys.argv[1]).split('.')[0]
pcap = dpkt.pcap.Reader(f)
count = 0
data = [] # [DNS TRX ID,TIME STAMP,REQ/RES(0/1),SRC IP,DST IP,SRC PORT,DST PORT]
fd = open(filename+".xml","w+")
fd.write("<?xml version=\"1.0\"?>\n")
fd.write("<!--description>Web surfing at home with "+filename+" </description -->\n")  #xml should have only one root this gives error
fd.write("<report>\n")
for ts,buf in pcap:
	count=count+1
print count	
for ts, buf in pcap:
    try:
        eth = dpkt.ethernet.Ethernet(buf)
        ip = eth.data
        udp = ip.data

    except:
        continue
    if eth.type != 2048 or ip.p != 17 or (udp.sport != 53 and udp.dport != 53):
        continue
    try:

     	current = []
        dns = dpkt.dns.DNS(udp.data)
	#print hex(dns.id),dns.qr,ts
	#print udp.sport,udp.dport
        #print socket.inet_ntoa(ip.src)		#To print source
        #print socket.inet_ntoa(ip.dst)     #To print destination
        #print  "DNS : %d query/response(0/1):%d TimeStamp : %f" % (dns.id,dns.qr,ts)   
	for qname in dns.qd:	
		current = current+[dns.id,ts,dns.qr,ip.src,ip.dst,udp.sport,udp.dport,qname.name]
        if(len(data)==0):
        	data = [current]
        else:
        	data = data+[current]	

    except:
        continue

#print data
val =0
n = len(data)
index = len(data[0])
for i in range(n):
	for j in range(n):
		if(data[i][0]==data[j][0] and data[i][2]==0 and data[j][2]==1 and 
			data[i][3]==data[j][4] and data[i][4]==data[j][3] and data[i][6]==data[j][5] and data[i][7]==data[j][7]):
			printXML(str(abs(data[j][1]-data[i][1])*1000)) #convert seconds into milliseconds
 			val = val + 1
			break;
fd.write("</report>\n")	
fd.close()

printToScreen(filename+".xml")			
print "Num of Request Response pairs : %d" %  val



			
