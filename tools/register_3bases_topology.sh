# OF-Patch max topology.

alias dec2hex="printf '0x%016x\n'"

sleep_time=0.1

# Sites Switch
echo "Create sites switch."
curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'Sites_SW','deviceType':'Sites_Switch','location':'-','datapathId':'','ofcIp':'','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"
sleep ${sleep_time}s
echo ""

echo "Create sites switch ports."
for i in {1..5}
do
  echo "Create Sites_SW-port$i."
  curl -X POST -H "Content-Type:application/json" -d "{'portName':'Sites_SW-port$i','portNumber':$i,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/Sites_SW"
  sleep ${sleep_time}s
  echo ""
done
echo ""


# AG Site Switch
# Kanekadan
  echo "Create Kane AG switch."
  curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'AG_Kane_SW','deviceType':'Aggregate_Switch','location':'Kane','datapathId':'','ofcIp':'','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"
  sleep ${sleep_time}s
  echo ""

  echo "Create Kane AG switch ports."
  band_num=(1024 1024 10240 10240 10240 10240)
  for i in {1..5}
  do
    echo "Create AG_Kane_SW-port$i."
    curl -X POST -H "Content-Type:application/json" -d "{'portName':'AG_Kane_SW-port$i','portNumber':$i,'band':${band_num[$i]}}" "http://localhost:8080/ofpm/device_mng/port/AG_Kane_SW"
    sleep ${sleep_time}s
    echo ""
  done

  echo "Connect  Sites_SW <--> AG Kane switch."
  curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'1024','link':[{'deviceName':'Sites_SW','portName':'Sites_SW-port1'},{'deviceName':'AG_Kane_SW','portName':'AG_Kane_SW-port1'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
  echo ""
  echo ""

# IT sinryo park
  echo "Create ITs AG switch."
  curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'AG_ITs_SW','deviceType':'Aggregate_Switch','location':'ITs','datapathId':'','ofcIp':'','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"
  sleep ${sleep_time}s
  echo ""

  echo "Create ITs AG switch ports."
  band_num=(1024 1024 10240 10240 10240 10240)
  for i in {1..5}
  do
    echo "Create AG_ITs_SW-port$i."
    curl -X POST -H "Content-Type:application/json" -d "{'portName':'AG_ITs_SW-port$i','portNumber':$i,'band':${band_num[$i]}}" "http://localhost:8080/ofpm/device_mng/port/AG_ITs_SW"
    sleep ${sleep_time}s
    echo ""
  done

  echo "Connect  Sites_SW <--> AG ITs switch."
  curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'1024','link':[{'deviceName':'Sites_SW','portName':'Sites_SW-port2'},{'deviceName':'AG_ITs_SW','portName':'AG_ITs_SW-port1'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
  echo ""
  echo ""

# Jicchaku
  echo "Create Jic AG switch."
  curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'AG_Jic_SW','deviceType':'Aggregate_Switch','location':'Jic','datapathId':'','ofcIp':'','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"
  sleep ${sleep_time}s
  echo ""

  echo "Create Jic AG switch ports."
  band_num=(1024 1024 10240 10240 10240 10240)
  for i in {1..5}
  do
    echo "Create AG_Jic_SW-port$i."
    curl -X POST -H "Content-Type:application/json" -d "{'portName':'AG_Jic_SW-port$i','portNumber':$i,'band':${band_num[$i]}}" "http://localhost:8080/ofpm/device_mng/port/AG_Jic_SW"
    sleep ${sleep_time}s
    echo ""
  done

  echo "Connect  Sites_SW <--> AG Jic switch."
  curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'1024','link':[{'deviceName':'Sites_SW','portName':'Sites_SW-port3'},{'deviceName':'AG_Jic_SW','portName':'AG_Jic_SW-port1'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
  echo ""
  echo ""

# Create Spine Switch
# Kanekadan hasn't Spine Switch(skip this section)
# IT sinryo park
  for spine_num in {1..2}
  do
    echo "Create ITs Spine${spine_num} switch."
    datapathId_dec=`expr 1 \* 1000 + $spine_num \* 100`
    datapathId_hex=`printf '0x%016x\n' $datapathId_dec`
    req_data="{'deviceName':'ITs_spine${spine_num}_SW','deviceType':'Spine','location':'ITs','datapathId':'$datapathId_hex','ofcIp':'localhost:28080','tenant':'admin'}"
    echo "Request data = $req_data"
    curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
    sleep ${sleep_time}s
    echo ""

    echo "Create ITs_spine${spine_num}_SW ports."
    for i in {1..36}
    do
      echo "Create ITs_spine${spine_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'ITs_spine${spine_num}_SW-port$i','portNumber':$i,'band':10240}" "http://localhost:8080/ofpm/device_mng/port/ITs_spine${spine_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done

    ag_sw_port_num=`expr 1 + $spine_num`
    echo "Connect  AG_ITs_SW-port$ag_sw_port_num <--> ITs_spine${spine_num}_SW-port17."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'AG_ITs_SW','portName':'AG_ITs_SW-port$ag_sw_port_num'},{'deviceName':'ITs_spine${spine_num}_SW','portName':'ITs_spine${spine_num}_SW-port17'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    echo ""
  done

# Jicchaku
  for spine_num in {1..2}
  do
    echo "Create Jic Spine${spine_num} switch."
    datapathId_dec=`expr 2 \* 1000 + $spine_num \* 100`
    datapathId_hex=`printf '0x%016x\n' $datapathId_dec`
    req_data="{'deviceName':'Jic_spine${spine_num}_SW','deviceType':'Spine','location':'Jic','datapathId':'$datapathId_hex','ofcIp':'localhost:28080','tenant':'admin'}"
    echo "Request data = $req_data"
    curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
    sleep ${sleep_time}s
    echo ""

    echo "Create Jic_spine${spine_num}_SW ports."
    for i in {1..36}
    do
      echo "Create Jic_spine${spine_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'Jic_spine${spine_num}_SW-port$i','portNumber':$i,'band':10240}" "http://localhost:8080/ofpm/device_mng/port/Jic_spine${spine_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done

    ag_sw_port_num=`expr 1 + $spine_num`
    echo "Connect AG_Jic_SW-port$ag_sw_port_num <--> Jic_spine${spine_num}_SW-port13."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'AG_Jic_SW','portName':'AG_Jic_SW-port$ag_sw_port_num'},{'deviceName':'Jic_spine${spine_num}_SW','portName':'Jic_spine${spine_num}_SW-port13'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    echo ""
  done


# Create Leaf Switch & Host
# Kanekadan
  for leaf_num in {1..1}
  do
    echo "Create Kane Leaf${leaf_num} switch."
    datapathId_dec=`expr 1 \* 1000 + $leaf_num`
    datapathId_hex=`printf '0x%016x\n' $datapathId_dec`
    req_data="{'deviceName':'Kane_leaf${leaf_num}_SW','deviceType':'Leaf','location':'Kane','datapathId':'$datapathId_hex','ofcIp':'localhost:28080','tenant':'admin'}"
    echo "Request data = $req_data"
    curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
    sleep ${sleep_time}s
    echo ""

    echo "Create Kane_leaf${leaf_num}_SW ports."
    for i in {1..48}
    do
      echo "Create Kane_leaf${leaf_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'Kane_leaf${leaf_num}_SW-port$i','portNumber':$i,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/Kane_leaf${leaf_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done
    for i in {49..52}
    do
      echo "Create Kane_leaf${leaf_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'Kane_leaf${leaf_num}_SW-port$i','portNumber':$i,'band':10240}" "http://localhost:8080/ofpm/device_mng/port/Kane_leaf${leaf_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done

# Kanekadan hasn't Spine Switch

    for h in {1..48}
    do
      leaf_index=`expr $leaf_num - 1`
      host_num=`expr $leaf_index \* 48 + $h`
      echo "Create Kane Host${host_num}."
      req_data="{'deviceName':'Kane_host${host_num}','deviceType':'Server','location':'Kane','datapathId':'','ofcIp':'','tenant':'admin'}"
      echo "Request data = $req_data"
      curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
      sleep ${sleep_time}s
      echo ""

      echo "Create Kane_host${host_num}-eth0."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'Kane_host${host_num}-eth0','portNumber':1,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/Kane_host${host_num}"
      sleep ${sleep_time}s
      echo ""

      echo "Connect  Kane_leaf${leaf_num}_SW-port$h <--> Kane_host${host_num}-eth0."
      curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'Kane_leaf${leaf_num}_SW','portName':'Kane_leaf${leaf_num}_SW-port$h'},{'deviceName':'Kane_host${host_num}','portName':'Kane_host${host_num}-eth0'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
      sleep ${sleep_time}s
      echo ""
    done
    echo "Connect  AG_Kane_SW-port2 <--> Kane_leaf1_SW-port49."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'AG_Kane_SW','portName':'AG_Kane_SW-port2'},{'deviceName':'Kane_leaf1_SW','portName':'Kane_leaf1_SW-port49'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 

    sleep ${sleep_time}s
  done


# IT sinryo park
  for leaf_num in {1..8}
  do
    echo "Create ITs Leaf${leaf_num} switch."
    datapathId_dec=`expr 2 \* 1000 + $leaf_num`
    datapathId_hex=`printf '0x%016x\n' $datapathId_dec`
    req_data="{'deviceName':'ITs_leaf${leaf_num}_SW','deviceType':'Leaf','location':'ITs','datapathId':'$datapathId_hex','ofcIp':'localhost:28080','tenant':'admin'}"
    echo "Request data = $req_data"
    curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
    sleep ${sleep_time}s
    echo ""

    echo "Create ITs_leaf${leaf_num}_SW ports."
    for i in {1..36}
    do
      echo "Create ITs_leaf${leaf_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'ITs_leaf${leaf_num}_SW-port$i','portNumber':$i,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/ITs_leaf${leaf_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done
    for i in {37..40}
    do
      echo "Create ITs_leaf${leaf_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'ITs_leaf${leaf_num}_SW-port$i','portNumber':$i,'band':10240}" "http://localhost:8080/ofpm/device_mng/port/ITs_leaf${leaf_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done

    spine_port_num=`expr $leaf_num \* 2 - 1`
    echo "Connect  ITs_spine1_SW-port$spine_port_num <--> ITs_leaf${leaf_num}_SW-port37."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'ITs_spine1_SW','portName':'ITs_spine1_SW-port${spine_port_num}'},{'deviceName':'ITs_leaf${leaf_num}_SW','portName':'ITs_leaf${leaf_num}_SW-port37'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    echo "Connect  ITs_spine1_SW-port`expr $spine_port_num + 1` <--> ITs_leaf${leaf_num}_SW-port38."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'ITs_spine1_SW','portName':'ITs_spine1_SW-port`expr $spine_port_num + 1`'},{'deviceName':'ITs_leaf${leaf_num}_SW','portName':'ITs_leaf${leaf_num}_SW-port38'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    echo "Connect  ITs_spine1_SW-port$spine_port_num <--> ITs_leaf${leaf_num}_SW-port39."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'ITs_spine1_SW','portName':'ITs_spine1_SW-port${spine_port_num}'},{'deviceName':'ITs_leaf${leaf_num}_SW','portName':'ITs_leaf${leaf_num}_SW-port39'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    echo "Connect  ITs_spine1_SW-port`expr $spine_port_num + 1` <--> ITs_leaf${leaf_num}_SW-port40."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'ITs_spine1_SW','portName':'ITs_spine1_SW-port`expr $spine_port_num + 1`'},{'deviceName':'ITs_leaf${leaf_num}_SW','portName':'ITs_leaf${leaf_num}_SW-port40'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    sleep ${sleep_time}s
    echo ""

    for h in {1..36}
    do
      leaf_index=`expr $leaf_num - 1`
      host_num=`expr $leaf_index \* 36 + $h`
      echo "Create ITs Host${host_num}."
      req_data="{'deviceName':'ITs_host${host_num}','deviceType':'Server','location':'ITs','datapathId':'','ofcIp':'','tenant':'admin'}"
      echo "Request data = $req_data"
      curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
      sleep ${sleep_time}s
      echo ""

      echo "Create ITs_host${host_num}-eth0."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'ITs_host${host_num}-eth0','portNumber':1,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/ITs_host${host_num}"
      sleep ${sleep_time}s
      echo ""

      echo "Connect  ITs_leaf${leaf_num}_SW-port$h <--> ITs_host${host_num}-eth0."
      curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'ITs_leaf${leaf_num}_SW','portName':'ITs_leaf${leaf_num}_SW-port$h'},{'deviceName':'ITs_host${host_num}','portName':'ITs_host${host_num}-eth0'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
      sleep ${sleep_time}s
      echo ""
      echo ""
    done
  done


# Jicchaku
  for leaf_num in {1..6}
  do
    echo "Create Jic Leaf${leaf_num} switch."
    datapathId_dec=`expr 3 \* 1000 + $leaf_num`
    datapathId_hex=`printf '0x%016x\n' $datapathId_dec`
    req_data="{'deviceName':'Jic_leaf${leaf_num}_SW','deviceType':'Leaf','location':'Jic','datapathId':'$datapathId_hex','ofcIp':'localhost:28080','tenant':'admin'}"
    echo "Request data = $req_data"
    curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
    sleep ${sleep_time}s
    echo ""

    echo "Create Jic_leaf${leaf_num}_SW ports."
    for i in {1..48}
    do
      echo "Create Jic_leaf${leaf_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'Jic_leaf${leaf_num}_SW-port$i','portNumber':$i,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/Jic_leaf${leaf_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done
    for i in {49..52}
    do
      echo "Create Jic_leaf${leaf_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'Jic_leaf${leaf_num}_SW-port$i','portNumber':$i,'band':10240}" "http://localhost:8080/ofpm/device_mng/port/Jic_leaf${leaf_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done

    spine_port_num=`expr $leaf_num \* 2 - 1`
    echo "Connect  Jic_spine1_SW-port$spine_port_num <--> Jic_leaf${leaf_num}_SW-port37."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'Jic_spine1_SW','portName':'Jic_spine1_SW-port${spine_port_num}'},{'deviceName':'Jic_leaf${leaf_num}_SW','portName':'Jic_leaf${leaf_num}_SW-port37'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    echo "Connect  Jic_spine1_SW-port`expr $spine_port_num + 1` <--> Jic_leaf${leaf_num}_SW-port38."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'Jic_spine1_SW','portName':'Jic_spine1_SW-port`expr $spine_port_num + 1`'},{'deviceName':'Jic_leaf${leaf_num}_SW','portName':'Jic_leaf${leaf_num}_SW-port38'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    echo "Connect  Jic_spine1_SW-port$spine_port_num <--> Jic_leaf${leaf_num}_SW-port39."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'Jic_spine1_SW','portName':'Jic_spine1_SW-port${spine_port_num}'},{'deviceName':'Jic_leaf${leaf_num}_SW','portName':'Jic_leaf${leaf_num}_SW-port39'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    echo "Connect  Jic_spine1_SW-port`expr $spine_port_num + 1` <--> Jic_leaf${leaf_num}_SW-port40."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'Jic_spine1_SW','portName':'Jic_spine1_SW-port`expr $spine_port_num + 1`'},{'deviceName':'Jic_leaf${leaf_num}_SW','portName':'Jic_leaf${leaf_num}_SW-port40'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    sleep ${sleep_time}s
    echo ""

    for h in {1..36}
    do
      leaf_index=`expr $leaf_num - 1`
      host_num=`expr $leaf_index \* 36 + $h`
      echo "Create Jic Host${host_num}."
      req_data="{'deviceName':'Jic_host${host_num}','deviceType':'Server','location':'Jic','datapathId':'','ofcIp':'','tenant':'admin'}"
      echo "Request data = $req_data"
      curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
      sleep ${sleep_time}s
      echo ""

      echo "Create Jic_host${host_num}-eth0."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'Jic_host${host_num}-eth0','portNumber':1,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/Jic_host${host_num}"
      sleep ${sleep_time}s
      echo ""

      echo "Connect  Jic_leaf${leaf_num}_SW-port$h <--> Jic_host${host_num}-eth0."
      curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'Jic_leaf${leaf_num}_SW','portName':'Jic_leaf${leaf_num}_SW-port$h'},{'deviceName':'Jic_host${host_num}','portName':'Jic_host${host_num}-eth0'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
      sleep ${sleep_time}s
      echo ""
      echo ""
    done
  done


