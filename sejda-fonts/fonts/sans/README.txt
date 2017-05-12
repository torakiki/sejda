# How to merge all fonts into a single file

Clone https://github.com/googlei18n/nototools
Follow their readme's setup

Update nototools/merge_fonts.py with our font list:

```
# file names to be merged
files = [
    # It's recommended to put NotoSans-Regular.ttf as the first element in the
    # list to maximize the amount of meta data retained in the final merged font.
    'NotoSans-Regular.ttf',
    'NotoSansBengali-Regular.ttf',
    'NotoKufiArabic-Regular.ttf',
    'NotoNaskhArabic-Regular.ttf',
    'NotoSansBengali-Regular.ttf',
    'NotoSansDevanagari-Regular.ttf',
    'NotoSansGeorgian-Regular.ttf',
    'NotoSansMalayalam-Regular.ttf',
    'NotoSansHebrew-Regular.ttf',
    'NotoSansTelugu-Regular.ttf',
    'NotoSansThai-Regular.ttf',
    'NotoSansSymbols-Regular.ttf',
    'NotoSansKhmer-Regular.ttf',
    'NotoSansEthiopic-Regular.ttf',
    'NotoSansGujarati-Regular.ttf',
    'NotoSansGurmukhi-Regular.ttf',
    'NotoSansTamil-Regular.ttf',
    'NotoSansMyanmar-Regular.ttf',
    'NotoSansSinhala-Regular.ttf',
    'NotoSansMongolian-Regular.ttf',
    'NotoSansKannada-Regular.ttf',
    'NotoSansOriya-Regular.ttf'
]
```

Execute the merge tool:

```
cd nototools
merge_fonts.py -d ~/dev/sejda/sejda-fonts/fonts/sans/ -o NotoSansMerged-Regular.ttf
```