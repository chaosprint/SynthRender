SynthRender {
	var defName, args, dur, soundFileName, chan;

	var oscPath, soundPath;

	var <score, synth, buffer, bufSymbol, bufSymbolID;

	*new {
		arg defName = \default, args= #[], dur = 10, soundFileName, chan = 2;

		^super.newCopyArgs(defName, args, dur, soundFileName, chan).initSynthRender;
	}

	initSynthRender {
		score = Score.new;
		synth = Synth.basicNew(defName);
		bufSymbolID = [];
		bufSymbol = [];

		args.do{
			arg item, count;
			if (item.asString.find("buf").isNumber) {bufSymbolID = bufSymbolID.add(count) };
		};

		switch ( bufSymbolID.size,

			0, { // the condition when synth uses no buf
				score.add( [0, synth.newMsg(Server.local, args) ] );
			},

			1, { // the synth has one buffer

				buffer = Buffer.new;
				args.do{
					arg item, count;
					if (item.asString.find("buf").isNumber) {bufSymbolID = count};
				};
				if (bufSymbolID.isNumber, {
					var path;
					bufSymbol = args.removeAt(bufSymbolID);
					path = args.removeAt(bufSymbolID).path;
					score.add( [0, buffer.allocReadMsg(path)] );
					score.add([0, synth.newMsg(Server.local, args ++ [bufSymbol, buffer] )] );
				},{
					score.add([0, synth.newMsg(Server.local, args)] );
				});

			}, { // more than one buf

				buffer = {Buffer.new}!(bufSymbolID.size);
				bufSymbolID.size.do{
					arg i;
					var buf;
					bufSymbol = bufSymbol.add( args.removeAt(bufSymbolID[0]) );
					buf = args.removeAt(bufSymbolID[0]);
					score.add(
						[
							0,
							buffer[i].allocReadChannelMsg(
								buf.path, 0, -1, i, ["/b_query", buffer[i].bufnum]
							)
						]
					);
				};
				bufSymbol.do{
					arg eachSymbol, i;
					args = args ++ [ eachSymbol, buffer[i] ];
				};
				score.add( [ 0, synth.newMsg(Server.local, args) ] );
			};
		);

		if (soundFileName.isNil, {
			oscPath = thisProcess.platform.recordingsDir.replace("\\", "/") ++
			"/" ++ Date.localtime.stamp;
			soundPath = thisProcess.platform.recordingsDir.replace("\\", "/") ++
			"/" ++ Date.localtime.stamp ++ ".wav";
		}, {
			oscPath = thisProcess.platform.recordingsDir.replace("\\", "/") ++
			"/" ++ soundFileName;
			soundPath = thisProcess.platform.recordingsDir.replace("\\", "/") ++
			"/" ++ soundFileName ++ ".wav";
		});

		Server.local.waitForBoot({
			score.recordNRT(
				oscPath,
				soundPath,
				duration:dur,
				options: ServerOptions.new.numOutputBusChannels_(chan),
				action: {File.delete(oscPath)}
			);
		};
		);
	}
}