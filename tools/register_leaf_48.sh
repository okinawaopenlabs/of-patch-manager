curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'leaf1','deviceType':'Leaf','location':'kanekadan','datapathId':'0x000000000000000a','ofcIp':'localhost:28080','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
echo ""
sleep 1s

for i in {1..48}
do
curl -X POST -H "Content-Type:application/json" -d "{'portName':'leaf1-eth$i','portNumber':$i,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/leaf1"
echo ""
sleep 1s
done

for i in {1..48}
do
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'host$i','deviceType':'Server','location':'kanekadan','datapathId':'','ofcIp':'','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
sleep 1s
curl -X POST -H "Content-Type:application/json" -d "{'portName':'host$i-eth1','portNumber':1,'band':'1024'}" "http://localhost:8080/ofpm/device_mng/port/host$i"  
echo ""
sleep 1s
done

for i in {1..48}
do
curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'1024','link':[{'deviceName':'host$i','portName':'host$i-eth1'},{'deviceName':'leaf1','portName':'leaf1-eth$i'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
echo ""
sleep 1s
done

