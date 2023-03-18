#!/bin/sh
export J4NPDIR=`dirname $0`/..
OPTIONS="-Dsun.java2d.pmoffscreen=false -Xmx8048m -Xms2024m"
java $OPTIONS -cp "$J4NPDIR/lib/core/*:$J4NPDIR/lib/ext/*" j4np.core.J4npModuleMain $*
