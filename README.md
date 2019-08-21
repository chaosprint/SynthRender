# SynthRender

One-line code for rendering a SynthDef in SuperCollider.

## Installation

Unzip into a folder called ```SynthRender```.

In SuperCollider, go to: **File > Open user support directory**.

If it doesn't already exist, make a folder called ```Extensions```.

Move the ```SynthRender``` folder into the ```Extensions``` folder.

## Baisc Usage

For a ```SynthDef``` shown below, you can use one-line code to do the non-real-time synthesis.
It is very efficient for sound design jobs.

```
(
SynthDef(\ding, {
    arg freq = 2000;
    var sig, env;
    env = Env.perc(0.05, 1, 1).kr;
    sig = Saw.ar(freq!2) * env;
    sig =  AllpassC.ar(Decay.ar(Impulse.ar(10), 0.1, sig), 0.1, 0.1, 1);
    sig = FreeVerb.ar(sig, 0.3, 0.5, 0.3);
    Out.ar(0, sig);
}).load;
)
```

```
Synth(\ding); // SuperCollider build-in Synth playback
SynthRender(\ding); // One-line code
SynthRender(\ding, [\freq, 3000], 5, "ding"); // You can add parameters
```

It also supports ```Buffer```.

```
(
SynthDef(\sampler, {
    arg buf=0, outBus=0, rate=1, amp=0.3;
    var sig;
    sig = PlayBuf.ar(1, buf, rate*BufRateScale.kr(buf), doneAction:2)!2;
    Out.ar(outBus, sig*amp);
}).load(s);
~snd = Buffer.read(s, Platform.resourceDir +/+ "sounds/a11wlk01.wav");
)

Synth(\sampler, [\buf, ~snd]);
SynthRender(\sampler, [\buf, ~snd]);
```

## More Information

In SuperCollider, input ```SynthRender```.

Press **CTRL(Command) + D** for detailed explaination.
