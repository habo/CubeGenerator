#!/bin/bash

# list all available fonts
#convert -list font

FONT=Times-Roman
FONT=Nimbus-Mono-Regular
SIZEX=5
SIZEY=5
POINTSIZE=8
JAVACLASS=CubeFont

cat <<EOF
package de.xonibo.cubegenerator;

import java.util.HashMap;
/**
	create Font with size $SIZE based on $FONT, Pointsize is $POINTSIZE
*/
public class $JAVACLASS extends CubeFont {
    super($SIZEX,$SIZEY);
    public $JAVACLASS() {
EOF

for i in {a..x} {A..X}; do 
	convert -resize ${SIZEX}x${SIZEY}\! -font $FONT -pointsize $POINTSIZE label:$i tmp.xbm
	str=`tail -n 1 tmp.xbm|sed 's/};/});/' `
	echo "    put('$i', new Character[]{$str"
done
cat <<EOF
	}
}
EOF
rm tmp.xbm
