#!/bin/bash

for pdir in `ls -1 | grep results`
do
    echo "Entering $pdir"
    cd $pdir
    for rdir in `ls -1`
    do
	echo "  Entering $rdir"
	cd $rdir
	for mdir in `ls -1`
	do
	    echo "    Entering $mdir"
	    cd $mdir
	    cp ../../../stats.r .
	    rm -f summary.csv
	    for cross in `ls -1 | grep best_fitness`
	    do
		name=`echo $cross | rev | cut -f 1,2 -d '_' | cut -f 2 -d '.' | rev`
		echo "      $name"
		echo -n "$name	" >> summary.csv
		cat $cross | cut -f 6 -d ' ' | tr '\n' '\t' >> summary.csv
		echo "" >> summary.csv
	    done
	    ruby -rcsv -e 'puts CSV.parse(STDIN, :col_sep => "\t").transpose.map &:to_csv' < summary.csv > summary-t.csv
	    mv summary-t.csv summary.csv
	    Rscript stats.r
	    rm stats.r
	    cd ..
	done
	cd ..
    done
    cd ..
done
    
