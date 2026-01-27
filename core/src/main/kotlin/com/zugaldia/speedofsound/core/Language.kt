package com.zugaldia.speedofsound.core

/**
 * ISO 639 language codes. Generated from ISO-639-2 standard:
 * https://www.loc.gov/standards/iso639-2/ISO-639-2_utf-8.txt
 */
data class Language(val name: String, val iso2: String, val iso3: String) {
    companion object {
        val AFAR = Language("Afar", "aa", "aar")
        val ABKHAZIAN = Language("Abkhazian", "ab", "abk")
        val AFRIKAANS = Language("Afrikaans", "af", "afr")
        val AKAN = Language("Akan", "ak", "aka")
        val ALBANIAN = Language("Albanian", "sq", "alb")
        val AMHARIC = Language("Amharic", "am", "amh")
        val ARABIC = Language("Arabic", "ar", "ara")
        val ARAGONESE = Language("Aragonese", "an", "arg")
        val ARMENIAN = Language("Armenian", "hy", "arm")
        val ASSAMESE = Language("Assamese", "as", "asm")
        val AVARIC = Language("Avaric", "av", "ava")
        val AVESTAN = Language("Avestan", "ae", "ave")
        val AYMARA = Language("Aymara", "ay", "aym")
        val AZERBAIJANI = Language("Azerbaijani", "az", "aze")
        val BASHKIR = Language("Bashkir", "ba", "bak")
        val BAMBARA = Language("Bambara", "bm", "bam")
        val BASQUE = Language("Basque", "eu", "baq")
        val BELARUSIAN = Language("Belarusian", "be", "bel")
        val BENGALI = Language("Bengali", "bn", "ben")
        val BISLAMA = Language("Bislama", "bi", "bis")
        val BOSNIAN = Language("Bosnian", "bs", "bos")
        val BRETON = Language("Breton", "br", "bre")
        val BULGARIAN = Language("Bulgarian", "bg", "bul")
        val BURMESE = Language("Burmese", "my", "bur")
        val CATALAN = Language("Catalan", "ca", "cat")
        val CHAMORRO = Language("Chamorro", "ch", "cha")
        val CHECHEN = Language("Chechen", "ce", "che")
        val CHINESE = Language("Chinese", "zh", "chi")
        val CHURCH_SLAVIC = Language("Church Slavic", "cu", "chu")
        val CHUVASH = Language("Chuvash", "cv", "chv")
        val CORNISH = Language("Cornish", "kw", "cor")
        val CORSICAN = Language("Corsican", "co", "cos")
        val CREE = Language("Cree", "cr", "cre")
        val CZECH = Language("Czech", "cs", "cze")
        val DANISH = Language("Danish", "da", "dan")
        val DIVEHI = Language("Divehi", "dv", "div")
        val DUTCH = Language("Dutch", "nl", "dut")
        val DZONGKHA = Language("Dzongkha", "dz", "dzo")
        val ENGLISH = Language("English", "en", "eng")
        val ESPERANTO = Language("Esperanto", "eo", "epo")
        val ESTONIAN = Language("Estonian", "et", "est")
        val EWE = Language("Ewe", "ee", "ewe")
        val FAROESE = Language("Faroese", "fo", "fao")
        val FIJIAN = Language("Fijian", "fj", "fij")
        val FINNISH = Language("Finnish", "fi", "fin")
        val FRENCH = Language("French", "fr", "fre")
        val WESTERN_FRISIAN = Language("Western Frisian", "fy", "fry")
        val FULAH = Language("Fulah", "ff", "ful")
        val GEORGIAN = Language("Georgian", "ka", "geo")
        val GERMAN = Language("German", "de", "ger")
        val GAELIC = Language("Gaelic", "gd", "gla")
        val IRISH = Language("Irish", "ga", "gle")
        val GALICIAN = Language("Galician", "gl", "glg")
        val MANX = Language("Manx", "gv", "glv")
        val MODERN_GREEK = Language("Modern Greek", "el", "gre")
        val GUARANI = Language("Guarani", "gn", "grn")
        val GUJARATI = Language("Gujarati", "gu", "guj")
        val HAITIAN = Language("Haitian", "ht", "hat")
        val HAUSA = Language("Hausa", "ha", "hau")
        val HEBREW = Language("Hebrew", "he", "heb")
        val HERERO = Language("Herero", "hz", "her")
        val HINDI = Language("Hindi", "hi", "hin")
        val HIRI_MOTU = Language("Hiri Motu", "ho", "hmo")
        val CROATIAN = Language("Croatian", "hr", "hrv")
        val HUNGARIAN = Language("Hungarian", "hu", "hun")
        val IGBO = Language("Igbo", "ig", "ibo")
        val ICELANDIC = Language("Icelandic", "is", "ice")
        val IDO = Language("Ido", "io", "ido")
        val SICHUAN_YI = Language("Sichuan Yi", "ii", "iii")
        val INUKTITUT = Language("Inuktitut", "iu", "iku")
        val INTERLINGUE = Language("Interlingue", "ie", "ile")
        val INTERLINGUA = Language("Interlingua", "ia", "ina")
        val INDONESIAN = Language("Indonesian", "id", "ind")
        val INUPIAQ = Language("Inupiaq", "ik", "ipk")
        val ITALIAN = Language("Italian", "it", "ita")
        val JAVANESE = Language("Javanese", "jv", "jav")
        val JAPANESE = Language("Japanese", "ja", "jpn")
        val KALAALLISUT = Language("Kalaallisut", "kl", "kal")
        val KANNADA = Language("Kannada", "kn", "kan")
        val KASHMIRI = Language("Kashmiri", "ks", "kas")
        val KANURI = Language("Kanuri", "kr", "kau")
        val KAZAKH = Language("Kazakh", "kk", "kaz")
        val CENTRAL_KHMER = Language("Central Khmer", "km", "khm")
        val KIKUYU = Language("Kikuyu", "ki", "kik")
        val KINYARWANDA = Language("Kinyarwanda", "rw", "kin")
        val KIRGHIZ = Language("Kirghiz", "ky", "kir")
        val KOMI = Language("Komi", "kv", "kom")
        val KONGO = Language("Kongo", "kg", "kon")
        val KOREAN = Language("Korean", "ko", "kor")
        val KUANYAMA = Language("Kuanyama", "kj", "kua")
        val KURDISH = Language("Kurdish", "ku", "kur")
        val LAO = Language("Lao", "lo", "lao")
        val LATIN = Language("Latin", "la", "lat")
        val LATVIAN = Language("Latvian", "lv", "lav")
        val LIMBURGAN = Language("Limburgan", "li", "lim")
        val LINGALA = Language("Lingala", "ln", "lin")
        val LITHUANIAN = Language("Lithuanian", "lt", "lit")
        val LUXEMBOURGISH = Language("Luxembourgish", "lb", "ltz")
        val LUBA_KATANGA = Language("Luba-Katanga", "lu", "lub")
        val GANDA = Language("Ganda", "lg", "lug")
        val MACEDONIAN = Language("Macedonian", "mk", "mac")
        val MARSHALLESE = Language("Marshallese", "mh", "mah")
        val MALAYALAM = Language("Malayalam", "ml", "mal")
        val MAORI = Language("Maori", "mi", "mao")
        val MARATHI = Language("Marathi", "mr", "mar")
        val MALAY = Language("Malay", "ms", "may")
        val MALAGASY = Language("Malagasy", "mg", "mlg")
        val MALTESE = Language("Maltese", "mt", "mlt")
        val MONGOLIAN = Language("Mongolian", "mn", "mon")
        val NAURU = Language("Nauru", "na", "nau")
        val NAVAJO = Language("Navajo", "nv", "nav")
        val SOUTH_NDEBELE = Language("South Ndebele", "nr", "nbl")
        val NORTH_NDEBELE = Language("North Ndebele", "nd", "nde")
        val NDONGA = Language("Ndonga", "ng", "ndo")
        val NEPALI = Language("Nepali", "ne", "nep")
        val NORWEGIAN_NYNORSK = Language("Norwegian Nynorsk", "nn", "nno")
        val NORWEGIAN_BOKMAL = Language("Norwegian Bokmål", "nb", "nob")
        val NORWEGIAN = Language("Norwegian", "no", "nor")
        val CHICHEWA = Language("Chichewa", "ny", "nya")
        val OCCITAN = Language("Occitan", "oc", "oci")
        val OJIBWA = Language("Ojibwa", "oj", "oji")
        val ORIYA = Language("Oriya", "or", "ori")
        val OROMO = Language("Oromo", "om", "orm")
        val OSSETIAN = Language("Ossetian", "os", "oss")
        val PANJABI = Language("Panjabi", "pa", "pan")
        val PERSIAN = Language("Persian", "fa", "per")
        val PALI = Language("Pali", "pi", "pli")
        val POLISH = Language("Polish", "pl", "pol")
        val PORTUGUESE = Language("Portuguese", "pt", "por")
        val PUSHTO = Language("Pushto", "ps", "pus")
        val QUECHUA = Language("Quechua", "qu", "que")
        val ROMANSH = Language("Romansh", "rm", "roh")
        val ROMANIAN = Language("Romanian", "ro", "rum")
        val RUNDI = Language("Rundi", "rn", "run")
        val RUSSIAN = Language("Russian", "ru", "rus")
        val SANGO = Language("Sango", "sg", "sag")
        val SANSKRIT = Language("Sanskrit", "sa", "san")
        val SINHALA = Language("Sinhala", "si", "sin")
        val SLOVAK = Language("Slovak", "sk", "slo")
        val SLOVENIAN = Language("Slovenian", "sl", "slv")
        val NORTHERN_SAMI = Language("Northern Sami", "se", "sme")
        val SAMOAN = Language("Samoan", "sm", "smo")
        val SHONA = Language("Shona", "sn", "sna")
        val SINDHI = Language("Sindhi", "sd", "snd")
        val SOMALI = Language("Somali", "so", "som")
        val SOTHO = Language("Sotho", "st", "sot")
        val SPANISH = Language("Spanish", "es", "spa")
        val SARDINIAN = Language("Sardinian", "sc", "srd")
        val SERBIAN = Language("Serbian", "sr", "srp")
        val SWATI = Language("Swati", "ss", "ssw")
        val SUNDANESE = Language("Sundanese", "su", "sun")
        val SWAHILI = Language("Swahili", "sw", "swa")
        val SWEDISH = Language("Swedish", "sv", "swe")
        val TAHITIAN = Language("Tahitian", "ty", "tah")
        val TAMIL = Language("Tamil", "ta", "tam")
        val TATAR = Language("Tatar", "tt", "tat")
        val TELUGU = Language("Telugu", "te", "tel")
        val TAJIK = Language("Tajik", "tg", "tgk")
        val TAGALOG = Language("Tagalog", "tl", "tgl")
        val THAI = Language("Thai", "th", "tha")
        val TIBETAN = Language("Tibetan", "bo", "tib")
        val TIGRINYA = Language("Tigrinya", "ti", "tir")
        val TONGA = Language("Tonga", "to", "ton")
        val TSWANA = Language("Tswana", "tn", "tsn")
        val TSONGA = Language("Tsonga", "ts", "tso")
        val TURKMEN = Language("Turkmen", "tk", "tuk")
        val TURKISH = Language("Turkish", "tr", "tur")
        val TWI = Language("Twi", "tw", "twi")
        val UIGHUR = Language("Uighur", "ug", "uig")
        val UKRAINIAN = Language("Ukrainian", "uk", "ukr")
        val URDU = Language("Urdu", "ur", "urd")
        val UZBEK = Language("Uzbek", "uz", "uzb")
        val VENDA = Language("Venda", "ve", "ven")
        val VIETNAMESE = Language("Vietnamese", "vi", "vie")
        val VOLAPUK = Language("Volapük", "vo", "vol")
        val WELSH = Language("Welsh", "cy", "wel")
        val WALLOON = Language("Walloon", "wa", "wln")
        val WOLOF = Language("Wolof", "wo", "wol")
        val XHOSA = Language("Xhosa", "xh", "xho")
        val YIDDISH = Language("Yiddish", "yi", "yid")
        val YORUBA = Language("Yoruba", "yo", "yor")
        val ZHUANG = Language("Zhuang", "za", "zha")
        val ZULU = Language("Zulu", "zu", "zul")

        val all = listOf(
            AFAR,
            ABKHAZIAN,
            AFRIKAANS,
            AKAN,
            ALBANIAN,
            AMHARIC,
            ARABIC,
            ARAGONESE,
            ARMENIAN,
            ASSAMESE,
            AVARIC,
            AVESTAN,
            AYMARA,
            AZERBAIJANI,
            BASHKIR,
            BAMBARA,
            BASQUE,
            BELARUSIAN,
            BENGALI,
            BISLAMA,
            BOSNIAN,
            BRETON,
            BULGARIAN,
            BURMESE,
            CATALAN,
            CHAMORRO,
            CHECHEN,
            CHINESE,
            CHURCH_SLAVIC,
            CHUVASH,
            CORNISH,
            CORSICAN,
            CREE,
            CZECH,
            DANISH,
            DIVEHI,
            DUTCH,
            DZONGKHA,
            ENGLISH,
            ESPERANTO,
            ESTONIAN,
            EWE,
            FAROESE,
            FIJIAN,
            FINNISH,
            FRENCH,
            WESTERN_FRISIAN,
            FULAH,
            GEORGIAN,
            GERMAN,
            GAELIC,
            IRISH,
            GALICIAN,
            MANX,
            MODERN_GREEK,
            GUARANI,
            GUJARATI,
            HAITIAN,
            HAUSA,
            HEBREW,
            HERERO,
            HINDI,
            HIRI_MOTU,
            CROATIAN,
            HUNGARIAN,
            IGBO,
            ICELANDIC,
            IDO,
            SICHUAN_YI,
            INUKTITUT,
            INTERLINGUE,
            INTERLINGUA,
            INDONESIAN,
            INUPIAQ,
            ITALIAN,
            JAVANESE,
            JAPANESE,
            KALAALLISUT,
            KANNADA,
            KASHMIRI,
            KANURI,
            KAZAKH,
            CENTRAL_KHMER,
            KIKUYU,
            KINYARWANDA,
            KIRGHIZ,
            KOMI,
            KONGO,
            KOREAN,
            KUANYAMA,
            KURDISH,
            LAO,
            LATIN,
            LATVIAN,
            LIMBURGAN,
            LINGALA,
            LITHUANIAN,
            LUXEMBOURGISH,
            LUBA_KATANGA,
            GANDA,
            MACEDONIAN,
            MARSHALLESE,
            MALAYALAM,
            MAORI,
            MARATHI,
            MALAY,
            MALAGASY,
            MALTESE,
            MONGOLIAN,
            NAURU,
            NAVAJO,
            SOUTH_NDEBELE,
            NORTH_NDEBELE,
            NDONGA,
            NEPALI,
            NORWEGIAN_NYNORSK,
            NORWEGIAN_BOKMAL,
            NORWEGIAN,
            CHICHEWA,
            OCCITAN,
            OJIBWA,
            ORIYA,
            OROMO,
            OSSETIAN,
            PANJABI,
            PERSIAN,
            PALI,
            POLISH,
            PORTUGUESE,
            PUSHTO,
            QUECHUA,
            ROMANSH,
            ROMANIAN,
            RUNDI,
            RUSSIAN,
            SANGO,
            SANSKRIT,
            SINHALA,
            SLOVAK,
            SLOVENIAN,
            NORTHERN_SAMI,
            SAMOAN,
            SHONA,
            SINDHI,
            SOMALI,
            SOTHO,
            SPANISH,
            SARDINIAN,
            SERBIAN,
            SWATI,
            SUNDANESE,
            SWAHILI,
            SWEDISH,
            TAHITIAN,
            TAMIL,
            TATAR,
            TELUGU,
            TAJIK,
            TAGALOG,
            THAI,
            TIBETAN,
            TIGRINYA,
            TONGA,
            TSWANA,
            TSONGA,
            TURKMEN,
            TURKISH,
            TWI,
            UIGHUR,
            UKRAINIAN,
            URDU,
            UZBEK,
            VENDA,
            VIETNAMESE,
            VOLAPUK,
            WELSH,
            WALLOON,
            WOLOF,
            XHOSA,
            YIDDISH,
            YORUBA,
            ZHUANG,
            ZULU
        )
    }
}
