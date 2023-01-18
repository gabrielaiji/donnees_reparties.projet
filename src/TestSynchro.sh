javac *.java

java Server &

sleep 5

echo "" > out.txt

for i in {1..5}
do
    java Synchro $i >> out.txt &
done

wait

awk '{ sum += $1 } END { print sum }' out.txt &

java SynchroRead &