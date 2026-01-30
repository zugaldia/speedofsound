VERSION = v1.12.23

# Expected SHA256 checksums (update these from the checksum.txt file)
CHECKSUM_JAR = 2db96b1b032174e14a0be6707abcb52286f139c981e38bb5783cf2c7b6f13a28
CHECKSUM_AARCH64 = e92c8b26819dae7eff60335c5c96d0335594ffd08ccd3b4a9abb226c4c740e46
CHECKSUM_X64 = 0f48ba768915c6d8299fee3dd0879c8575209f6e63d230088c06726fb808c944

download-checksum:
	@echo "Downloading checksum.txt file."
	rm -f checksum.txt
	wget https://github.com/k2-fsa/sherpa-onnx/releases/download/$(VERSION)/checksum.txt
	cat checksum.txt | grep jar

download-libs:
	@echo "Removing existing JAR files..."
	rm -f sherpa-onnx-$(VERSION).jar
	rm -f sherpa-onnx-native-lib-linux-aarch64-$(VERSION).jar
	rm -f sherpa-onnx-native-lib-linux-x64-$(VERSION).jar
	@echo "Downloading JAR files..."
	wget https://github.com/k2-fsa/sherpa-onnx/releases/download/$(VERSION)/sherpa-onnx-$(VERSION).jar
	wget https://github.com/k2-fsa/sherpa-onnx/releases/download/$(VERSION)/sherpa-onnx-native-lib-linux-aarch64-$(VERSION).jar
	wget https://github.com/k2-fsa/sherpa-onnx/releases/download/$(VERSION)/sherpa-onnx-native-lib-linux-x64-$(VERSION).jar
	@echo "Verifying checksums..."
	@echo "$(CHECKSUM_JAR)  sherpa-onnx-$(VERSION).jar" | sha256sum -c -
	@echo "$(CHECKSUM_AARCH64)  sherpa-onnx-native-lib-linux-aarch64-$(VERSION).jar" | sha256sum -c -
	@echo "$(CHECKSUM_X64)  sherpa-onnx-native-lib-linux-x64-$(VERSION).jar" | sha256sum -c -
	@echo "All checksums verified."
