#!/usr/bin/python

from mininet.net import Mininet
from mininet.node import Controller, RemoteController, OVSController
from mininet.node import CPULimitedHost, Host, Node
from mininet.node import OVSKernelSwitch, UserSwitch
from mininet.node import IVSSwitch
from mininet.cli import CLI
from mininet.log import setLogLevel, info
from mininet.link import TCLink, Intf
from subprocess import call
import time

def myNetwork():

    net = Mininet( topo=None,
                   build=False,
                   ipBase='10.0.0.0/8')

    info( '*** Adding controller\n' )
    c2=net.addController(name='c2',
                      controller=Controller,
                      protocol='tcp',
                      port=6635)

    c1=net.addController(name='c1',
                      controller=Controller,
                      protocol='tcp',
                      port=6634)

    c0=net.addController(name='c0',
                      controller=Controller,
                      protocol='tcp',
                      port=6633)

    info( '*** Add switches\n')
    s3 = net.addSwitch('s3', cls=OVSKernelSwitch)
    s6 = net.addSwitch('s6', cls=OVSKernelSwitch)
    s2 = net.addSwitch('s2', cls=OVSKernelSwitch)
    s8 = net.addSwitch('s8', cls=OVSKernelSwitch)
    s4 = net.addSwitch('s4', cls=OVSKernelSwitch)
    s5 = net.addSwitch('s5', cls=OVSKernelSwitch)
    s9 = net.addSwitch('s9', cls=OVSKernelSwitch)
    s7 = net.addSwitch('s7', cls=OVSKernelSwitch)
    s1 = net.addSwitch('s1', cls=OVSKernelSwitch)

    info( '*** Add hosts\n')
    h11 = net.addHost('h11', cls=Host, ip='10.0.0.11', defaultRoute=None)
    h4 = net.addHost('h4', cls=Host, ip='10.0.0.4', defaultRoute=None)
    h2 = net.addHost('h2', cls=Host, ip='10.0.0.2', defaultRoute=None)
    h9 = net.addHost('h9', cls=Host, ip='10.0.0.9', defaultRoute=None)
    h8 = net.addHost('h8', cls=Host, ip='10.0.0.8', defaultRoute=None)
    h6 = net.addHost('h6', cls=Host, ip='10.0.0.6', defaultRoute=None)
    h7 = net.addHost('h7', cls=Host, ip='10.0.0.7', defaultRoute=None)
    h3 = net.addHost('h3', cls=Host, ip='10.0.0.3', defaultRoute=None)
    h5 = net.addHost('h5', cls=Host, ip='10.0.0.5', defaultRoute=None)
    h1 = net.addHost('h1', cls=Host, ip='10.0.0.1', defaultRoute=None)
    h10 = net.addHost('h10', cls=Host, ip='10.0.0.10', defaultRoute=None)

    info( '*** Add links\n')
    s1s2 = {'delay':'10'}
    net.addLink(s1, s2, cls=TCLink , **s1s2)
    s2s3 = {'delay':'10'}
    net.addLink(s2, s3, cls=TCLink , **s2s3)
    net.addLink(h1, s1)
    net.addLink(h2, s2)
    net.addLink(h3, s3)
    net.addLink(s4, h4)
    net.addLink(s6, h6)
    net.addLink(s5, h5)
    s7s3 = {'delay':'10'}
    net.addLink(s7, s3, cls=TCLink , **s7s3)
    s8s6 = {'delay':'10'}
    net.addLink(s8, s6, cls=TCLink , **s8s6)
    net.addLink(s7, s9)
    net.addLink(s9, s8)
    s6s5 = {'delay':'10'}
    net.addLink(s6, s5, cls=TCLink , **s6s5)
    s5s4 = {'delay':'10'}
    net.addLink(s5, s4, cls=TCLink , **s5s4)
    net.addLink(h9, s9)
    net.addLink(h8, s7)
    net.addLink(h7, s8)
    net.addLink(h10, s5)
    net.addLink(h11, s4)

    info( '*** Starting network\n')
    net.build()
    info( '*** Starting controllers\n')
    for controller in net.controllers:
        controller.start()

    info( '*** Starting switches\n')
    net.get('s3').start([c0])
    net.get('s6').start([c1])
    net.get('s2').start([c0])
    net.get('s8').start([c2])
    net.get('s4').start([c1])
    net.get('s5').start([c1])
    net.get('s9').start([c2])
    net.get('s7').start([c2])
    net.get('s1').start([c0])

    info( '*** Post configure switches and hosts\n')
    h1.sendCmd('java -jar tracker.jar')
    time.sleep(3)
    h2.sendCmd('java -jar hostCustomizavel.jar 10 5 50 100 100 10.0.0.1')
    h3.sendCmd('java -jar hostCustomizavel.jar 10 5 50 100 100 10.0.0.1')
    h4.sendCmd('java -jar hostCustomizavel.jar 10 5 50 100 100 10.0.0.1')
    h5.sendCmd('java -jar hostCustomizavel.jar 10 5 50 100 100 10.0.0.1')
    h6.sendCmd('java -jar hostCustomizavel.jar 10 5 50 100 100 10.0.0.1')
    h7.sendCmd('java -jar hostCustomizavel.jar 10 5 50 100 100 10.0.0.1')
    h8.sendCmd('java -jar hostCustomizavel.jar 10 5 50 100 100 10.0.0.1')
    h9.sendCmd('java -jar hostCustomizavel.jar 10 5 50 100 100 10.0.0.1')
    h10.sendCmd('java -jar hostCustomizavel.jar 10 5 50 100 100 10.0.0.1')
    h11.sendCmd('java -jar hostCustomizavel.jar 10 5 50 100 100 10.0.0.1')
    print h1.waitOutput()
    print h2.waitOutput()
    print h3.waitOutput()
    print h4.waitOutput()
    print h5.waitOutput()
    print h6.waitOutput()
    print h7.waitOutput()
    print h8.waitOutput()
    print h9.waitOutput()
    print h11.waitOutput()
    print h10.waitOutput()
    CLI(net)
    net.stop()

if __name__ == '__main__':
    setLogLevel( 'info' )
    myNetwork()

