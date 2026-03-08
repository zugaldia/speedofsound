prepare-gifs:
	rm -f demo-light.gif demo-dark.gif
	for name in demo-light demo-dark; do \
		ffmpeg -i $$name.mp4 -vf "palettegen" palette.png; \
		ffmpeg -i $$name.mp4 -i palette.png -lavfi "paletteuse" -loop 0 $$name.gif; \
		rm palette.png; \
	done
