prepare-gif:
	ffmpeg -i sos-text-editor.webm -vf "palettegen" palette.png
	ffmpeg -i sos-text-editor.webm -i palette.png -lavfi "paletteuse" -loop 0 sos-text-editor.gif
	rm palette.png