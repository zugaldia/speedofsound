UDHR_BASE_URL = https://www.un.org
UDHR_PATH = about-us/universal-declaration-of-human-rights
CURL = curl -sSL -A "Mozilla/5.0"

# Each UDHR page uses h2.heading-underlined-blue for section headings. The first h2 is the
# Preamble, so Article 1 is always the second h2. We take its immediately following p as the
# Article 1 text. This pattern holds consistently across all 6 official UN language pages.
XPATH = (//h2[@class='heading-underlined-blue'])[2]/following-sibling::p[1]/text()

UDHR_FILES = udhr_en.html udhr_fr.html udhr_es.html udhr_ru.html udhr_ar.html udhr_zh.html

.PHONY: all clean

all: $(UDHR_FILES) sample.txt

udhr_en.html:
	$(CURL) "$(UDHR_BASE_URL)/en/$(UDHR_PATH)" -o $@

udhr_fr.html:
	$(CURL) "$(UDHR_BASE_URL)/fr/$(UDHR_PATH)" -o $@

udhr_es.html:
	$(CURL) "$(UDHR_BASE_URL)/es/$(UDHR_PATH)" -o $@

udhr_ru.html:
	$(CURL) "$(UDHR_BASE_URL)/ru/$(UDHR_PATH)" -o $@

udhr_ar.html:
	$(CURL) "$(UDHR_BASE_URL)/ar/$(UDHR_PATH)" -o $@

udhr_zh.html:
	$(CURL) "$(UDHR_BASE_URL)/zh/$(UDHR_PATH)" -o $@

sample.txt: $(UDHR_FILES)
	@for f in $(UDHR_FILES); do \
		xmllint --html --xpath "$(XPATH)" $$f 2>/dev/null; echo; \
	done > $@

clean:
	rm -f $(UDHR_FILES) sample.txt
