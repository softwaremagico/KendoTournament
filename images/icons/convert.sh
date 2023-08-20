#!/bin/bash

for i in *.svg
do
	echo $i;
	dest=`basename $i .svg`.png;
	rm `basename $i .svg`.png;
	#/usr/bin/convert $i  -alpha on -background transparent $dest
	rsvg-convert $i -o $dest -h 25
done