package mantvydas.volumr;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by baranauskasm on 11/12/2015.
 */
public class TLSSocketFactory {
    Context context;

    public SSLSocketFactory createSSLSocketFactory(Context context, String pathToCertificate) {
        this.context = context;
        Certificate certificate = new CertificateGenerator().generateCertificateFromFile(pathToCertificate);
        TrustManager[] trustManagers = new CertificateTrustManager().createTrustManagerFromCertificate(certificate);
        SSLSocketFactory sslSocketFactory = getSSLSocketFactory(trustManagers);
        return sslSocketFactory;
    }

    private SSLSocketFactory getSSLSocketFactory(TrustManager[] trustManager) {
        SSLContext sslContext = null;

        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustManager, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext.getSocketFactory();
    }




    private class CertificateTrustManager {

        public KeyStore addCertificateToKeyStore(Certificate certificate) {
            KeyStore keyStore = null;

            try {
                String keyStoreType = KeyStore.getDefaultType();
                keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", certificate);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (java.security.cert.CertificateException e) {
                e.printStackTrace();
            }
            return keyStore;
        }

        public TrustManager[] addTrustedKeyStore(KeyStore keyStore) {
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = null;

            try {
                trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
                trustManagerFactory.init(keyStore);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            return trustManagerFactory.getTrustManagers();
        }

        private TrustManager[] createTrustManagerFromCertificate(Certificate certificate) {
            KeyStore keyStore = addCertificateToKeyStore(certificate);
            TrustManager[] trustManagers = addTrustedKeyStore(keyStore);
            return trustManagers;
        }
    }



    public class CertificateGenerator {

        private CertificateFactory getCertificateFactory(String instanceName) {
            CertificateFactory certificateFactory = null;
            String defaultInstance = "X.509";

            if (instanceName == null) {
                instanceName = defaultInstance;
            }

            try {
                certificateFactory = CertificateFactory.getInstance(instanceName);
            } catch (java.security.cert.CertificateException e) {
                e.printStackTrace();
            }
            return certificateFactory;
        }

        private BufferedInputStream readCertificateFile(String pathToCertificate) {
            BufferedInputStream bufferedInputStream = null;

            try {
                InputStream inputStream = context.getAssets().open(pathToCertificate);
                bufferedInputStream = new BufferedInputStream(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {

            }
            return bufferedInputStream;
        }

        private Certificate generateCertificateFromStream(CertificateFactory certificateFactory, BufferedInputStream certificateStream) {
            Certificate certificate = null;
            try {
                certificate = certificateFactory.generateCertificate(certificateStream);
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            return certificate;
        }

        public Certificate generateCertificateFromFile(String pathToCertificate) {
            CertificateFactory certificateFactory = getCertificateFactory(null);
            BufferedInputStream certificateStream = readCertificateFile(pathToCertificate);
            Certificate certificate = generateCertificateFromStream(certificateFactory, certificateStream);
            return certificate;
        }
    }
}
