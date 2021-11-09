#!/bin/sh
export J4NPDIR=`dirname $0`/..
java -cp $J4NPDIR/target/j4np-level3-1.0.2-SNAPSHOT-jar-with-dependencies.jar  j4np.level3.data.Evio2Et $*

