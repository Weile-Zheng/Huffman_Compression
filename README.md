# Huffman Compression
***

Huffman Compression Command for zsh

Get Started:

Open the terminal and execute the following commands

```Bash
cd ~
git clone https://github.com/Weile-Zheng/Huffman_Compression.git
echo "export PATH=\$PATH:$(pwd)/Huffman_Compression/bin" >> .zprofile
chmod ugo+x ~/Huffman_Compression/bin/huffman 

```

Test Installation
```Bash
huffman --version
```

Usage
```
huffman -[compress/decompress] <InputFile> (Optional) -o [OutputFileName]
#Ex:
huffman -compress text.txt
```

