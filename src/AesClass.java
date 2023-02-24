import java.io.*;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class AesClass {

    private SecretKeySpec secretKey;

    public AesClass(String reqSecretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        //바이트 배열로부터 SecretKey를 구축
        this.secretKey = new SecretKeySpec(reqSecretKey.getBytes("UTF-8"), "AES");
    }


    //AES ECB PKCS5Padding 암호화(Hex | Base64)
    public List<String> AesECBEncode(List<List<String>> plainTextList) throws Exception {

        List<String> encryptTextList = new ArrayList<String>();
        String PlainText = null;

        //Cipher 객체 인스턴스화(Java에서는 PKCS#5 = PKCS#7이랑 동일)
        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        //Cipher 객체 초기화
        c.init(Cipher.ENCRYPT_MODE, secretKey);
        //Encrpytion/Decryption
        for(int i=0; i<plainTextList.size(); i++) {
            List<String> line = plainTextList.get(i);
            for(int j=0; j<line.size(); j++) {
                System.out.print(line.get(j)+",");
                PlainText = line.get(0);
                System.out.print("제목:"+PlainText);

            }
            byte[] encrpytionByte = c.doFinal(PlainText.getBytes("UTF-8"));
            //Hex Encode
            encryptTextList.add(Hex.encodeHexString(encrpytionByte));
            System.out.println();
        }
        return encryptTextList;
        //Base64 Encode
//		return Base64.encodeBase64String(encrpytionByte);
    }

    //AES ECB PKCS5Padding 복호화(Hex | Base64)
    public String AesECBDecode(String encodeText) throws Exception {
        //Cipher 객체 인스턴스화(Java에서는 PKCS#5 = PKCS#7이랑 동일)
        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        //Cipher 객체 초기화
        c.init(Cipher.DECRYPT_MODE, secretKey);
        //Decode Hex
        byte[] decodeByte = Hex.decodeHex(encodeText.toCharArray());
        //Decode Base64
//		byte[] decodeByte = Base64.decodeBase64(encodeText);
        return new String(c.doFinal(decodeByte), "UTF-8");
    }

    public void writeCSV(List<String> encryptTextList) {
        File csv = new File("D:\\TitleList.csv");
        BufferedWriter bw = null; // 출력 스트림 생성
        try {
            bw = new BufferedWriter(new FileWriter(csv));
            // csv파일의 기존 값에 이어쓰려면 위처럼 true를 지정하고, 기존 값을 덮어쓰려면 true를 삭제한다

            for (int i = 0; i < encryptTextList.size(); i++) {

                String data = encryptTextList.get(i);
                System.out.println("Data다!!!!!"+data);
                //String aData = "";
                //aData = data[0] + "," + data[1] + "," + data[2] + "," + data[3];
                // 한 줄에 넣을 각 데이터 사이에 ,를 넣는다
                bw.write(data);
                // 작성한 데이터를 파일에 넣는다
                bw.newLine(); // 개행
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.flush(); // 남아있는 데이터까지 보내 준다
                    bw.close(); // 사용한 BufferedWriter를 닫아 준다
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class CsvUtils {
        public static List<List<String>> readToList(String path) throws IOException {
            List<List<String>> list = new ArrayList<List<String>>();
            File csv = new File(path);
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(csv));
                Charset.forName("UTF-8");
                String line = "";

                while((line=br.readLine()) != null) {
                    String[] token = line.split(",");
                    List<String> tempList = new ArrayList<String>(Arrays.asList(token));
                    list.add(tempList);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                    if(br != null) {br.close();}

            }
            return list;
        }
    }

    public static void main(String[] args) throws Exception {

        /*
         * 키 값의 바이트 수에 따라서 달라집니다.
         * AES128 : 키값 16bytes
         * AES192 : 키값 24bytes
         * AES256 : 키값 32bytes
         */

        String key_128 = "AES-128는 128비트(16바이트)의 키";//
        String key_192 = "AES-192는 192비트(24바이트)의 키";//
        String key_256 = "AES-256는 256비트(32바이트)의 키";//

        String iv = "aesiv12345678912";


        String path = "D:\\videodata.csv";
        List<List<String>> plainTextList = CsvUtils.readToList(path);

        AesClass ase_128_ecb = new AesClass(key_128);
        List<String> aes128EcbEncode = ase_128_ecb.AesECBEncode(plainTextList);
        ase_128_ecb.writeCSV(aes128EcbEncode);

        //String aes128EcbDeocde = ase_128_ecb.AesECBDecode(aes128EcbEncode);
        AesClass ase_192_ecb = new AesClass(key_192);
        List<String> aes192EcbEncode = ase_192_ecb.AesECBEncode(plainTextList);
        ase_192_ecb.writeCSV(aes192EcbEncode);
        //String aes192EcbDeocde = ase_192_ecb.AesECBDecode(aes192EcbEncode);
        AesClass ase_256_ecb = new AesClass(key_256);
        List<String> aes256EcbEncode = ase_256_ecb.AesECBEncode(plainTextList);
        ase_256_ecb.writeCSV(aes256EcbEncode);
        //String aes256EcbDeocde = ase_256_ecb.AesECBDecode(aes256EcbEncode);



    }

}