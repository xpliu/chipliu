#!/bin/bash

kinit -kt user.keytab username

CLASS_PATH="./conf:lib/*:."
JAVA_OPTS=" -Djava.security.auth.login.config=./conf/jaas.conf -Djava.security.krb5.conf=./conf/krb5.conf \
-Dzookeeper.server.principal=zookeeper/hadoop.hadoop.com"

###for non FI hadoop
###-Dzookeeper.server.principal=zookeeper/<hostname>

java -cp $CLASS_PATH $JAVA_OPTS com.lxp.test.HBaseTokenTest