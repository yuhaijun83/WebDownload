package cn.com.yuhaijun;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Download {

	public static void main(String[] args) throws InterruptedException, IOException {

		String strDownloadURL = null;
		String strDownloadFileName = null;
		String strDownloadPath = null;
		String strDownloadSubPath = null;

		String strDownloadFileNameDef = "media_%num%.ts";
		String strDownloadPathDef = "C:\\Downloads\\";

		int iSize = 0;
		if (null != args) {
			iSize = args.length;
		}
		if (iSize == 0) {
			display();
			System.exit(-1);
		} else if (iSize == 1) {
			strDownloadURL = args[0];
			strDownloadFileName = strDownloadFileNameDef;
			strDownloadPath = strDownloadPathDef;

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
			strDownloadSubPath = sdf.format(calendar.getTime()).concat("\\");
		} else if (iSize == 2) {
			strDownloadURL = args[0];
			strDownloadFileName = args[1].concat("_%num%.ts");
			strDownloadPath = strDownloadPathDef;
			strDownloadSubPath = args[1].concat("\\");
		} else if (iSize == 3) {
			strDownloadURL = args[0];
			strDownloadFileName = args[1].concat("_%num%.ts");
			strDownloadPath = args[2].concat("\\");
			strDownloadSubPath = args[1].concat("\\");
		} else {
			display();
			System.exit(-1);
		}

		boolean bDownRet = false;

		int iBegin = 0;
		int iEnd = 999999999; // 999999999
		int iErrorCnt = 5;

		String strSubNum = null;
		String strURL = null;
		String strFileName = null;
		for (int i = iBegin;; i++) {
			if (i == iEnd) {
				break;
			}

			strSubNum = String.valueOf(i);
			if (strSubNum.length() == 1) {
				strSubNum = "0000".concat(strSubNum);
			} else if (strSubNum.length() == 2) {
				strSubNum = "000".concat(strSubNum);
			} else if (strSubNum.length() == 3) {
				strSubNum = "00".concat(strSubNum);
			} else if (strSubNum.length() == 4) {
				strSubNum = "0".concat(strSubNum);
			} else {
				;
			}

			strFileName = strDownloadFileName.replaceAll("%num%", strSubNum);
			strURL = strDownloadURL.replaceAll("%num%", String.valueOf(i));

			for (int j = 0; j < iErrorCnt; j++) {
				if (j > 0) {
					Thread.sleep(1000);
				}

				try {
					bDownRet = false;
					downloadNet(strURL, strDownloadPath.concat(strDownloadSubPath), strFileName);
					bDownRet = true;
				} catch (Exception e) {
					if (e instanceof FileNotFoundException) {
						System.out.println("Download Successful !!!");
						break;
					} else if (e instanceof IOException) {
						if (i == 0) {
							bDownRet = true;
							break;
						} else {
							System.err.println(e.getMessage());
						}
					} else {
						System.err.println(e.getMessage());
					}
				} finally {
					if (bDownRet == true) {
						break;
					}
				}
			}

			if (bDownRet == false) {
				break;
			}
		}
		
//		Runtime.getRuntime().exec(("cmd /c copy /b ")
//				.concat(strDownloadPath).concat(strDownloadSubPath).concat(" ")
//				.concat(strDownloadPath).concat(strFileName.replaceAll("/", "")).concat(".ts"));

//		Runtime.getRuntime().exec("cmd /c rmdir /S /Q ".concat(strDownloadPath).concat(strDownloadSubPath));

		Runtime.getRuntime().exec("explorer.exe ".concat(strDownloadPath.concat(strDownloadSubPath)));

	}

	private static void downloadNet(String parmURL, String filePath, String fileName)
			throws FileNotFoundException, IOException {

		int bytesum = 0;
		int byteread = 0;

		InputStream inStream = null;
		FileOutputStream fs = null;

		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			;
		}

		try {
			URL url = new URL(parmURL);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			inStream = conn.getInputStream();
			fs = new FileOutputStream(filePath.concat(fileName));
			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
			System.out.println(filePath.concat(fileName).concat("  --->  download OK ! (Size:")
					.concat(String.valueOf(bytesum).concat(" Bytes)")));
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			System.out.println(filePath.concat(fileName).concat("  --->  download Error ! (Size:")
					.concat(String.valueOf(bytesum).concat(" Bytes)")));
			throw e;
		} finally {
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void display() {
		System.out.println("Download :");
		System.out.println();
		System.out.println("    java -jar Download.jar <URL> [File Name] [Download Path]");
		System.out.println();
		System.out.println("    java -jar Download.jar http://www.baidu.com/media_%num%.ts 醉玲珑 C:\\Download");
		System.out.println();
		// System.out.println("C:\\Download\\醉玲珑\\醉玲珑.ts.mp4");
	}

}
