!p.font=1;
!p.charthick=2.0;
!p.charsize=1.5;


pi=3.1415926;


fromenerg=0.01
toenerg=0.1



filename="/Users/varunkapoor/Documents/MTAnalysisRansac/TestRanSacSeedLabel2-endA.txt";

nooflines = FILE_LINES(filename);
nignore = 1;
noofoutputs = nooflines - nignore;

period = 1;
noofextension = noofoutputs * period;
if (noofextension MOD 2 NE 0) then begin
noofextension = noofextension + 1;
endif
noofcolumns=long(10)
tab=dblarr(noofcolumns,noofoutputs );

noofcolors=64;
loadct, 4, ncolors=noofcolors;
colors=noofcolors-1-lindgen(noofcolors)


openr, l, filename, /get_lun;
skip_lun, l , nignore, /LINES;
readf, l, tab;
close, /all;

set_plot, "ps";
device, filename= "/Users/varunkapoor/Documents/MTAnalysisRansac/TestRanSacSeedLabel2-endAGABOR.ps", /color, bits=8;

t=tab(0,*);
cwf=tab(1,*);

textend = dblarr(noofextension);
cwfextend = dblarr(noofextension);

for i=0,noofoutputs - 1  do begin

textend(i) = t(i)

end

for i=0,noofoutputs - 1  do begin

  cwfextend(i) = cwf(i)

end


count = 1;
for i = noofoutputs, noofextension - 1  do begin

textend(i) = t(noofoutputs - 1) + count;

count++;
end

for i = 1, period - 1 do begin


cwfextend(i*(noofoutputs):(i + 1) * noofoutputs - 1 ) = cwf(0:noofoutputs - 1 )

end



plot, t, cwf, xtitle = 'time (pixel units)', ytitle = 'Length (pixel units)'

nooftimewindows=long(1000)
windowwidth=long(10)
starttimeindex= 0;
finaltimeindex= noofextension-1-windowwidth;


finalspec=dblarr(nooftimewindows,windowwidth)
finalspec2=dblarr(nooftimewindows,windowwidth)
tarray=dblarr(nooftimewindows)

for  k=long(0),nooftimewindows-1 do begin

tstartindex=starttimeindex+k*(finaltimeindex-starttimeindex)/(1.0*nooftimewindows);
tstart=textend(tstartindex);

tarray(k)=tstart;

tendindex=tstartindex+windowwidth;
tend=textend(tendindex);

fftresult=fft(hanning(windowwidth)*cwfextend(tstartindex:tendindex), /double, /inverse)

fftresultII=fftresult*conj(fftresult)

frequ=lindgen(windowwidth)
frequ=frequ-windowwidth/2


frequ=(2*3.1415926/(textend(tendindex)-textend(tstartindex)))*frequ



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


contour, alog10(finalspec),tarray,frequ, levels=levelsforlogfinalspec, c_colors=colors, /fill, /closed,pos=[0.15,0.15,0.85,0.98],  yrange=[fromenerg,toenerg],xrange=[t(0), t(noofoutputs - 1)],  xstyle=1, ystyle=1, xtitle = "Time (pixel units)",ytitle = "Frequency (pixel units)"



colorbar, ncolors=noofcolors, maxrange=levelsforlogfinalspec(0), minrange=levelsforlogfinalspec(noofcolors-1), position=[0.95,0.15,0.98,0.98], /vertical, format='(D8.2)'


plot, frequ, finalspec(nooftimewindows-1,*),   xrange=[fromenerg,toenerg],/ylog, xtitle = "Frequency (1 / framenumber)", ytitle = "Amplitude", title = "Periodic extended L vs T Fourier Transform"





device, /close;
set_plot, "x";
end
