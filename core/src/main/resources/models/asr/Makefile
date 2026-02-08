MODEL_ID = sherpa-onnx-whisper-tiny

# Expected SHA256 checksums (update these from the checksum.txt file)
CHECKSUM_ARCHIVE = c46116994e539aa165266d96b325252728429c12535eb9d8b6a2b10f129e66b1

download-checksum:
	@echo "Downloading checksum.txt file."
	rm -f checksum.txt
	wget https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/checksum.txt
	cat checksum.txt | grep $(MODEL_ID).tar.bz2

download-archive:
	@echo "Removing existing files..."
	rm -f tiny-encoder.int8.onnx tiny-decoder.int8.onnx tiny-tokens.txt
	@echo "Downloading model file..."
	wget https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/$(MODEL_ID).tar.bz2
	@echo "Verifying checksum..."
	@echo "$(CHECKSUM_ARCHIVE)  $(MODEL_ID).tar.bz2" | sha256sum -c -
	@echo "Extracting archive files..."
	tar jxf $(MODEL_ID).tar.bz2
	cp $(MODEL_ID)/tiny-encoder.int8.onnx .
	cp $(MODEL_ID)/tiny-decoder.int8.onnx .
	cp $(MODEL_ID)/tiny-tokens.txt .
	@echo "Cleaning up..."
	rm -rf $(MODEL_ID).tar.bz2 $(MODEL_ID)/

# See: https://github.com/microsoft/onnxruntime-inference-examples/tree/main/mobile/examples/whisper/local/android
# And: https://github.com/microsoft/olive-recipes/tree/main/openai-whisper-large-v3/olive
download-tiny-merged:
	@echo "Removing existing file..."
	rm -f whisper_cpu_int8_model.onnx
	@echo "Downloading model file..."
	wget https://github.com/microsoft/onnxruntime-inference-examples/raw/refs/heads/main/mobile/examples/whisper/local/android/app/src/main/res/raw/whisper_cpu_int8_model.onnx
