# create leaf
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'leaf1','deviceType':'Leaf','location':'kanekadan','datapathId':'0x0000000000000110','ofcIp':'localhost:28080','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
echo ""
sleep 0.1s
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'leaf2','deviceType':'Leaf','location':'kanekadan','datapathId':'0x0000000000000120','ofcIp':'localhost:28080','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
echo ""
sleep 0.1s
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'leaf3','deviceType':'Leaf','location':'kanekadan','datapathId':'0x0000000000000130','ofcIp':'localhost:28080','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
echo ""
sleep 0.1s
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'leaf4','deviceType':'Leaf','location':'kanekadan','datapathId':'0x0000000000000140','ofcIp':'localhost:28080','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
echo ""
sleep 0.1s


# add to host port
for n in {1..4}
  do
  for i in {1..36}
    do
    curl -X POST -H "Content-Type:application/json" -d "{'portName':'leaf$n-eth$i','portNumber':$i,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/leaf$n"
    echo ""
    sleep 0.1s
  done
done

# add to spine port
for n in {1..4}
  do
  for i in {37..40}
    do
    curl -X POST -H "Content-Type:application/json" -d "{'portName':'leaf$n-eth$i','portNumber':$i,'band':10240}" "http://localhost:8080/ofpm/device_mng/port/leaf$n"
    echo ""
    sleep 0.1s
  done
done


# create hosts include port
for n in {1..4}
  do
  for i in {1..36}
    do
    curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'leaf$n-host$i','deviceType':'Server','location':'kanekadan','datapathId':'','ofcIp':'','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
    sleep 0.1s
    curl -X POST -H "Content-Type:application/json" -d "{'portName':'leaf$n-host$i-eth1','portNumber':1,'band':'1024'}" "http://localhost:8080/ofpm/device_mng/port/leaf$n-host$i"  
    echo ""
    sleep 0.1s
  done
done

# create spine
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'spine1','deviceType':'Spine','location':'kanekadan','datapathId':'0x0000000000001010','ofcIp':'localhost:28080','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
sleep 0.1s
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'spine2','deviceType':'Spine','location':'kanekadan','datapathId':'0x0000000000001020','ofcIp':'localhost:28080','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
sleep 0.1s
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'spine3','deviceType':'Spine','location':'kanekadan','datapathId':'0x0000000000001030','ofcIp':'localhost:28080','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
sleep 0.1s
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'spine4','deviceType':'Spine','location':'kanekadan','datapathId':'0x0000000000001040','ofcIp':'localhost:28080','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"  
sleep 0.1s


# add to leaf port
for n in {1..4}
  do
  for i in {1..4}
    do
    curl -X POST -H "Content-Type:application/json" -d "{'portName':'spine$n-eth$i','portNumber':$i,'band':10240}" "http://localhost:8080/ofpm/device_mng/port/spine$n"
    sleep 0.1s
  done
done

# connect leaf <-> host
for n in {1..4}
  do
  for i in {1..36}
    do
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'1024','link':[{'deviceName':'leaf$n-host$i','portName':'leaf$n-host$i-eth1'},{'deviceName':'leaf$n','portName':'leaf$n-eth$i'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    sleep 0.1s
  done
done

# connect spine <-> leaf
for n in {1..2}
  do
  curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'leaf$n','portName':'leaf$n-eth37'},{'deviceName':'spine1','portName':'spine1-eth$n'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
  sleep 0.1s
  curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'leaf$n','portName':'leaf$n-eth38'},{'deviceName':'spine2','portName':'spine2-eth$n'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
  sleep 0.1s
  curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'leaf$n','portName':'leaf$n-eth39'},{'deviceName':'spine3','portName':'spine3-eth$n'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
  sleep 0.1s
  curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'leaf$n','portName':'leaf$n-eth40'},{'deviceName':'spine4','portName':'spine4-eth$n'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
  sleep 0.1s
done

