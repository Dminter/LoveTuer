package com.zncm.utils.http.core;


import com.zncm.utils.exception.CheckedExceptionHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Random;


class MultipartEntity implements HttpEntity {
    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();
    private String boundary = null;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    boolean isSetLast = false;
    boolean isSetFirst = false;

    public MultipartEntity() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        this.boundary = buf.toString();
    }

    public void writeFirstBoundaryIfNeeds() {
        if (!isSetFirst) {
            try {
                out.write(("--" + boundary + "\r\n").getBytes());
            } catch (final IOException e) {
                CheckedExceptionHandler.handleException(e);
            }
        }
        isSetFirst = true;
    }

    public void writeLastBoundaryIfNeeds() {
        if (isSetLast) {
            return;
        }
        try {
            out.write(("\r\n--" + boundary + "--\r\n").getBytes());
        } catch (final IOException e) {
            CheckedExceptionHandler.handleException(e);
        }
        isSetLast = true;
    }

    public void addPart(final String key, final String value) {
        writeFirstBoundaryIfNeeds();
        try {
            out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
            out.write(value.getBytes());
            out.write(("\r\n--" + boundary + "\r\n").getBytes());
        } catch (final IOException e) {
            CheckedExceptionHandler.handleException(e);
        }
    }

    private void addPart(final String key, final String fileName, final InputStream fin, final boolean isLast,
                         RequestParams.UploadProgressListener listener) {
        addPart(key, fileName, fin, "application/octet-stream", isLast, listener);
    }

    public void addPart(final String key, final String fileName, final InputStream fin, String type, boolean isLast,
                        RequestParams.UploadProgressListener listener) {
        if (type == null) {
            type = "application/octet-stream";
        }
        writeFirstBoundaryIfNeeds();
        try {
            type = "Content-Type: " + type + "\r\n";
            out.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n")
                    .getBytes());
            out.write(type.getBytes());
            out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());
            final byte[] tmp = new byte[4096];
            int l = 0;
            int n = 0;
            int available = fin.available();
            // L.e("fin" + fin.available());
            while ((l = fin.read(tmp)) != -1) {
                out.write(tmp, 0, l);
                n += l;
                // L.e(n + "");
                // L.e("%" + n / (float) available);
                if (listener != null) {
                    // 进度格式化为 XX
                    int progress = Integer.parseInt(new DecimalFormat("##").format((n / (float) available) * 100));
                    listener.publishProgress(progress);
                }
            }
            // !isLast
            if (!isLast)
                out.write(("\r\n--" + boundary + "\r\n").getBytes());
            out.flush();
        } catch (final IOException e) {
            CheckedExceptionHandler.handleException(e);
        } finally {
            try {
                fin.close();
            } catch (final IOException e) {
                CheckedExceptionHandler.handleException(e);
            }
        }
    }

    public void addPart(final String key, final File value, final boolean isLast, RequestParams.UploadProgressListener listener) {
        try {
            addPart(key, value.getName(), new FileInputStream(value), isLast, listener);
        } catch (final FileNotFoundException e) {
            CheckedExceptionHandler.handleException(e);
        }
    }

    @Override
    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return out.toByteArray().length;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        outstream.write(out.toByteArray());
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public void consumeContent() throws IOException, UnsupportedOperationException {
        if (isStreaming()) {
            CheckedExceptionHandler.handleException(new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()"));
        }
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        return new ByteArrayInputStream(out.toByteArray());
    }
}
