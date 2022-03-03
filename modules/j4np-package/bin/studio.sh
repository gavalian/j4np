#!/bin/sh
export J4NPDIR=`dirname $0`/..
OPTIONS="-Dsun.java2d.pmoffscreen=false -Xmx2048m -Xms1024m"
java $OPTIONS -cp "$J4NPDIR/lib/core/*:$J4NPDIR/lib/ext/*" twig.studio.TwigStudio $*
