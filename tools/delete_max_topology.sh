# OF-Patch max topology.

sleep_time=0.1

# Sites Switch
echo "Delete sites switch."
curl -X DELETE "http://localhost:8080/ofpm/device_mng/Sites_SW"
echo ""
sleep ${sleep_time}s

# AG Site1 Switch
for site_num in {1..5}
do
  echo "Delete site$site_num AG switch."
  curl -X DELETE "http://localhost:8080/ofpm/device_mng/AG_site${site_num}_SW"
  echo ""
  sleep ${sleep_time}s

  for spine_num in {1..4}
  do
    echo "Delete site${site_num} Spine${spine_num} switch."
    curl -X DELETE "http://localhost:8080/ofpm/device_mng/site${site_num}_spine${spine_num}_SW"
    sleep ${sleep_time}s
    echo ""
  done

  for leaf_num in {1..47}
  do
    echo "Delete site${site_num} Leaf${leaf_num} switch."
    curl -X DELETE "http://localhost:8080/ofpm/device_mng/site${site_num}_leaf${leaf_num}_SW"
    sleep ${sleep_time}s
    echo ""

    for h in {1..36}
    do
      leaf_index=`expr $leaf_num - 1`
      host_num=`expr $leaf_index \* 36 + $h`
      echo "Delete site${site_num} Host${host_num}."
      curl -X DELETE "http://localhost:8080/ofpm/device_mng/site${site_num}_host${host_num}"
      echo ""
    done
  done
done
