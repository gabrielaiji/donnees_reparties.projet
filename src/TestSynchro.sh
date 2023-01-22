javac *.java

java Server &
sleep 3

echo "" > out.txt

java Synchro 0 >> out.txt &
sleep 3

for i in {1..10}
do
    java Synchro $i >> out.txt &
done

sleep 10 # plutôt wait

echo -e "\nCompteur théorique (somme des valeurs précédentes) :" >> out.txt

awk '{ sum += $1 } END { print sum }' out.txt >> out.txt

echo -e "\nCompteur réel :" >> out.txt

java SynchroRead >> out.txt

killall -KILL java
