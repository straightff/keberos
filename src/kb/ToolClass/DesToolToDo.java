package kb.ToolClass;
/*
 * package kb.des;
 * 
 * public class DesTool { private int[] E_Table = { // 扩展矩阵 32, 1, 2, 3, 4, 5,
 * 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18,
 * 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31,
 * 32, 1 }; private int[] PC1_Table = { // 密钥第一次置换矩阵 57, 49, 41, 33, 25, 17, 9,
 * 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44,
 * 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45,
 * 37, 29, 21, 13, 5, 28, 20, 12, 4 }; private int[] PC2_Table = { // 密钥第二次置换矩阵
 * 14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27,
 * 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56,
 * 34, 53, 46, 42, 50, 36, 29, 32 }; private int[] IP_Table = { // IP置换矩阵 58,
 * 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30,
 * 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59,
 * 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31,
 * 23, 15, 7 }; private int[] IPR_Table = { // 逆IP置换矩阵 40, 8, 48, 16, 56, 24,
 * 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5,
 * 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19,
 * 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25 };
 * private int[] P_Table = { // P 盒 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23,
 * 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25
 * }; private int[][][] S_Box = { // 8个S盒 三维数组 { // S1 { 14, 4, 13, 1, 2, 15,
 * 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 }, { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12,
 * 11, 9, 5, 3, 8 }, { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 }, {
 * 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 } }, { // S2 { 15, 1, 8,
 * 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 }, { 3, 13, 4, 7, 15, 2, 8, 14,
 * 12, 0, 1, 10, 6, 9, 11, 5 }, { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3,
 * 2, 15 }, { 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 } }, { // S3
 * { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 }, { 13, 7, 0, 9, 3,
 * 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 }, { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2,
 * 12, 5, 10, 14, 7 }, { 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }
 * }, { // S4 { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 }, { 13, 8,
 * 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 }, { 10, 6, 9, 0, 12, 11, 7,
 * 13, 15, 1, 3, 14, 5, 2, 8, 4 }, { 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12,
 * 7, 2, 14 } }, { // S5 { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9
 * }, { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 }, { 4, 2, 1, 11,
 * 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 }, { 11, 8, 12, 7, 1, 14, 2, 13, 6,
 * 15, 0, 9, 10, 4, 5, 3 } }, { // S6 { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4,
 * 14, 7, 5, 11 }, { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 }, {
 * 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 }, { 4, 3, 2, 12, 9, 5,
 * 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 } }, { // S7 { 4, 11, 2, 14, 15, 0, 8, 13,
 * 3, 12, 9, 7, 5, 10, 6, 1 }, { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15,
 * 8, 6 }, { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 }, { 6, 11,
 * 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 } }, { // S8 { 13, 2, 8, 4, 6,
 * 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 }, { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5,
 * 6, 11, 0, 14, 9, 2 }, { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8
 * }, { 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } } };
 * 
 * // 密文异或 private void Xor(int[] a, int[] b, int len) { for (int i = 0; i <
 * len; ++i) { a[i] = a[i] ^ b[i]; } }
 * 
 * // 64位ip置换 private void IP64(int input[], int output[], int table[]) { for
 * (int i = 0; i < 64; ++i) { output[i] = input[table[i] - 1]; } }
 * 
 * // E扩充置换 private void E(int input[], int output[], int table[]) { for (int i
 * = 0; i < 48; ++i) { output[i] = input[table[i] - 1]; } }
 * 
 * // 密钥置换Pc_1 private void PC_1(int input[], int output[], int table[]) { for
 * (int i = 0; i < 56; i++) { output[i] = input[table[i] - 1]; } }
 * 
 * // PC_2 private void PC_2(int input[], int output[], int table[]) { for (int
 * i = 0; i < 48; i++) { output[i] = input[table[i] - 1]; } }
 * 
 * // p盒置换 private void P(int input[], int output[], int table[]) { for (int i =
 * 0; i < 32; i++) { output[i] = input[table[i] - 1]; } }
 * 
 * // S盒压缩变换 private void S(int input[], int output[], int table[][][]) { int i
 * = 0; int j = 0; int[] INT = new int[8];
 * 
 * // 第一个for循环得到8组6bit数，通过对应的S盒的行列对应，得到8个的十进制数 for (; i < 48; i += 6) { INT[j] =
 * table[j][(input[i] << 1) + (input[i + 5])][(input[i + 1] << 3) + (input[i +
 * 2] << 2) + (input[i + 3] << 1) + (input[i + 4])]; j++; }
 * 
 * // 第二个for循环将这8个十进制数转换为二进制存储 for (j = 0; j < 8; j++) { for (i = 0; i < 4; i++)
 * { output[4 * j + 3 - i] = (INT[j] >> i) & 1; } } }
 * 
 * // 秘钥循环左移 void leftShift(int input[], int output[], int leftcount) { int i;
 * int len = 28;// 因为不是位运算，所以可以不用unsigned for (i = 0; i < len; i++) { output[i]
 * = input[(i + leftcount) % len]; } };
 * 
 * // 完成DES算法轮变换,就是f函数的内部实现，就是一个异或而已 private void F_func(int input[], int
 * output[], int subkey[]) { int len = 48; int[] temp = { 0 }; int[] temp_1 = {
 * 0 }; E(input, temp, E_Table);// 扩充置换32->48 Xor(temp, subkey, len); // 与密钥异或加密
 * S(temp, temp_1, S_Box);// S盒压缩48->32 P(temp_1, output, P_Table);// P盒置换位数不变 }
 * 
 * // 生成16轮子钥 void subkey_make(int input[], int subkey[][]) { int pc[] = { 0 };
 * int[][] pc_gp = { { 0 } }; PC_1(input, pc, PC1_Table); // 第一次置换
 * 
 * int[] c = new int[28]; int[] d = new int[28]; for (int i = 0; i < 28; ++i) //
 * 将56位密钥分为左右两部分 { c[i] = pc[i]; d[i] = pc[i + 28]; }
 * 
 * int leftcount = 0; int[][] left_c = { { 0 } }; int[][] left_d = { { 0 } };
 * for (int i = 0; i < 16; i++) { if (i == 0 || i == 1 || i == 8 || i == 15)//
 * 左移一位(查表可知) { leftcount += 1; leftShift(c, left_c[i], leftcount); leftShift(d,
 * left_d[i], leftcount); } else// 左移两位 { leftcount += 2; leftShift(c,
 * left_c[i], leftcount); leftShift(d, left_d[i], leftcount); } }
 * 
 * // 两边处理完之后将密钥合并 for (int i = 0; i < 16; ++i) { for (int j = 0; j < 28; ++j) {
 * pc_gp[i][j] = left_c[i][j]; pc_gp[i][j + 28] = left_d[i][j]; } }
 * 
 * // 密钥第二次置换 56->48 for (int i = 0; i < 16; ++i) { PC_2(pc_gp[i], subkey[i],
 * PC2_Table); } }
 * 
 * private void StringToBit(String input, int output[], int bits) { for (int i =
 * 0; i < input.length(); ++i) { for (int j = 0; j < 8; ++j) { output[8 * i + 7
 * - j] = (input.charAt(i) >> j) & 1; } } }
 * 
 * private void des_encode(int input[], int key[], int output[]) { int IpShift[]
 * = { 0 };
 * 
 * // IP初始置换 IP64(input, IpShift, IP_Table);
 * 
 * // 生成16轮密钥 int[][] subkeys = { { 0 } }; subkey_make(key, subkeys);
 * 
 * // 密文分为左右各32bit int[][] le = new int[17][32]; int[][] rg = new int[17][32];
 * for (int i = 0; i < 32; ++i) { le[0][i] = IpShift[i]; rg[0][i] = IpShift[32 +
 * i]; }
 * 
 * // 前15轮 for (int i = 1; i <= 15; ++i) { for (int j = 0; j < 32; ++j) {
 * le[i][j] = rg[i - 1][j]; // 下一轮left=right } F_func(rg[i - 1], rg[i],
 * subkeys[i - 1]); Xor(rg[i], le[i - 1], 32); // 每一轮最后一步生成下一轮right=right^left }
 * 
 * // 最后一轮 要将 左右调换位置 for (int i = 0; i < 32; ++i) { rg[16][i] = rg[15][i]; }
 * F_func(rg[15], le[16], subkeys[15]); Xor(le[16], le[15], 32);
 * 
 * // 合并左右 int outp_temp[] = { 0 }; for (int i = 0; i < 32; ++i) { outp_temp[i]
 * = le[16][i]; outp_temp[32 + i] = rg[16][i]; } IP64(outp_temp, output,
 * IPR_Table); }
 * 
 * // 把INT转换为CHAR 2->10 private void BitToDec(int input[], int output[], int
 * bits) { int i, j; for (j = 0; j < 8; j++) { output[j] = 0; for (i = 0; i < 8;
 * i++) { output[j] += input[i + j * 8] << (7 - i); // 转换成十进制 } } }
 * 
 * private void encode(String plainText, String key) { // string plainText;
 * 
 * int[] pt64 = new int[64]; int[] ct64 = new int[64]; // string key; int[]
 * key64 = new int[64];
 * 
 * int length = plainText.length() * 8; int gp = 0;
 * 
 * // 判断有几组明文 if (length % 64 == 0) gp = length / 64; else gp = length / 64 + 1;
 * 
 * int[] plainT = new int[64]; int[] cipherT = new int[64];
 * StringToBit(plainText, plainT, 64); StringToBit(key, key64, 8); // 不足64一组的补零
 * for (int i = length; i < gp * 64; ++i) { plainT[i] = 0; }
 * 
 * // 分组加密 System.out.println("the length is");
 * System.out.println("the group num is"); int gp_num = 0; for (int i = 0; i <
 * gp * 64; ++i) { pt64[i % 64] = plainT[i]; if ((i + 1) % 64 == 0) {
 * des_encode(pt64, key64, ct64); for (int j = 0; j < 64; ++j) { cipherT[gp_num
 * * 64 + j] = ct64[j]; } ++gp_num; } }
 * System.out.println("the plain text is :"); for (int i = 0; i < gp * 64; ++i)
 * { System.out.println(plainT[i]); if ((i + 1) % 8 == 0) System.out.println();
 * } System.out.println("the cipher text is :"); for (int i = 0; i < gp * 64;
 * ++i) { System.out.println(cipherT[i]); if ((i + 1) % 8 == 0)
 * System.out.println(); } int[] cT = new int[256]; BitToDec(cipherT, cT, 8);
 * System.out.println("the cipher text in string is :"); for (int i = 0; i < 8;
 * ++i) { System.out.println(cT[i]); } System.out.println(); }
 * 
 * private void decode(String cipherText,String key) { // string cipherText;
 * //存储十进制密文 int[] ciText = new int[256]; //存储密文转化数 int[] pt64 = new int[64];
 * //temp plainT int[] ct64 = new int[64]; //temp ciphert // string key; int[]
 * key64 = new int[64];//64位key
 * 
 * int gp=0;
 * 
 * System.out.println("ciText is :"); for (int i = 0; i < num; i++) {
 * System.out.println(ciText[i]+" "); if((i+1)%8==0) System.out.print(" ");
 * if((i+1)%16==0) System.out.println(); } System.out.println(); gp=num/8;
 * 
 * int plainT1[256]={0}; int cipherT1[256]={0};
 * 
 * DecToBit(ciText,cipherT1,num); for (int i = 0; i < gp*64; i++) {
 * cout<<cipherT1[i]; if((i+1)%8==0) cout<<" "; if((i+1)%16==0) cout<<endl; }
 * StringToBit(key,key64,8); cout<<"key64 :"<<endl; for (int i = 0; i < 64; i++)
 * { cout<<key64[i]; if((i+1)%8==0) cout<<" "; if((i+1)%16==0) cout<<endl; } int
 * gp_num=0; for(int i=0;i<gp*64;++i) { ct64[i%64]=cipherT1[i]; if((i+1)%64==0)
 * { cout<<"flag 11111"<<endl; des_decode(ct64,key64,pt64); for(int
 * j=0;j<64;++j) { plainT1[gp_num*64+j]=pt64[j]; } ++gp_num; } }
 * cout<<"the plain text is :"<<endl; for(int i=0;i<gp*64;++i) {
 * cout<<plainT1[i]; if((i+1)%8==0) cout<<" "; if((i+1)%16==0) cout<<endl; }
 * cout<<"the cipher text is :"<<endl; for(int i=0;i<gp*64;++i) {
 * cout<<cipherT1[i]; if((i+1)%8==0) cout<<" "; if((i+1)%16==0) cout<<endl; }
 * int cT[256]; BitToDec(plainT1,cT,8);
 * cout<<"the plain text in string is :"<<endl; for(int i=0;i<8;++i) {
 * printf("%c ",cT[i]); } cout<<endl; delete[] plainT1;
 * 
 * } }
 */