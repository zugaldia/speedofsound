VERSION = v1.12.33

# Expected SHA256 checksums (update these from the checksum.txt file)
CHECKSUM_JAR = e1829d0c043be3cec15dbcd93ea631b145fd6fbe5e92fac3d1b6774ddcfb1e30
CHECKSUM_AARCH64 = f099840f58e0fadfe4312d342ea7740396b3f48d5cab4c3bd5afb7828e605ae8
CHECKSUM_X64 = dec2cd424d6236c0ace38fd9c260da7acd5dd227f0010ce9446fcbcbf55b784f

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
