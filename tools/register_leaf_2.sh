curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'leaf1','deviceType':'Leaf','location':'kanekadan','datapathId':'0x000000000000000a','ofcIp':'localhost:28080','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'host1','deviceType':'Server','location':'kanekadan','datapathId':'','ofcIp':'','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'host2','deviceType':'Server','location':'kanekadan','datapathId':'','ofcIp':'','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/" 
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'host3','deviceType':'Server','location':'kanekadan','datapathId':'','ofcIp':'','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/" 

curl -X POST -H "Content-Type:application/json" -d "{'portName':'leaf1-eth1','portNumber':1,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/leaf1"
curl -X POST -H "Content-Type:application/json" -d "{'portName':'leaf1-eth2','portNumber':2,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/leaf1"
curl -X POST -H "Content-Type:application/json" -d "{'portName':'leaf1-eth3','portNumber':3,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/leaf1"  

curl -X POST -H "Content-Type:application/json" -d "{'portName':'host1-eth1','portNumber':1,'band':'1024'}" "http://localhost:8080/ofpm/device_mng/port/host1"  
curl -X POST -H "Content-Type:application/json" -d "{'portName':'host2-eth1','portNumber':1,'band':'1024'}" "http://localhost:8080/ofpm/device_mng/port/host2"  
curl -X POST -H "Content-Type:application/json" -d "{'portName':'host3-eth1','portNumber':1,'band':'1024'}" "http://localhost:8080/ofpm/device_mng/port/host3"  

curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'1024','link':[{'deviceName':'host1','portName':'host1-eth1'},{'deviceName':'leaf1','portName':'leaf1-eth1'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'1024','link':[{'deviceName':'host2','portName':'host2-eth1'},{'deviceName':'leaf1','portName':'leaf1-eth2'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'1024','link':[{'deviceName':'host3','portName':'host3-eth1'},{'deviceName':'leaf1','portName':'leaf1-eth3'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 

