#!/bin/sh

RULES=100
FACTS=1000

#java -jar .ruleGenerator.jar <rules> <max body atoms> <facts> <individuals> <new predicates> <max arity> 

source ../env

echo "generating f0.p"
${JAVA} -XX:+AggressiveHeap -jar .rulesGenerator.jar lipid.fs.owl lipid/f0.p $RULES 5 $FACTS 1000 20 3
cat lipid/f0.p > lipid/0.p

for i in {1..9}; do
	echo "generating f${i}.p";
  ${JAVA} -XX:+AggressiveHeap -jar .rulesGenerator.jar lipid.fs.owl lipid/f${i}.p 0 5 $FACTS 1000 20 3;
	echo "concatenation";
	cat lipid/f*.p > lipid/${i}.p;
done;
rm lipid/f*.p
