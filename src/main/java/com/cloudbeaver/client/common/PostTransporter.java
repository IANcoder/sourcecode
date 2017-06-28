package com.cloudbeaver.client.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

interface PostTransporter {
	void setUrlConnectionProperty(URLConnection urlConnection, String key, String value);
	void upload(OutputStream out, String content, long startIdx) throws IOException;
	void download(InputStream in, StringBuilder sb) throws IOException;
}

abstract class AbstractPostUploader implements PostTransporter{
	private static final PostStringTransporter postStringTransporter = new PostStringTransporter();
	private static final PostFileTransporter postFileTransporter = new PostFileTransporter();

	public static final String STRING_TRANSPORTER = "PostStringUploader";
	public static final String BIG_FILE_TRANSPORTER = "PostFileUploader";

	static final int READ_BUFFER_SIZE = 1024 * 10;
	static final int WRITE_BUFFER_SIZE = 1024 * 10;
	static final int LOCAL_CHUNK_SIZE = 1024 * 1024;

	public static final PostTransporter getPostTranspoter(String uploaderName){
		switch (uploaderName) {
		case STRING_TRANSPORTER:
			return postStringTransporter;

		case BIG_FILE_TRANSPORTER:
			return postFileTransporter;

		default:
			assert(false);
		}

		return null;
	}
}

class PostStringTransporter extends AbstractPostUploader{
	@Override
	public void setUrlConnectionProperty(URLConnection urlConnection, String key, String value) {
		if (urlConnection instanceof HttpsURLConnection) {
			((HttpsURLConnection)urlConnection).setRequestProperty(key, value);
		}else{
			((HttpURLConnection)urlConnection).setRequestProperty(key, value);
		}
	}

	@Override
	public void upload(OutputStream out, String content, long startIdx) throws IOException {
        PrintWriter pWriter = new PrintWriter(out);
        pWriter.write(startIdx > 0 ? content.substring((int)startIdx) : content);
        pWriter.flush();
	}

	@Override
	public void download(InputStream in, StringBuilder stringBuffer) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
        	stringBuffer.append(line);
        }
	}
}

class PostFileTransporter extends AbstractPostUploader{
	@Override
	public void setUrlConnectionProperty(URLConnection urlConnection, String key, String value) {
		if (urlConnection instanceof HttpsURLConnection) {
//	      urlConnection.setUseCaches(false);
			((HttpsURLConnection)urlConnection).setChunkedStreamingMode(LOCAL_CHUNK_SIZE);
		} else {
			((HttpURLConnection)urlConnection).setChunkedStreamingMode(LOCAL_CHUNK_SIZE);
		}
	}

	@Override
	public void upload(OutputStream out, String fileName, long offset) throws IOException {
		try( RandomAccessFile in = new RandomAccessFile(fileName, "r") ){
			if (offset > 0) {
				in.seek(offset);
			}

			byte[] readBuf = new byte[READ_BUFFER_SIZE];
			int len = 0;
			while ((len = in.read(readBuf)) != -1) {
				out.write(readBuf, 0, len);
				out.flush();
			}
		}
	}

	@Override
	public void download(InputStream in, StringBuilder localFile) throws IOException{
		try (RandomAccessFile file = new RandomAccessFile(localFile.toString(), "rw")) {
			file.seek(file.length());
			byte[] readBuf = new byte[WRITE_BUFFER_SIZE];
			int len = 0;
			while ((len = in.read(readBuf)) != -1) {
				file.write(readBuf, 0, len);
			}
		}
	}
}