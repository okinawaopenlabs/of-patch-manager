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
for site_num in {1..5}
do
  echo "Create site${site_num} AG switch."
  curl -X POST -H "Content-Type:application/json" -d "{'deviceName':'AG_site${site_num}_SW','deviceType':'Aggregate_Switch','location':'site${site_num}','datapathId':'','ofcIp':'','tenant':'admin'}" "http://localhost:8080/ofpm/device_mng/"
  sleep ${sleep_time}s
  echo ""

  echo "Create site${site_num} AG switch ports."
  band_num=(1024 1024 10240 10240 10240 10240)
  for i in {1..5}
  do
    echo "Create AG_site${site_num}_SW-port$i."
    curl -X POST -H "Content-Type:application/json" -d "{'portName':'AG_site${site_num}_SW-port$i','portNumber':$i,'band':${band_num[$i]}}" "http://localhost:8080/ofpm/device_mng/port/AG_site${site_num}_SW"
    sleep ${sleep_time}s
    echo ""
  done

  echo "Connect  Sites_SW <--> site${site_num} AG switch."
  curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'1024','link':[{'deviceName':'Sites_SW','portName':'Sites_SW-port${site_num}'},{'deviceName':'AG_site${site_num}_SW','portName':'AG_site${site_num}_SW-port1'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
  echo ""
  echo ""

  # Create Spine Switch
  for spine_num in {1..4}
  do
    echo "Create site${site_num} Spine${spine_num} switch."
    datapathId_dec=`expr $site_num \* 1000 + $spine_num \* 100`
    datapathId_hex=`printf '0x%016x\n' $datapathId_dec`
    req_data="{'deviceName':'site${site_num}_spine${spine_num}_SW','deviceType':'Spine','location':'site${site_num}','datapathId':'$datapathId_hex','ofcIp':'localhost:28080','tenant':'admin'}"
    echo "Request data = $req_data"
    curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
    sleep ${sleep_time}s
    echo ""

    echo "Create site${site_num}_spine${spine_num}_SW ports."
    for i in {1..48}
    do
      echo "Create site${site_num}_spine${spine_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'site${site_num}_spine${spine_num}_SW-port$i','portNumber':$i,'band':10240}" "http://localhost:8080/ofpm/device_mng/port/site${site_num}_spine${spine_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done

    ag_sw_port_num=`expr 1 + $spine_num`
    echo "Connect  AG_site${site_num}_SW-port$ag_sw_port_num <--> site${site_num}_spine${spine_num}_SW-port48."
    curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'AG_site${site_num}_SW','portName':'AG_site${site_num}_SW-port$ag_sw_port_num'},{'deviceName':'site${site_num}_spine${spine_num}_SW','portName':'site${site_num}_spine${spine_num}_SW-port48'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
    echo ""
    echo ""
  done

  # Create Leaf Switch
  for leaf_num in {1..47}
  do
    echo "Create site${site_num} Leaf${leaf_num} switch."
    datapathId_dec=`expr $site_num \* 1000 + $leaf_num`
    datapathId_hex=`printf '0x%016x\n' $datapathId_dec`
    req_data="{'deviceName':'site${site_num}_leaf${leaf_num}_SW','deviceType':'Leaf','location':'site${site_num}','datapathId':'$datapathId_hex','ofcIp':'localhost:28080','tenant':'admin'}"
    echo "Request data = $req_data"
    curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
    sleep ${sleep_time}s
    echo ""

    echo "Create site${site_num}_leaf${leaf_num}_SW ports."
    for i in {1..48}
    do
      echo "Create site${site_num}_leaf${leaf_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'site${site_num}_leaf${leaf_num}_SW-port$i','portNumber':$i,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/site${site_num}_leaf${leaf_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done
    for i in {49..52}
    do
      echo "Create site${site_num}_leaf${leaf_num}_SW-port$i."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'site${site_num}_leaf${leaf_num}_SW-port$i','portNumber':$i,'band':10240}" "http://localhost:8080/ofpm/device_mng/port/site${site_num}_leaf${leaf_num}_SW"
      sleep ${sleep_time}s
      echo ""
    done

    for spine_num in {1..4}
    do
      leaf_sw_uplink_port_num=`expr 48 + $spine_num`
      echo "Connect  site${site_num}_spine${spine_num}_SW-port$leaf_num <--> site${site_num}_leaf${leaf_num}_SW-port$leaf_sw_uplink_port_num."
      curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'site${site_num}_spine${spine_num}_SW','portName':'site${site_num}_spine${spine_num}_SW-port$leaf_num'},{'deviceName':'site${site_num}_leaf${leaf_num}_SW','portName':'site${site_num}_leaf${leaf_num}_SW-port$leaf_sw_uplink_port_num'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
      sleep ${sleep_time}s
      echo ""
    done
      echo ""


    for h in {1..36}
    do
      leaf_index=`expr $leaf_num - 1`
      host_num=`expr $leaf_index \* 36 + $h`
      echo "Create site${site_num} Host${host_num}."
      req_data="{'deviceName':'site${site_num}_host${host_num}','deviceType':'Server','location':'site${site_num}','datapathId':'','ofcIp':'','tenant':'admin'}"
      echo "Request data = $req_data"
      curl -X POST -H "Content-Type:application/json" -d "$req_data" "http://localhost:8080/ofpm/device_mng/"
      sleep ${sleep_time}s
      echo ""

      echo "Create site${site_num}_host${host_num}-eth0."
      curl -X POST -H "Content-Type:application/json" -d "{'portName':'site${site_num}_host${host_num}-eth0','portNumber':2000,'band':1024}" "http://localhost:8080/ofpm/device_mng/port/site${site_num}_host${host_num}"
      sleep ${sleep_time}s
      echo ""

      echo "Connect  site${site_num}_leaf${leaf_num}_SW-port$h <--> site${site_num}_host${host_num}-eth0."
      curl -X POST -H "Content-Type:application/json" -d "{'links':[{'band':'10240','link':[{'deviceName':'site${site_num}_leaf${leaf_num}_SW','portName':'site${site_num}_leaf${leaf_num}_SW-port$h'},{'deviceName':'site${site_num}_host${host_num}','portName':'site${site_num}_host${host_num}-eth0'}]}]}" "http://localhost:8080/ofpm/physical_topology/connect" 
      sleep ${sleep_time}s
      echo ""
      echo ""
    done
  done
done
