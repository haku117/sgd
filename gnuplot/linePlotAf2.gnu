# set terminal pngcairo  transparent enhanced font "arial,10" fontscale 1.0 size 600, 400 
# set output 'simple.8.eps'
# set key bmargin left horizontal Right noreverse enhanced autotitle box lt black linewidth 1.000 dashtype solid

set terminal pdfcairo
set output 'af.pdf'

set key top left width 40 height 20

set title 'test graph'
#set title  font ",20" norotate

set yrange [:0]
set xrange [:100]
set xlabel 'time (ms)'

# set label 'finished walk' at 15, 140
# unset label

plot '../results/AfFsp1500.txt' u 1:3 w lp t 'Fsp mini1500',\
'../results/AfAsync100.txt' u 1:3 w lp t 'Async mini1500',\
'../results/AfFsp100.txt' u 1:3 w lp t 'Fsp mini100',\
'../results/AfAsync100.txt' u 1:3 w lp t 'Async mini100',\
'../results/AfFsp500.txt' u 1:3 w lp t 'Fsp mini500',\
'../results/AfAsync500.txt' u 1:3 w lp t 'Async mini500',\
'../results/AfFsp1000.txt' u 1:3 w lp t 'Fsp mini1000',\
'../results/AfAsync1000.txt' u 1:3 w lp t 'Async mini1000'