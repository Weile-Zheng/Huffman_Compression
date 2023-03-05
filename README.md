# Huffman Compression
***

Huffman Compression Command for zsh

Get Started:

```Bash
cd ~
git clone https://github.com/Weile-Zheng/Huffman_Compression.git
echo "export PATH=\$PATH:$(pwd)/Huffman_Compression/bin" >> .zprofile
chmod ugo+x ~/Huffman_Compression/bin/huffman 

```

Usage
```
huffman -[compress/decompress] <InputFile> (Optional) -o [OutputFileName]
#Ex:
huffman -compress text.txt
```

*Note that after compression the original file will be deleted. To keep a copy of the original file, use huffman -k
