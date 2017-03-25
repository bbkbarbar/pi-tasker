sudo kill $(ps aux | grep 'tasker' | awk '{print $2}')
echo "Tasker closed."
