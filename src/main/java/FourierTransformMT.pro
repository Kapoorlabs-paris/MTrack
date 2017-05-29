!p.font=1;
!p.charthick=2.0;
!p.charsize=1.5;


pi=3.1415926;


fromenerg=0.01
toenerg=0.1



filename="/Users/varunkapoor/Documents/MTAnalysisRansac/TestRanSacSeedLabel2-endA.txt";

nooflines = FILE_LINES(filename);
nignore = 41;
noofoutputs = nooflines - nignore;

period = 10;
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
device, filename= "/Users/varunkapoor/Documents/MTAnalysisRansac/TestRanSacSeedLabel2-endA.ps", /color, bits=8;

t=tab(0,*);
cwf=tab(1,*);

textend = dblarr(noofextension);
cwfextend = dblarr(noofextension);

for i=0,noofoutputs - 1  do begin

textend(i) = t(i)

end




count = 1;
for i = noofoutputs, noofextension - 1  do begin

textend(i) = t(noofoutputs - 1) + count;

count++;
end

for i = 0, period - 1 do begin


cwfextend(i*(noofoutputs):(i + 1) * noofoutputs - 1 ) = cwf(0:noofoutputs - 1 )

end





plot, t, cwf, xtitle = 'time (pixel units)', ytitle = 'Length (pixel units)'


;;fftresult=fft(hanning(noofextension)*cwfextend, /double, /inverse)
   fftresult=fft(cwfextend, /double, /inverse)

fftresultII=fftresult*conj(fftresult)

frequ=lindgen(noofextension)
frequ=frequ-noofextension/2

frequ=(2*3.1415926/(textend(noofextension-1)-textend(0)))*frequ


fftresultIII=dblarr(noofextension);

fftresultIII(noofextension/2:noofextension-1)=fftresultII(0:noofextension/2-1)
fftresultIII(0:noofextension/2-1)=fftresultII(noofextension/2:noofextension-1)

fftresultIII = fftresultIII/max(fftresultIII);

plot, frequ,fftresultIII, xrange=[fromenerg,toenerg], xstyle=1, ystyle=1, /ylog, xtitle = "Frequency (1 / framenumber)", ytitle = "Amplitude", title = "Periodic extended L vs T Fourier Transform"  




device, /close;
set_plot, "x";
end
