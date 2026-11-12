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
    'NotoNaskhArabic-Regular.ttf',
    'NotoSansDevanagari-Regular.ttf',
    'NotoSansGeorgian-Regular.ttf',
    'NotoSansMalayalam-Regular.ttf',
    'NotoSansHebrew-Regular.ttf',
    'NotoSansTelugu-Regular.ttf',
    'NotoSansThai-Regular.ttf',
    'NotoSansSymbols-Regular.ttf',
    'NotoSansSymbols2-Regular.ttf',
    'NotoSansKhmer-Regular.ttf',
    'NotoSansEthiopic-Regular.ttf',
    'NotoSansGujarati-Regular.ttf',
    'NotoSansGurmukhi-Regular.ttf',
    'NotoSansTamil-Regular.ttf',
    'NotoSansMyanmar-Regular.ttf',
    'NotoSansSinhala-Regular.ttf',
    'NotoSansMongolian-Regular.ttf',
    'NotoSansKannada-Regular.ttf',
    'NotoSansOriya-Regular.ttf',
    'NotoSansThaana-Regular.ttf',
    'NotoSansCJKPunct-Regular.ttf',
    'NotoSansArmenian-Regular.ttf'
]
```

Execute the merge tool:

```
cd nototools
merge_fonts.py -d ~/dev/sejda/sejda-fonts/fonts/sans/ -o NotoSansMerged-Regular.ttf
```

The `NotoSansCJKPunct-Regular.ttf` font will be created with:

```
# NotoSansJP[wght].ttf from https://github.com/google/fonts/tree/main/ofl/notosansjp

cd ~/Downloads
fonttools varLib.instancer "NotoSansJP[wght].ttf" wght=400 -o NotoSansJP-Regular.ttf

pyftsubset NotoSansJP-Regular.ttf \
  --unicodes="U+3000-303F,U+FF01-FF60,U+FFE0-FFE6" \
  --no-hinting \
  --drop-tables+=BASE,GDEF,GSUB,GPOS,vhea,vmtx,VORG,STAT,meta \
  --output-file=NotoSansCJKPunct-Regular.ttf
fonttools ttLib.scaleUpem NotoSansCJKPunct-Regular.ttf 2048 --output-file NotoSansCJKPunct-Regular.ttf

mv NotoSansCJKPunct-Regular.ttf ~/dev/sejda/sejda-fonts/fonts/sans/

fonttools varLib.instancer "NotoSansJP[wght].ttf" wght=700 -o NotoSansJP-Bold.ttf
pyftsubset NotoSansJP-Bold.ttf \
  --unicodes="U+3000-303F,U+FF01-FF60,U+FFE0-FFE6" \
  --no-hinting \
  --drop-tables+=BASE,GDEF,GPOS,vhea,vmtx,VORG,STAT,meta \
  --layout-features="" \
  --output-file=NotoSansCJKPunct-Bold.ttf

mv NotoSansCJKPunct-Bold.ttf ~/dev/sejda/sejda-fonts/fonts/sans/

```  
