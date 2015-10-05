package Sniffer;  
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import FileSearch.FileFrameListener;
import Frame.SnifferFrame;
import jpcap.*; 
import jpcap.packet.*;

public class Sniffer implements PacketReceiver, Runnable
{ 
	public SnifferFrame sf;
	public String s ;
	public FileFrameListener fl;
	
	
	public Sniffer(FileFrameListener fl){
		this.fl = fl;
	}
	public Sniffer(){
		
	}
	
    public static void main(String[] args) throws IOException 
    {
    	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    	String s;
    	
    	System.out.println("usage: java Sniffer <select a number from the following>");
    	NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		for (int i = 0; i < devices.length; i++) {
			System.out.println(i+" :"+devices[i].name + "(" + devices[i].description+")");
			System.out.println("    data link:"+devices[i].datalink_name + "("
					+ devices[i].datalink_description+")");
			System.out.print("    MAC address:");
			for (byte b : devices[i].mac_address)
				System.out.print(Integer.toHexString(b&0xff) + ":");
			System.out.println();
			for (NetworkInterfaceAddress a : devices[i].addresses)
				System.out.println("    address:"+a.address + " " + a.subnet + " "
						+ a.broadcast);
		}
		s = stdin.readLine();
//		System.out.println("s"+s);
        try 
        {              
        	NetworkInterface device =JpcapCaptor.getDeviceList()[Integer.parseInt(s)];
    		JpcapCaptor captor=JpcapCaptor.openDevice(device,2000,false,5000);
    		captor.loopPacket(-1, (PacketReceiver) new Sniffer()); 
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        } 
    }
    
    public void init(){
    	
    	NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		for (int i = 0; i < devices.length; i++) {
			fl.onMessage(i+" :"+devices[i].name + "(" + devices[i].description+")");
			fl.onMessage("    data link:"+devices[i].datalink_name + "("
					+ devices[i].datalink_description+")");
			String str = "    MAC address:";
			for (byte b : devices[i].mac_address)
				str += (Integer.toHexString(b&0xff) + ":");
			fl.onMessage(str);
			for (NetworkInterfaceAddress a : devices[i].addresses)
				fl.onMessage("    address:"+a.address + " " + a.subnet + " "
						+ a.broadcast+"");
		}
    }
    
    public void run(){
    	System.out.println("run=====");
        try 
        {              
        	NetworkInterface device =JpcapCaptor.getDeviceList()[Integer.parseInt(s)];
    		JpcapCaptor captor=JpcapCaptor.openDevice(device,2000,false,5000);
    		captor.loopPacket(-1, (PacketReceiver) new Sniffer()); 
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        } 
    }

	public void receivePacket(Packet packet) {
		// TODO Auto-generated method stub
		
		System.out.println("Packet : " + packet.toString());
		if(packet.datalink!=null && packet.datalink instanceof EthernetPacket){
			EthernetPacket ethernet=(EthernetPacket)packet.datalink;
			System.out.println("Ethernet:");
			
			System.out.println("\t" + "Frame Typ   e : " + ethernet.frametype);
			System.out.println("\t" + "Source MAC : " + ethernet.getSourceAddress());
			System.out.println("\t" + "Destination MAC : " + ethernet.getSourceAddress());
			
		}
		
		if(packet instanceof IPPacket){
			IPPacket ipPacket = (IPPacket) packet;
			analyzeIP(ipPacket);
		}
		else if(packet instanceof ARPPacket){
			ARPPacket arpPacket = (ARPPacket) packet;
			analyzeARP(arpPacket);
		}
		System.out.println("");
	} 
	
	public void analyzeIP(IPPacket ip){
		System.out.println("Internet Protocol:");
		
		System.out.println("\t" + "Version : " + ip.version);
		System.out.println("\t" + "Total Length : " + ip.length);
		System.out.println("\t" + "Fragment Offset : " + ip.offset);
		System.out.println("\t" + "Time to Live : " + ip.hop_limit);
		System.out.println("\t" + "Source IP : " + ip.src_ip);
		System.out.println("\t" + "Source Host Name : " + ip.src_ip.getHostName());
		System.out.println("\t" + "Destination IP : " + ip.dst_ip);
		System.out.println("\t" + "Destination Host Name : " + ip.dst_ip.getHostName());
		
		if(ip instanceof TCPPacket){
			TCPPacket tcpPacket = (TCPPacket) ip;
			analyzeTCP(tcpPacket);
		}
		else if(ip instanceof UDPPacket){
			UDPPacket udpPacket = (UDPPacket) ip;
			analyzeUDP(udpPacket);
		}
		else if(ip instanceof ICMPPacket){
			ICMPPacket icmpPacket = (ICMPPacket) ip;
			analyzeICMP(icmpPacket);
		}
	}
	
	public void analyzeARP(ARPPacket arp){
		System.out.println("Address Resolution Protocol:");
		
		System.out.println("\t" + "Hardware Type : " + arp.hardtype);
		System.out.println("\t" + "Protocol Type : " + arp.prototype);
		System.out.println("\t" + "Hardware Size : " + arp.hlen);
		System.out.println("\t" + "Protocol Type : " + arp.plen);
		switch(arp.operation){
		case ARPPacket.ARP_REQUEST: System.out.println("\t" + "Operation : " + "ARP Request");break;
		case ARPPacket.ARP_REPLY: System.out.println("\t" + "Operation : " + "ARP Reply");break;
		case ARPPacket.RARP_REQUEST: System.out.println("\t" + "Operation : " +"Reverse ARP Request");break;
		case ARPPacket.RARP_REPLY: System.out.println("\t" + "Operation : " +"Reverse ARP Reply");break;
		case ARPPacket.INV_REQUEST: System.out.println("\t" + "Operation : " +"Identify peer Request");break;
		case ARPPacket.INV_REPLY: System.out.println("\t" + "Operation : " +"Identify peer Reply");break;
		default: System.out.println("\t" + arp.operation);break;
		}
		System.out.println("\t" + "Sender MAC Address : " + arp.getSenderHardwareAddress());
		System.out.println("\t" + "Sender IP Address : " + arp.getSenderProtocolAddress());
		System.out.println("\t" + "Target MAC Address : " + arp.getTargetHardwareAddress());
		System.out.println("\t" + "Target IP Address : " + arp.getTargetProtocolAddress());
		
	}
	
	public void analyzeTCP(TCPPacket tcp){
		System.out.println("Transmission Control Protocol:");
		
		System.out.println("\t" + "Source Port : " + tcp.src_port);
		System.out.println("\t" + "Destination Port : " + tcp.dst_port);
		System.out.println("\t" + "Sequence Number : " + tcp.sequence);
		System.out.println("\t" + "Ack Number : " + tcp.ack_num);
		System.out.println("\t" + "URG Flag : " + tcp.urg);
		System.out.println("\t" + "ACK Flag : " + tcp.ack);
		System.out.println("\t" + "PSH Flag : " + tcp.psh);
		System.out.println("\t" + "RST Flag : " + tcp.rst);
		System.out.println("\t" + "SYN Flag : " + tcp.syn);
		System.out.println("\t" + "FIN Flag : " + tcp.fin);
		System.out.println("\t" + "Window Size : " + tcp.window);
		
		if(tcp.src_port==20 || tcp.dst_port==20 ||tcp.src_port==21 || tcp.dst_port==21){
			System.out.println("\t" + "APPLICATION : " + "FTP");
		}else if(tcp.src_port==22 || tcp.dst_port==22){
			System.out.println("\t" + "APPLICATION : " + "SSH");
		}else if(tcp.src_port==23 || tcp.dst_port==23){
			System.out.println("\t" + "APPLICATION : " + "TELNET");
		}else if(tcp.src_port==25 || tcp.dst_port==25){
			System.out.println("\t" + "APPLICATION : " + "SMTP");
		}else if(tcp.src_port==80 || tcp.dst_port==80){
			System.out.println("\t" + "APPLICATION : " + "HTTP");
		}else if(tcp.src_port==110 || tcp.dst_port==110){
			System.out.println("\t" + "APPLICATION : " + "POP3");
		}
	}
	
	public void analyzeUDP(UDPPacket udp){
		System.out.println("User Datagram Protocol:");
		
		System.out.println("\t" + "Source Port : " + udp.src_port);
		System.out.println("\t" + "Destination Port : " + udp.dst_port);
		System.out.println("\t" + "Length : " + udp.length);
	}
	
	public void analyzeICMP(ICMPPacket icmp){
		
		final String[] typeNames={
			"Echo Reply(0)",
			"Unknown(1)",
			"Unknown(2)",
			"Destination Unreachable(3)",
			"Source Quench(4)",
			"Redirect(5)",
			"Unknown(6)",
			"Unknown(7)",
			"Echo(8)",
			"Unknown(9)",
			"Unknown(10)",
			"Time Exceeded(11)",
			"Parameter Problem(12)",
			"Timestamp(13)",
			"Timestamp Reply(14)",
			"Unknown(15)",
			"Unknown(16)",
			"Address Mask Request(17)",
			"Address Mask Reply(18)"
		};
		
		System.out.println("Internet Control Message Protocol:");
		
		if (icmp.type >= typeNames.length) {
			System.out.println("\t" + "Type : " + String.valueOf(icmp.type));
		} else {
			System.out.println("\t" + "Type : " + typeNames[icmp.type]);
		}
		System.out.println("\t" + "Code : " + new Integer(icmp.code));
		
		if(icmp.type==0 || icmp.type==8 || (icmp.type>=13 && icmp.type<=18)){
			System.out.println("\t" + "ID : " + new Integer(icmp.id));
			System.out.println("\t" + "Sequence : " + icmp.seq);
		}
		
		if(icmp.type==5)
			System.out.println("\t" + "Redirect Address : " + icmp.redir_ip);
		
		if(icmp.type==17 || icmp.type==18)
			System.out.println("\t" + "Address Mask : " + (icmp.subnetmask>>12)+"."+
			                         ((icmp.subnetmask>>8)&0xff)+"."+
			                         ((icmp.subnetmask>>4)&0xff)+"."+
			                         (icmp.subnetmask&0xff)+".");
		
		if(icmp.type==13 || icmp.type==14){
			System.out.println("\t" + "Original Timestamp : " + icmp.orig_timestamp);
			System.out.println("\t" + "Receive Timestamp : " + icmp.recv_timestamp);
			System.out.println("\t" + "Transmission Timestamp : " + icmp.trans_timestamp);
		}
	}
} 
