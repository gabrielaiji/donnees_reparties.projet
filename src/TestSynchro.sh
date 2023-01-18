javac *.java

java Server &

pidServer=$!

sleep 5

echo "" > out.txt

for i in {1..3}
do
    java Synchro $i >> out.txt &
done

sleep 10 # plutôt wait

awk '{ sum += $1 } END { print sum }' out.txt & # marche pas

java SynchroRead >> out.txt & # marche pas

killall -KILL java &
