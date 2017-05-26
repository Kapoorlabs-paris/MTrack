!p.font=2;
!p.charthick=2.5;
!p.charsize=2;
;;!y.ticks=1
;;!y.tickv=3
pi=3.1415926;




filename="/Users/varunkapoor/Documents/MTAnalysisRansac/TestRanSacSeedLabel1-endA.txt";
nooflines = FILE_LINES(filename);
noofoutputs = nooflines - 1;
noofcolumns=long(10)

tab=dblarr(noofcolumns,noofoutputs);



noofcolors=64;
loadct, 4, ncolors=noofcolors;
colors=noofcolors-1-lindgen(noofcolors)


openr, l, filename, /get_lun;
skip_lun, l , 1, /LINES;
readf, l, tab;
close, /all;

set_plot, "ps";
device, filename="/Users/varunkapoor/Documents/MTAnalysisRansac/TestRanSacSeedLabel1-endA.ps", /color, bits=8;


t=tab(0,*);
cwf=tab(1,*);






fromenerg=-2.0
toenerg=2

plot, t, cwf, xtitle = 'time (pixel units)', ytitle = 'Length (pixel units)'

nooftimewindows=long(100)
windowwidth=long(20)
starttimeindex= t(0)
finaltimeindex= t(noofoutputs-1-windowwidth)


finalspec=dblarr(nooftimewindows,windowwidth)
finalspec2=dblarr(nooftimewindows,windowwidth)
tarray=dblarr(nooftimewindows)

for  k=long(0),nooftimewindows-1 do begin

tstartindex=starttimeindex+k*(finaltimeindex-starttimeindex)/(1.0*nooftimewindows);
tstart=t(tstartindex);

tarray(k)=tstart;

tendindex=tstartindex+windowwidth;
tend=t(tendindex);

fftresult=fft(hanning(windowwidth)*cwf(tstartindex:tendindex), /double, /inverse)

fftresultII=fftresult*conj(fftresult)

frequ=lindgen(windowwidth)
frequ=frequ-windowwidth/2

frequ=(2*3.1415926/(t(tendindex)-t(tstartindex)))*frequ


fftresultIII=dblarr(windowwidth)

fftresultIII(windowwidth/2:windowwidth-1)=fftresultII(0:windowwidth/2-1)
fftresultIII(0:windowwidth/2-1)=fftresultII(windowwidth/2:windowwidth-1)

finalspec(k,*)= fftresultIII



endfor


maxfinalspec=max(finalspec)

finalspec=finalspec/maxfinalspec;

maxalogfinalspec=max(alog10(finalspec));
orderofmagn=6
levelsforlogfinalspec=lindgen(noofcolors)
levelsforlogfinalspec=levelsforlogfinalspec/(1.0*(noofcolors-1))
levelsforlogfinalspec=maxalogfinalspec-orderofmagn+orderofmagn*levelsforlogfinalspec


contour, alog10(finalspec),tarray,frequ, levels=levelsforlogfinalspec, c_colors=colors, /fill, /closed,pos=[0.15,0.15,0.85,0.98],  yrange=[fromenerg,toenerg], xstyle=1, ystyle=1



colorbar, ncolors=noofcolors, maxrange=levelsforlogfinalspec(0), minrange=levelsforlogfinalspec(noofcolors-1), position=[0.95,0.15,0.98,0.98], /vertical, format='(D8.2)'


plot, frequ, finalspec(nooftimewindows-1,*), xrange=[-1,1],/ylog





device, /close;
set_plot, "x";
end
