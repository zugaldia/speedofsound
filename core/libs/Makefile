VERSION = v1.12.24

# Expected SHA256 checksums (update these from the checksum.txt file)
CHECKSUM_JAR = c2aea10acf90fa2fe896262400aa2eded1d2578a43f053abb11869494cd49d7a
CHECKSUM_AARCH64 = 45327e125a6401277e40e5317bbcefe50d10499a6c40aa0f1c9caa5f8767b2e4
CHECKSUM_X64 = 4793872901df4ca8c8098b8fae17ae9d60f03180eb9a2a61f5ab040b3da1b5ba

download-checksum:
	@echo "Downloading checksum.txt file."
	rm -f checksum.txt
	wget https://github.com/k2-fsa/sherpa-onnx/releases/download/$(VERSION)/checksum.txt
	cat checksum.txt | grep jar

download-libs:
	@echo "Removing existing JAR files..."
	rm -f sherpa-onnx-*.jar
	@echo "Downloading JAR files..."
	wget https://github.com/k2-fsa/sherpa-onnx/releases/download/$(VERSION)/sherpa-onnx-$(VERSION).jar
	wget https://github.com/k2-fsa/sherpa-onnx/releases/download/$(VERSION)/sherpa-onnx-native-lib-linux-aarch64-$(VERSION).jar
	wget https://github.com/k2-fsa/sherpa-onnx/releases/download/$(VERSION)/sherpa-onnx-native-lib-linux-x64-$(VERSION).jar
	@echo "Verifying checksums..."
	@echo "$(CHECKSUM_JAR)  sherpa-onnx-$(VERSION).jar" | sha256sum -c -
	@echo "$(CHECKSUM_AARCH64)  sherpa-onnx-native-lib-linux-aarch64-$(VERSION).jar" | sha256sum -c -
	@echo "$(CHECKSUM_X64)  sherpa-onnx-native-lib-linux-x64-$(VERSION).jar" | sha256sum -c -
	@echo "All checksums verified."
