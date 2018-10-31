# set terminal pngcairo  transparent enhanced font "arial,10" fontscale 1.0 size 600, 400 
# set output 'simple.8.eps'
# set key bmargin left horizontal Right noreverse enhanced autotitle box lt black linewidth 1.000 dashtype solid

set terminal pdfcairo
set output 'PE2.pdf'

set key top left width 70 height 20

set title 'test graph'
#set title  font ",20" norotate

#set yrange [:0]
set xrange [:500]
set xlabel 'time (ms)'

# set label 'finished walk' at 15, 140
# unset label

plot '< sort -nk1 ../results/PE2Fsp500.txt' u 1:3 w lp ps 0.5 t 'Fsp mini500',\
'< sort -nk1 ../results/PE2Async500.txt' u 1:3 w lp ps 0.5 t 'Async mini500',\
'< sort -nk1 ../results/PE2Fsp1000.txt' u 1:3 w lp ps 0.5 t 'Fsp mini1000',\
'< sort -nk1 ../results/PE2Async1000.txt' u 1:3 w lp ps 0.5 t 'Async mini1000',\
'< sort -nk1 ../results/PE2Fsp2000.txt' u 1:3 w lp ps 0.5 t 'Fsp mini2000',\
'< sort -nk1 ../results/PE2Async2000.txt' u 1:3 w lp ps 0.5 t 'Async mini2000',\
'< sort -nk1 ../results/PE2Fsp5000.txt' u 1:3 w lp ps 0.5 t 'Fsp mini5000',\
'< sort -nk1 ../results/PE2Async5000.txt' u 1:3 w lp ps 0.5 t 'Async mini5000'